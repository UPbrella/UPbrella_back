package upbrella.be.rent.repository;

import org.springframework.data.domain.Pageable;
import upbrella.be.rent.dto.response.HistoryInfoDto;
import upbrella.be.rent.dto.request.HistoryFilterRequest;
import upbrella.be.rent.entity.History;

import java.util.List;

public interface RentRepositoryCustom {

    List<History> findAll(HistoryFilterRequest filter, Pageable pageable);

    List<HistoryInfoDto> findHistoryInfos(HistoryFilterRequest filter, Pageable pageable);

    long countAll(HistoryFilterRequest filter, Pageable pageable);

    List<History> findAllByUserId(long userId);
}
