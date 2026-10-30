package upbrella.be.umbrella.controller

import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.assertj.core.api.Assertions.assertThat
import upbrella.be.store.entity.StoreMeta
import upbrella.be.user.entity.User
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.entity.UmbrellaStatus
import upbrella.be.umbrella.repository.UmbrellaRepository
import upbrella.be.rent.repository.RentRepository
import upbrella.be.rent.entity.History
import java.time.*
import java.util.Optional

class InventoryControllerTest {
    private val umbrellas = mock<UmbrellaRepository>()
    private val histories = mock<RentRepository>()
    private val clock = Clock.fixed(Instant.parse("2026-10-11T00:00:00Z"), ZoneId.of("Asia/Seoul"))
    private val controller = InventoryController(umbrellas, histories, clock)
    private val store = StoreMeta("테스트", true, category = "테스트", id = 1)
    private val user = User(-1, "테스트", email = "", id = 1)
    private fun umbrella(id: Long) = Umbrella(store, 70 + id, UmbrellaStatus.AVAILABLE, false, LocalDateTime.now(clock), null, id)
    @Test fun `preview is read only and three month boundary is inclusive`() {
        val a=umbrella(1);val b=umbrella(2);val c=umbrella(3)
        whenever(umbrellas.findAllByDeletedFalseOrderByIdAsc()).thenReturn(listOf(a,b,c))
        whenever(histories.findFirstByUmbrellaIdOrderByRentedAtDesc(1)).thenReturn(Optional.of(History(a,user,rentStoreMeta=store,rentedAt=LocalDateTime.now(clock).minusMonths(3))))
        whenever(histories.findFirstByUmbrellaIdOrderByRentedAtDesc(2)).thenReturn(Optional.of(History(b,user,rentStoreMeta=store,rentedAt=LocalDateTime.now(clock).minusMonths(3).minusSeconds(1))))
        whenever(histories.findFirstByUmbrellaIdOrderByRentedAtDesc(3)).thenReturn(Optional.empty())
        val rows=controller.reconcile(InventoryRequest()).data!!
        assertThat(rows.map { it["status"] }).containsExactly("UNLOCATED","LOST","UNLOCATED")
        assertThat(b.missed).isFalse();verify(umbrellas,never()).findByIdAndDeletedIsFalseForUpdate(any())
    }
    @Test fun `classification preserves management number and return restores inventory`() {
        val a=umbrella(1)
        whenever(umbrellas.findAllByDeletedFalseOrderByIdAsc()).thenReturn(listOf(a))
        whenever(umbrellas.findByIdAndDeletedIsFalseForUpdate(1)).thenReturn(Optional.of(a))
        whenever(histories.findFirstByUmbrellaIdOrderByRentedAtDesc(1)).thenReturn(Optional.empty())
        controller.reconcile(InventoryRequest(apply=true))
        assertThat(a.status).isEqualTo(UmbrellaStatus.UNLOCATED);assertThat(a.cannotBeRented()).isTrue();assertThat(a.uuid).isEqualTo(71)
        a.returnUmbrella(store)
        assertThat(a.status).isEqualTo(UmbrellaStatus.AVAILABLE);assertThat(a.missed).isFalse();assertThat(a.rentable).isTrue()
    }
}
