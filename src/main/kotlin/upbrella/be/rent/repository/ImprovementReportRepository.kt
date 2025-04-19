package upbrella.be.rent.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.rent.entity.ImprovementReport

interface ImprovementReportRepository : JpaRepository<ImprovementReport, Long>
