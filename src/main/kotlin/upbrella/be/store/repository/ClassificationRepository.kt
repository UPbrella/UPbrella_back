package upbrella.be.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.ClassificationType

interface ClassificationRepository : JpaRepository<Classification, Long> {
    fun findByType(type: ClassificationType): List<Classification>
}