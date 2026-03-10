# Feature: 대여 내역 응답 시간 KST 변환

## 개요

어드민 대여 내역(rent history) 조회 시 시간이 UTC(표준시)로 응답되던 문제를 수정하여, KST(한국 표준시, UTC+9)로 변환하여 응답하도록 한다.

## 배경

- DB에는 UTC 시간이 저장됨 (`LocalDateTime.now()`는 JVM 기본 타임존인 UTC 기준)
- 응답 시에도 UTC 그대로 반환되어, 한국 시간과 9시간 차이가 발생
- `@JsonFormat(timezone = "Asia/Seoul")`은 `LocalDateTime` 타입에서 **타임존 변환이 동작하지 않음**

## 원인 분석

`LocalDateTime`은 타임존 정보를 포함하지 않는 Java 타입이다. Jackson의 `@JsonFormat(timezone = ...)` 속성은 `Date`, `Instant`, `ZonedDateTime` 등 타임존 인식 타입에서만 변환이 동작하며, `LocalDateTime`에서는 무시된다.

따라서 **코드 레벨에서 직접 UTC → KST 변환**을 수행해야 한다.

## 변경 사항

### 1. RentalHistoryResponse

**파일:** `src/main/kotlin/upbrella/be/rent/dto/response/RentalHistoryResponse.kt`

| 항목 | 변경 전 | 변경 후 |
|------|---------|---------|
| `@JsonFormat` pattern | `kk:mm:ss` | `HH:mm:ss` |
| `@JsonFormat` timezone | `timezone = "Asia/Seoul"` | 제거 (불필요) |
| 팩토리 메서드 | DB 값 그대로 사용 | `toKst()` 변환 후 사용 |

**추가된 메서드:**

```kotlin
private val KST = ZoneId.of("Asia/Seoul")

private fun toKst(time: LocalDateTime): LocalDateTime {
    return time.atZone(ZoneId.systemDefault()).withZoneSameInstant(KST).toLocalDateTime()
}
```

**변환 흐름:**

```
DB (UTC) → HistoryInfoDto (UTC) → toKst() 변환 → RentalHistoryResponse (KST) → JSON 응답
```

**적용 대상 필드:**

- `rentAt`: `createReturnedHistory()`, `createNonReturnedHistory()`에서 변환
- `returnAt`: `createReturnedHistory()`에서 변환 (nullable 처리)

### 2. @JsonFormat 패턴 수정

| 변경 전 | 변경 후 | 이유 |
|---------|---------|------|
| `kk:mm:ss` | `HH:mm:ss` | `kk`는 1~24시 표기 (자정=24시), `HH`는 0~23시 표기 (표준) |

## 시간 변환 예시

| DB 저장값 (UTC) | 응답값 (KST) |
|----------------|-------------|
| 2023-07-18 00:00:00 | 2023-07-18 09:00:00 |
| 2023-07-18 15:00:00 | 2023-07-19 00:00:00 |
| 2023-07-20 23:30:00 | 2023-07-21 08:30:00 |

## 영향 받는 파일

| 파일 | 변경 내용 |
|------|----------|
| `RentalHistoryResponse.kt` | `toKst()` 메서드 추가, 팩토리 메서드에서 시간 변환 적용, `@JsonFormat` 패턴 수정 |
| `RentalHistoryResponseTest.kt` | KST 변환 검증 테스트 추가 (신규) |
| `RentControllerTest.kt` | 시간 고정값 사용 및 응답 포맷 검증 추가 |

## 테스트 케이스

### RentalHistoryResponseTest (신규)

- [x] 반납된 대여 내역의 `rentAt`이 UTC → KST(+9시간)로 변환
- [x] 반납된 대여 내역의 `returnAt`이 UTC → KST(+9시간)로 변환 (날짜 변경 케이스 포함)
- [x] 미반납 대여 내역의 `rentAt`이 UTC → KST(+9시간)로 변환, `returnAt`은 null 유지

### RentControllerTest (수정)

- [x] 대여 내역 조회 응답에서 `rentAt`, `returnAt`이 `yyyy-MM-dd HH:mm:ss` 포맷으로 응답

## 주의사항

1. **DB 저장은 UTC 유지**: 엔티티의 `LocalDateTime.now()`는 변경하지 않음. DB에는 계속 UTC로 저장됨.
2. **변환은 응답 DTO에서만**: `RentalHistoryResponse`의 팩토리 메서드에서만 KST 변환을 수행함.
3. **JVM 타임존 의존성**: `toKst()`는 `ZoneId.systemDefault()`를 사용하므로, 서버 JVM이 UTC로 설정되어 있어야 정상 동작함.
