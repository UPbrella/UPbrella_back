# 재고 실사 API

선행 PR #521의 상태 enum을 그대로 사용합니다. 새 상태 컬럼이나 locationUnknown 체계는 추가하지 않습니다. `POST /admin/umbrellas/reconcile`에 실물 확인한 우산의 고유 ID 목록과 apply=false를 보내 분류를 미리 확인한 뒤 apply=true로 적용합니다.

실물 확인 우산은 최근 미반납 기록이 있으면 RENTED, 없으면 AVAILABLE입니다. 나머지는 마지막 대여일이 3개월을 넘으면 LOST, 정확히 3개월이거나 더 최근이면 UNLOCATED입니다. 이력이 없는 우산도 UNLOCATED입니다. 삭제된 우산은 제외하고, 존재하지 않는 ID는 거절합니다. 관리번호(uuid)를 바꾸지 않습니다. 적용 시 행 잠금을 잡고 엔티티의 상태 전이를 통해 기존 호환 필드도 함께 유지합니다.

#521의 상태 마이그레이션 후 `src/main/resources/db/26_2_inventory_verified.sql`을 한 번 적용해야 합니다. 이 PR은 관리번호 재배정 SQL을 실행하거나 적용하지 않습니다. 관리자 대여 내역의 마지막 불완전 페이지가 누락되던 페이지 수 계산도 올림으로 보정합니다.
