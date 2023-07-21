package upbrella.be.rent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.rent.entity.ConditionReport;

import java.util.List;

public interface ConditionReportRepository extends JpaRepository<ConditionReport, Long> {

    List<ConditionReport> findAll();
}
