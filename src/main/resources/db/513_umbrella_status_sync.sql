-- #513 배포 직후 실행한다. 여러 번 실행해도 된다.
--
-- 513_umbrella_status.sql 실행 후 새 코드가 배포되기 전까지는 예전 코드가 rentable/missed만 바꾼다.
-- 새 코드는 status와 rentable/missed를 항상 같이 바꾸므로, 둘이 어긋난 행은 그 사이에 예전 코드가 바꾼 것뿐이다.

UPDATE umbrella
SET status = CASE
    WHEN COALESCE(missed, 0) = 1 THEN 'LOST'
    WHEN COALESCE(rentable, 0) = 1 THEN 'AVAILABLE'
    ELSE 'RENTED'
END
WHERE (status = 'AVAILABLE') <> (COALESCE(rentable, 0) = 1)
   OR (status IN ('UNLOCATED', 'LOST')) <> (COALESCE(missed, 0) = 1);
