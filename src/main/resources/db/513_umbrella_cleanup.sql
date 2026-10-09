-- #513 재고 정리 (26-2 결정사항 [우산 관리 탭]). 운영진에게 9/27 배치 우산 목록을 받은 뒤 한 번 실행한다.
--
-- - 9/27 기준 배치 우산(목록)에 새 관리번호(1~30)를 준다. 반납 안 된 대여가 있으면 대여중, 아니면 사용 가능.
-- - 목록 밖 우산은 마지막 대여일(대여 기록이 없으면 등록일) 기준 3개월 이내면 위치 미확인, 넘으면 분실.
--   반납 안 된 대여가 있어도 결정사항대로 분류한다. 나중에 반납되면 사용 가능으로 바뀐다.
-- - 새 관리번호와 겹치는 목록 밖 우산은 가장 큰 관리번호 뒤로 옮긴다.
-- - 기존 rentable/missed 컬럼도 status에 맞춘다.
-- 513_umbrella_status.sql 실행과 배포가 끝난 뒤에 실행한다.

-- 기준일. 이 날짜에서 3개월 전보다 최근에 대여됐으면 위치 미확인
SET @base_date = '2026-09-27';

-- 1. 9/27 배치 우산 목록 (현재 관리번호 → 새 관리번호)
CREATE TABLE umbrella_cleanup_513 (
    current_uuid BIGINT PRIMARY KEY,
    new_uuid     BIGINT NOT NULL UNIQUE,
    umbrella_id  INT NULL
);

-- 운영진 목록으로 채운다. 예: (현재 관리번호, 새 관리번호)
-- INSERT INTO umbrella_cleanup_513 (current_uuid, new_uuid) VALUES
--     (105, 1),
--     (37, 2);

UPDATE umbrella_cleanup_513 c
JOIN umbrella u ON u.uuid = c.current_uuid AND u.deleted = 0
SET c.umbrella_id = u.id;

-- 2. 확인: 목록에 있는데 우산을 못 찾은 관리번호. 결과가 있으면 목록을 고치고 다시 시작한다
SELECT current_uuid FROM umbrella_cleanup_513 WHERE umbrella_id IS NULL;
SELECT COUNT(*) AS listed_umbrellas FROM umbrella_cleanup_513;

-- 3. 정리
START TRANSACTION;

-- 새 관리번호와 겹치는 목록 밖 우산을 가장 큰 관리번호 뒤로 옮긴다
SET @next_uuid = (SELECT MAX(uuid) FROM umbrella);
UPDATE umbrella u
SET u.uuid = (@next_uuid := @next_uuid + 1)
WHERE u.deleted = 0
  AND u.uuid IN (SELECT new_uuid FROM umbrella_cleanup_513)
  AND NOT EXISTS (SELECT 1 FROM umbrella_cleanup_513 c WHERE c.umbrella_id = u.id)
ORDER BY u.uuid;

-- 목록의 우산
UPDATE umbrella u
JOIN umbrella_cleanup_513 c ON c.umbrella_id = u.id
SET u.uuid = c.new_uuid,
    u.status = IF(
        EXISTS (SELECT 1 FROM history h WHERE h.umbrella_id = u.id AND h.returned_at IS NULL),
        'RENTED',
        'AVAILABLE'
    );

-- 목록 밖 우산
UPDATE umbrella u
LEFT JOIN (SELECT umbrella_id, MAX(rented_at) AS last_rented_at FROM history GROUP BY umbrella_id) h
    ON h.umbrella_id = u.id
SET u.status = IF(
    COALESCE(h.last_rented_at, u.created_at) >= @base_date - INTERVAL 3 MONTH,
    'UNLOCATED',
    'LOST'
)
WHERE u.deleted = 0
  AND NOT EXISTS (SELECT 1 FROM umbrella_cleanup_513 c WHERE c.umbrella_id = u.id);

-- 기존 컬럼을 status에 맞춘다
UPDATE umbrella
SET rentable = (status = 'AVAILABLE'),
    missed   = (status IN ('UNLOCATED', 'LOST'))
WHERE deleted = 0;

COMMIT;

-- 4. 확인
SELECT status, COUNT(*) FROM umbrella WHERE deleted = 0 GROUP BY status;
SELECT uuid, status FROM umbrella WHERE deleted = 0 ORDER BY uuid LIMIT 40;
