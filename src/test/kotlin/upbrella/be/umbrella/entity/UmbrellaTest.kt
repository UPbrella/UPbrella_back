package upbrella.be.umbrella.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
import java.time.LocalDateTime

class UmbrellaTest {

    private val storeMeta = StoreMeta(id = 1L, name = "대여점", activated = true, category = "카페")
    private val otherStoreMeta = StoreMeta(id = 2L, name = "반납점", activated = true, category = "카페")

    private fun umbrella(status: UmbrellaStatus) = Umbrella(
        id = 1L,
        storeMeta = storeMeta,
        uuid = 1L,
        status = status,
        deleted = false,
        createdAt = LocalDateTime.now(),
        etc = null
    )

    @Test
    @DisplayName("대여하면 대여중이 되고, 기존 rentable/missed도 같이 바뀐다")
    fun rentUmbrella() {
        // given
        val umbrella = umbrella(UmbrellaStatus.AVAILABLE)

        // when
        umbrella.rentUmbrella()

        // then
        assertAll(
            { assertThat(umbrella.status).isEqualTo(UmbrellaStatus.RENTED) },
            { assertThat(umbrella.rentable).isFalse() },
            { assertThat(umbrella.missed).isFalse() },
            { assertThat(umbrella.cannotBeRented()).isTrue() }
        )
    }

    @Test
    @DisplayName("분실된 우산도 반납되면 반납 지점에서 사용 가능이 된다")
    fun returnLostUmbrella() {
        // given
        val umbrella = umbrella(UmbrellaStatus.LOST)

        // when
        umbrella.returnUmbrella(otherStoreMeta)

        // then
        assertAll(
            { assertThat(umbrella.status).isEqualTo(UmbrellaStatus.AVAILABLE) },
            { assertThat(umbrella.storeMeta).isEqualTo(otherStoreMeta) },
            { assertThat(umbrella.rentable).isTrue() },
            { assertThat(umbrella.missed).isFalse() },
            { assertThat(umbrella.cannotBeRented()).isFalse() }
        )
    }

    @Test
    @DisplayName("위치 미확인 우산은 대여할 수 없고, 기존 missed는 true다")
    fun unlocatedUmbrella() {
        // given
        val umbrella = umbrella(UmbrellaStatus.UNLOCATED)

        // then
        assertAll(
            { assertThat(umbrella.cannotBeRented()).isTrue() },
            { assertThat(umbrella.rentable).isFalse() },
            { assertThat(umbrella.missed).isTrue() }
        )
    }

    @Test
    @DisplayName("수정 요청에 status가 있으면 그 상태로 바꾼다")
    fun updateWithStatus() {
        // given
        val umbrella = umbrella(UmbrellaStatus.AVAILABLE)
        val request = UmbrellaModifyRequest(
            storeMetaId = 2L,
            uuid = 7L,
            status = UmbrellaStatus.UNLOCATED,
            rentable = true,
            missed = false
        )

        // when
        umbrella.update(request, otherStoreMeta)

        // then
        assertAll(
            { assertThat(umbrella.status).isEqualTo(UmbrellaStatus.UNLOCATED) },
            { assertThat(umbrella.uuid).isEqualTo(7L) },
            { assertThat(umbrella.storeMeta).isEqualTo(otherStoreMeta) },
            { assertThat(umbrella.rentable).isFalse() },
            { assertThat(umbrella.missed).isTrue() }
        )
    }

    @Test
    @DisplayName("수정 요청에 상태 관련 값이 하나도 없으면 상태를 바꾸지 않는다")
    fun updateWithoutStatus() {
        // given
        val umbrella = umbrella(UmbrellaStatus.RENTED)
        val request = UmbrellaModifyRequest(storeMetaId = 1L, uuid = 1L)

        // when
        umbrella.update(request, storeMeta)

        // then
        assertThat(umbrella.status).isEqualTo(UmbrellaStatus.RENTED)
    }

    @ParameterizedTest(name = "rentable={0}, missed={1} → {2}")
    @CsvSource(
        "true, false, AVAILABLE",
        "false, false, RENTED",
        "false, true, LOST",
        "true, true, LOST"
    )
    @DisplayName("status 없이 rentable/missed만 보내는 기존 수정 요청은 사용 가능 우산을 예전 통계와 같은 상태로 바꾼다")
    fun updateWithLegacyFields(rentable: Boolean, missed: Boolean, expected: UmbrellaStatus) {
        // given
        val umbrella = umbrella(UmbrellaStatus.AVAILABLE)
        val request = UmbrellaModifyRequest(storeMetaId = 1L, uuid = 1L, rentable = rentable, missed = missed)

        // when
        umbrella.update(request, storeMeta)

        // then
        assertThat(umbrella.status).isEqualTo(expected)
    }

    @ParameterizedTest(name = "{0}, rentable={1}, missed={2} → {3}")
    @CsvSource(
        "UNLOCATED, false, false, UNLOCATED",
        "UNLOCATED, false, true, UNLOCATED",
        "UNLOCATED, true, true, UNLOCATED",
        "UNLOCATED, true, false, AVAILABLE",
        "LOST, false, false, LOST",
        "LOST, false, true, LOST",
        "LOST, true, false, AVAILABLE"
    )
    @DisplayName("기존 수정 요청은 위치 미확인·분실 우산을 대여 가능으로 바꿀 때만 상태를 바꾼다")
    fun updateMissingUmbrellaWithLegacyFields(
        current: UmbrellaStatus,
        rentable: Boolean,
        missed: Boolean,
        expected: UmbrellaStatus
    ) {
        // given
        val umbrella = umbrella(current)
        val request = UmbrellaModifyRequest(storeMetaId = 1L, uuid = 1L, rentable = rentable, missed = missed)

        // when
        umbrella.update(request, storeMeta)

        // then
        assertThat(umbrella.status).isEqualTo(expected)
    }

    @Test
    @DisplayName("등록 요청에 상태 관련 값이 없으면 사용 가능으로 등록한다")
    fun createWithoutStatus() {
        // given
        val request = UmbrellaCreateRequest(storeMetaId = 1L, uuid = 1L)

        // when
        val umbrella = Umbrella.ofCreated(request, storeMeta)

        // then
        assertAll(
            { assertThat(umbrella.status).isEqualTo(UmbrellaStatus.AVAILABLE) },
            { assertThat(umbrella.rentable).isTrue() }
        )
    }

    @Test
    @DisplayName("status 없이 rentable=false만 보내는 기존 등록 요청은 대여중으로 등록한다")
    fun createWithLegacyRentable() {
        // given
        val request = UmbrellaCreateRequest(storeMetaId = 1L, uuid = 1L, rentable = false)

        // when
        val umbrella = Umbrella.ofCreated(request, storeMeta)

        // then
        assertThat(umbrella.status).isEqualTo(UmbrellaStatus.RENTED)
    }
}
