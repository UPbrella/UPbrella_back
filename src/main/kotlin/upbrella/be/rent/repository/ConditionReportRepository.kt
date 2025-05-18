package upbrella.be.rent.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.rent.entity.ConditionReport

interface ConditionReportRepository : JpaRepository<ConditionReport, Long>
