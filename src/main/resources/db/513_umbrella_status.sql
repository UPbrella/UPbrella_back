-- #513 우산 상태(status) 컬럼 추가. 머지 전에 한 번 실행한다.
--
-- 순서
-- 1. 머지 전: 이 파일 실행 (컬럼 추가 + 기존 rentable/missed로 상태 채우기)
-- 2. 머지 → 맥미니 자동 배포
-- 3. 배포 직후: 513_umbrella_status_sync.sql 실행
--
-- 기존 값은 예전 통계와 똑같이 옮긴다. 분실(missed) → LOST, 대여 가능(rentable) → AVAILABLE, 나머지 → RENTED.
-- 결정사항에 맞춘 재고 정리는 513_umbrella_cleanup.sql 로 따로 한다.

ALTER TABLE umbrella
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
        COMMENT '우산 상태: AVAILABLE(사용 가능), RENTED(대여중), UNLOCATED(위치 미확인), LOST(분실)';

UPDATE umbrella
SET status = CASE
    WHEN COALESCE(missed, 0) = 1 THEN 'LOST'
    WHEN COALESCE(rentable, 0) = 1 THEN 'AVAILABLE'
    ELSE 'RENTED'
END;
