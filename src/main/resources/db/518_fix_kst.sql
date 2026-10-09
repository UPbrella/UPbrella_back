-- #518 맥미니 전환 전에 UTC로 저장된 시간을 한국 시간(+9시간)으로 보정한다.
--
-- 전환 전 서버(.github/workflows/Dockerfile)는 UTC, 전환 후 서버(루트 Dockerfile, TZ=Asia/Seoul)는
-- 한국 시간으로 LocalDateTime.now()를 저장했다. 그래서 전환 전 값은 UTC 기준 @cutover 이전이고,
-- 전환 후 값은 한국 시간 기준 @cutover + 9시간 이후다. 컬럼별로 `값 < @cutover` 조건을 걸면
-- 전환 전 값만 고를 수 있다. (한 행 안에서도 대여는 전환 전, 반납은 전환 후일 수 있다)
--
-- 다시 실행해도 9시간이 두 번 더해지지 않게, 원래 값을 스냅샷 테이블(kst_fix_518_*)에 저장하고
-- "지금 값이 스냅샷 값과 같을 때만" 보정한다. 스냅샷은 백업도 겸한다.
-- 스냅샷 테이블을 지우고 다시 만들면 보정된 값이 원래 값으로 저장되므로, 보정이 끝난 뒤 다시 만들지 않는다.
--
-- toKst()를 없앤 코드를 배포한 직후에 실행한다.
--
-- umbrella.created_at(TIMESTAMP)은 보정하지 않는다. 어떤 API 응답에도 나가지 않고, TIMESTAMP라 보정이 필요한지가
-- 기록 당시 DB 연결의 세션 시간대에 달려 있어 저장소만으로는 판단할 수 없다.
-- locker.last_access도 1분 재요청 제한에만 쓰여서 보정하지 않는다.

-- 맥미니 서버가 운영 DB에 처음 쓴 시각(UTC). 첫 mac-deploy 실행은 2026-09-30 03:13:51 UTC(12:13 KST).
-- 예전 서버를 내린 시각을 확인해서 맞게 고친다. 다시 실행할 때도 같은 값을 쓴다.
SET @cutover = '2026-09-30 03:13:51';

-- 1. 확인
-- 전환 후 대여 건이 한국 시간으로 저장됐는지 (슬랙 대여 알림 시각과 비교)
SELECT id, rented_at, returned_at FROM history WHERE rented_at >= @cutover ORDER BY id LIMIT 20;

-- UTC 전환 시각과 한국 시간 전환 시각 사이에는 값이 없어야 한다. 있으면 @cutover가 틀렸거나 두 서버가 동시에 돌았던 것
SELECT 'history.rented_at' AS col, COUNT(*) FROM history WHERE rented_at >= @cutover AND rented_at < @cutover + INTERVAL 9 HOUR
UNION ALL SELECT 'history.returned_at', COUNT(*) FROM history WHERE returned_at >= @cutover AND returned_at < @cutover + INTERVAL 9 HOUR
UNION ALL SELECT 'history.paid_at', COUNT(*) FROM history WHERE paid_at >= @cutover AND paid_at < @cutover + INTERVAL 9 HOUR
UNION ALL SELECT 'history.refunded_at', COUNT(*) FROM history WHERE refunded_at >= @cutover AND refunded_at < @cutover + INTERVAL 9 HOUR;

-- 2. 원래 값 스냅샷 (이미 있으면 에러로 멈춘다. 보정 전에 실패했다면 스냅샷은 원래 값이므로 3번만 다시 실행하면 된다)
CREATE TABLE kst_fix_518_history AS
SELECT id, rented_at, returned_at, paid_at, refunded_at FROM history
WHERE rented_at < @cutover OR returned_at < @cutover OR paid_at < @cutover OR refunded_at < @cutover;

CREATE TABLE kst_fix_518_user AS
SELECT id, created_at, updated_at, deleted_at FROM `user`
WHERE created_at < @cutover OR updated_at < @cutover OR deleted_at < @cutover;

CREATE TABLE kst_fix_518_black_list AS
SELECT id, blocked_at FROM black_list
WHERE blocked_at < @cutover;

-- 3. 보정: 지금 값이 스냅샷 값과 같고 전환 전 값일 때만 9시간을 더한다
START TRANSACTION;

UPDATE history h JOIN kst_fix_518_history o ON o.id = h.id
SET h.rented_at   = IF(h.rented_at   = o.rented_at   AND o.rented_at   < @cutover, o.rented_at   + INTERVAL 9 HOUR, h.rented_at),
    h.returned_at = IF(h.returned_at = o.returned_at AND o.returned_at < @cutover, o.returned_at + INTERVAL 9 HOUR, h.returned_at),
    h.paid_at     = IF(h.paid_at     = o.paid_at     AND o.paid_at     < @cutover, o.paid_at     + INTERVAL 9 HOUR, h.paid_at),
    h.refunded_at = IF(h.refunded_at = o.refunded_at AND o.refunded_at < @cutover, o.refunded_at + INTERVAL 9 HOUR, h.refunded_at);

-- updated_at에 ON UPDATE NOW()가 걸려 있어서, 같은 문장에서 직접 지정해야 현재 시각으로 덮어써지지 않는다
UPDATE `user` u JOIN kst_fix_518_user o ON o.id = u.id
SET u.created_at = IF(u.created_at = o.created_at AND o.created_at < @cutover, o.created_at + INTERVAL 9 HOUR, u.created_at),
    u.updated_at = IF(u.updated_at = o.updated_at AND o.updated_at < @cutover, o.updated_at + INTERVAL 9 HOUR, u.updated_at),
    u.deleted_at = IF(u.deleted_at = o.deleted_at AND o.deleted_at < @cutover, o.deleted_at + INTERVAL 9 HOUR, u.deleted_at);

UPDATE black_list b JOIN kst_fix_518_black_list o ON o.id = b.id
SET b.blocked_at = IF(b.blocked_at = o.blocked_at, o.blocked_at + INTERVAL 9 HOUR, b.blocked_at);

COMMIT;
