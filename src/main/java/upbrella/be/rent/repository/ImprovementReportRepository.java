package upbrella.be.rent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.rent.entity.ImprovementReport;

import java.util.List;

public interface ImprovementReportRepository extends JpaRepository<ImprovementReport, Long> {

    List<ImprovementReport> findAll();
}
