package upbrella.be.rent.repository;

import upbrella.be.rent.dto.request.HistoryFilterRequest;
import upbrella.be.rent.entity.History;

import java.util.List;

public interface RentRepositoryCustom {

    List<History> findAll(HistoryFilterRequest filter);

    List<History> findAllByUserId(long userId);
}
