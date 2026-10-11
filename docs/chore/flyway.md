# Flyway 도입

DB 스키마 변경을 손으로 SQL을 실행하는 대신 Flyway 마이그레이션으로 한다. 앱이 뜰 때 아직 적용하지 않은 마이그레이션을 차례로 실행하고, 적용한 기록은 `flyway_schema_history` 테이블에 남는다.

## 구성

| 항목 | 내용 |
|------|------|
| 의존성 | `flyway-core`, `flyway-mysql` (버전은 Spring Boot 2.7.13이 관리, Flyway 8.5.13) |
| 마이그레이션 위치 | `src/main/resources/db/migration` |
| 설정 | `application.yml`의 `spring.flyway` (dev·prod 공통) |
| 운영 DB 계정 | `upbrella` 계정은 `upbrella` DB에 모든 권한이 있어 따로 권한을 줄 필요가 없다 |

### V1 = 도입 시점의 운영 스키마

`V1__init.sql`은 2026-10-11 운영 DB(맥미니 `mysql8`의 `upbrella`) 스키마를 그대로 옮긴 것이다. 그동안 손으로 바꾼 내용(`user`의 생성일·수정일·삭제일·`provider`·`social_id` 유니크 키, `store_image.image_urls`)이 모두 들어 있다.

- 저장소에 있던 `db/init.sql`은 이 변경들이 빠진 예전 스키마라서 지웠다. `db/add_time.sql`은 이미 운영에 적용돼 V1에 들어 있어서 지웠다.
- **기존 DB** (테이블은 있는데 Flyway 이력이 없음): `baseline-on-migrate`로 V1까지 적용된 것으로 기록하고 V1은 실행하지 않는다.
- **빈 DB**: V1부터 실행해 테이블을 만든다.

## 첫 배포 때 운영 DB에 생기는 일

`flyway_schema_history` 테이블이 생기고 기준(baseline) 기록 한 줄(version 1)만 들어간다. 스키마는 바뀌지 않는다.

```sql
SELECT installed_rank, version, description, type, success FROM flyway_schema_history;
```

## 마이그레이션 추가하는 법

1. `src/main/resources/db/migration/V<번호>__<설명>.sql` 파일을 만든다. 밑줄은 두 개, 번호는 정수로 이어서 붙인다 (`V2__add_umbrella_status.sql`).
2. PR을 올리면 CI의 `FlywayMigrationTest`가 MySQL 8.0.30에서 적용해 본다.
3. dev에 머지되면 맥미니에 배포되면서 적용된다.

### 지킬 것

- **머지된 파일은 고치지 않는다.** Flyway가 적용한 파일의 체크섬을 기록해 두기 때문에, 내용이 바뀌면 앱이 뜨지 않는다. 고칠 게 있으면 새 파일을 만든다.
- **번호가 겹치거나 거꾸로 머지되지 않게 한다.** 머지 전에 dev의 마지막 번호를 확인하고 필요하면 번호를 올린다. 운영에 V3이 적용된 뒤 V2가 들어오면 앱이 뜨지 않는다.
- **파일 하나에 변경 하나.** MySQL은 DDL을 트랜잭션으로 되돌리지 못해서, 파일 중간에서 실패하면 앞부분만 적용된 채로 남는다.
- **예전 코드도 동작하게.** 배포가 실패하면 배포 스크립트가 이전 이미지로 롤백하는데, 스키마는 되돌아가지 않는다. 컬럼 추가는 기본값을 주고, 컬럼 삭제·이름 변경은 코드에서 안 쓰게 바꿔 배포한 뒤 다음 마이그레이션으로 한다.
- 운영진 입력이 필요한 일회성 데이터 작업(예: #513 재고 정리)은 마이그레이션이 아니라 손으로 실행한다.

### 마이그레이션이 실패하면

앱이 뜨지 않아 헬스체크가 실패하고, 배포 스크립트가 이전 이미지로 롤백한 뒤 Slack으로 알린다. `flyway_schema_history`에는 `success = 0`인 기록이 남고, 이 기록을 지울 때까지 새 버전은 뜨지 않는다.

1. 컨테이너 로그에서 실패한 파일과 SQL을 확인한다.
2. 그 파일에서 이미 적용된 부분을 손으로 되돌린다.
3. 실패 기록을 지운다: `DELETE FROM flyway_schema_history WHERE success = 0;`
4. 파일을 고쳐 다시 머지·배포한다.

## 테스트

- 기존 테스트(H2)는 지금처럼 엔티티로 스키마를 만든다(`ddl-auto: create-drop`). `application-test.yml`에서 Flyway를 끈다. V1이 MySQL 문법이라 H2에서는 돌지 않는다.
- `FlywayMigrationTest`는 CI(`dev-pr-test.yml`)가 띄운 MySQL 8.0.30에서만 돈다(`FLYWAY_TEST_DB_URL`이 없으면 건너뜀). 설정은 `application.yml`의 `spring.flyway`를 읽어 앱과 같게 맞춘다.
  - 빈 DB에 모든 마이그레이션이 적용되는지
  - 도입 전 운영 DB(V1 스키마, 이력 없음)는 V1을 건너뛰고 그 뒤 마이그레이션만 적용하는지

## 하지 않은 것

- Hibernate 스키마 검증(`ddl-auto: validate`)은 켜지 않았다. 운영 스키마의 id 컬럼은 `int`인데 엔티티는 `Long`이라 지금 켜면 앱이 뜨지 않는다. id 타입을 맞추면 `FlywayMigrationTest`에서 마이그레이션 결과와 엔티티가 맞는지도 확인할 수 있다.
