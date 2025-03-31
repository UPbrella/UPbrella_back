package upbrella.be.rent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import upbrella.be.rent.entity.ConditionReport;

public interface ConditionReportRepository extends JpaRepository<ConditionReport, Long> {

    List<ConditionReport> findAll();
}
