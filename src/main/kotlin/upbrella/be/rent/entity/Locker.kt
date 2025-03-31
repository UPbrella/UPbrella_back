package upbrella.be.rent.entity

import upbrella.be.store.entity.StoreMeta
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
class Locker(

    @OneToOne
    @JoinColumn(name = "store_meta_id")
    var storeMeta: StoreMeta,
    var count: Long = 0,
    var secretKey: String,
    var lastAccess: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {

    fun updateCount() {
        this.count += 1
    }

    fun updateLastAccess(now: LocalDateTime) {
        this.lastAccess = now
    }

    fun updateLocker(storeMeta: StoreMeta, secretKey: String) {
        this.storeMeta = storeMeta
        this.secretKey = secretKey
    }

    fun updateCount(count: Long) {
        this.count = count
    }
}