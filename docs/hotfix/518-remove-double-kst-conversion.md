# Hotfix: 대여 시간 이중 KST 변환 제거

## 개요

어드민 대여/반납 현황에서 대여 날짜가 하루 밀려 보이는 문제(예: 10월 4일 대여 → 10월 5일로 표시)를 고친다. 응답 DTO의 UTC → KST 변환을 없애고, 서버 시간을 한국 시간으로 통일한다.

## 원인

| 기간 | 실행 이미지 | JVM 시간대 | DB에 저장되는 값 |
|------|------------|-----------|-----------------|
| ~ 2026-09-30 | `.github/workflows/Dockerfile` | UTC | UTC |
| 2026-09-30 ~ (맥미니, #510) | 루트 `Dockerfile` (`ENV TZ=Asia/Seoul`) | Asia/Seoul | 한국 시간 |

#508의 `toKst()`는 DB 값이 UTC라고 보고 9시간을 더한다. 전환 후에는 이미 한국 시간인 값에 9시간이 또 더해져서, 15시 이후 대여가 다음 날로 보였다.

## 방향: 한국 시간으로 통일

서버를 UTC로 되돌리는 방법도 있지만, 매장 영업 중 판정(`StoreMeta.isOpenStore`)이 `LocalDateTime.now()`를 영업시간(한국 시간)과 바로 비교한다. UTC로 되돌리면 이 판정이 다시 9시간 어긋난다. 그래서 서버는 한국 시간 그대로 두고 응답 변환을 없앤다.

## 변경 사항

- `RentalHistoryResponse`, `SingleHistoryResponse`, `SingleBlackListResponse`에서 `toKst()` 제거. DB 값을 그대로 응답한다.
- 테스트에서 UTC → KST 변환을 기대하던 부분을 되돌리고, 15시 이후 시간도 날짜가 바뀌지 않는지 확인하는 테스트 추가.
- 전환 전 데이터 보정 SQL: `src/main/resources/db/518_fix_kst.sql`

## 데이터 보정

전환 전에 UTC로 저장된 값에 9시간을 더한다. 컬럼별로 `값 < 전환 시각(UTC)`인 것만 고른다. (한 행 안에서도 대여는 전환 전, 반납은 전환 후일 수 있다)

| 테이블 | 컬럼 |
|--------|------|
| `history` | `rented_at`, `returned_at`, `paid_at`, `refunded_at` |
| `user` | `created_at`, `updated_at`, `deleted_at` |
| `black_list` | `blocked_at` |
| `umbrella` | `created_at` (조건부, 아래 참고) |

- 다시 실행해도 9시간이 두 번 더해지지 않게, 원래 값을 스냅샷 테이블(`kst_fix_518_*`)에 저장하고 지금 값이 스냅샷 값과 같을 때만 보정한다. 스냅샷은 백업도 겸한다.
- `user.updated_at`에 `ON UPDATE NOW()`가 걸려 있어서, 같은 UPDATE 문에서 직접 지정해야 현재 시각으로 덮어써지지 않는다.
- `umbrella.created_at`은 TIMESTAMP라서 MySQL이 세션 시간대 기준으로 변환한다. DB 서버를 옮기지 않았거나 시간대 설정이 같다면 DATETIME처럼 보정하고, 시간대가 다른 서버로 옮겼다면 실제 값을 보고 판단한다. 그래서 SQL에서는 주석으로 남겨 뒀다.

## 배포 순서

1. 전환 시각 확정 (맥미니 서버가 운영 DB에 처음 쓴 시각, 첫 mac-deploy 실행은 2026-09-30 03:13:51 UTC)
2. DB 백업
3. 이 PR 머지 → 맥미니 자동 배포
4. 배포 직후 `518_fix_kst.sql` 실행 (확인 쿼리 → 스냅샷 → 보정 → 필요하면 umbrella)
5. 확인이 끝나면 스냅샷 테이블(`kst_fix_518_*`)은 백업으로 보관하다가 지운다

3과 4 사이에는 전환 전 대여 건이 9시간 이르게 보인다. 사이 시간을 짧게 한다.
