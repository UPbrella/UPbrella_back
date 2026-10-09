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

## 기존 데이터는 보정하지 않음

전환 전(2026-09-30 이전)에 저장된 값은 UTC 그대로 둔다. 그래서 그 기간의 기록은 실제보다 9시간 이르게 보인다. (예: 9월 29일 15:00 대여 → 06:00으로 표시)

| 테이블 | 영향 받는 컬럼 |
|--------|---------------|
| `history` | `rented_at`, `returned_at`, `paid_at`, `refunded_at` |
| `user` | `created_at`, `updated_at`, `deleted_at` |
| `black_list` | `blocked_at` |

나중에 보정이 필요해지면 전환 시각(맥미니 서버가 운영 DB에 처음 쓴 시각, 첫 mac-deploy 실행은 2026-09-30 03:13:51 UTC) 이전 값에만 컬럼별로 9시간을 더한다. 한 행 안에서도 대여는 전환 전, 반납은 전환 후일 수 있다. `user.updated_at`에는 `ON UPDATE NOW()`가 걸려 있어서 같은 UPDATE 문에서 직접 지정해야 현재 시각으로 덮어써지지 않는다.
