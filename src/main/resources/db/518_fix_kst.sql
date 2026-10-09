-- #518 맥미니 전환 전에 UTC로 저장된 시간을 한국 시간(+9시간)으로 보정한다.
--
-- 전환 전 서버(.github/workflows/Dockerfile)는 UTC, 전환 후 서버(루트 Dockerfile, TZ=Asia/Seoul)는
-- 한국 시간으로 LocalDateTime.now()를 저장했다. 그래서 전환 전 값은 UTC 기준 @cutover 이전이고,
-- 전환 후 값은 한국 시간 기준 @cutover + 9시간 이후다. 컬럼별로 `값 < @cutover` 조건을 걸면
-- 전환 전 값만 고를 수 있다. (한 행 안에서도 대여는 전환 전, 반납은 전환 후일 수 있다)
--
-- 앱과 같은 세션 시간대로 실행한다. (SET time_zone 을 바꾸지 않는다. TIMESTAMP 컬럼이 섞여 있음)
-- 실행 전에 DB를 백업하고, toKst()를 없앤 코드를 배포한 직후에 실행한다.

-- 맥미니 서버가 운영 DB에 처음 쓴 시각(UTC). 첫 mac-deploy 실행은 2026-09-30 03:13:51 UTC(12:13 KST).
-- 예전 서버를 내린 시각을 확인해서 맞게 고친다.
SET @cutover = '2026-09-30 03:13:51';

-- 1. 확인: 전환 후 대여 건이 한국 시간으로 저장됐는지 (슬랙 대여 알림 시각과 비교)
SELECT id, rented_at, returned_at FROM history WHERE rented_at >= @cutover ORDER BY id LIMIT 20;

-- 2. 확인: UTC 전환 시각과 한국 시간 전환 시각 사이에는 값이 없어야 한다. 있으면 @cutover가 틀렸거나 두 서버가 동시에 돌았던 것
SELECT 'history.rented_at' AS col, COUNT(*) FROM history WHERE rented_at >= @cutover AND rented_at < @cutover + INTERVAL 9 HOUR
UNION ALL SELECT 'history.returned_at', COUNT(*) FROM history WHERE returned_at >= @cutover AND returned_at < @cutover + INTERVAL 9 HOUR
UNION ALL SELECT 'history.paid_at', COUNT(*) FROM history WHERE paid_at >= @cutover AND paid_at < @cutover + INTERVAL 9 HOUR
UNION ALL SELECT 'history.refunded_at', COUNT(*) FROM history WHERE refunded_at >= @cutover AND refunded_at < @cutover + INTERVAL 9 HOUR;

-- 3. 보정
START TRANSACTION;

UPDATE history
SET rented_at   = IF(rented_at   < @cutover, rented_at   + INTERVAL 9 HOUR, rented_at),
    returned_at = IF(returned_at < @cutover, returned_at + INTERVAL 9 HOUR, returned_at),
    paid_at     = IF(paid_at     < @cutover, paid_at     + INTERVAL 9 HOUR, paid_at),
    refunded_at = IF(refunded_at < @cutover, refunded_at + INTERVAL 9 HOUR, refunded_at)
WHERE rented_at < @cutover OR returned_at < @cutover OR paid_at < @cutover OR refunded_at < @cutover;

-- updated_at에 ON UPDATE NOW()가 걸려 있어서, 같은 문장에서 직접 지정해야 현재 시각으로 덮어써지지 않는다
UPDATE `user`
SET created_at = IF(created_at < @cutover, created_at + INTERVAL 9 HOUR, created_at),
    updated_at = IF(updated_at < @cutover, updated_at + INTERVAL 9 HOUR, updated_at),
    deleted_at = IF(deleted_at < @cutover, deleted_at + INTERVAL 9 HOUR, deleted_at)
WHERE created_at < @cutover OR updated_at < @cutover OR deleted_at < @cutover;

UPDATE umbrella
SET created_at = created_at + INTERVAL 9 HOUR
WHERE created_at < @cutover;

UPDATE black_list
SET blocked_at = blocked_at + INTERVAL 9 HOUR
WHERE blocked_at < @cutover;

-- locker.last_access는 1분 재요청 제한에만 쓰여서 보정하지 않는다

COMMIT;
