package upbrella.be.rent.repository;

import upbrella.be.rent.entity.History;

import java.util.List;

public interface RentRepositoryCustom {

    List<History> findAllByUserId(long userId);
}
