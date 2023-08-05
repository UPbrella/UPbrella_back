package upbrella.be.rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.rent.dto.request.HistoryFilterRequest;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.RentalHistoriesPageResponse;
import upbrella.be.rent.dto.response.RentalHistoryResponse;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.repository.RentRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.service.StoreMetaService;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.service.UmbrellaService;
import upbrella.be.user.dto.response.AllHistoryResponse;
import upbrella.be.user.dto.response.SingleHistoryResponse;
import upbrella.be.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentService {

    private final UmbrellaService umbrellaService;
    private final StoreMetaService storeMetaService;
    private final RentRepository rentRepository;

    @Transactional
    public History addRental(RentUmbrellaByUserRequest rentUmbrellaByUserRequest, User userToRent) {

        Umbrella willRentUmbrella = umbrellaService.findUmbrellaById(rentUmbrellaByUserRequest.getUmbrellaId());

        StoreMeta rentalStore = storeMetaService.findStoreMetaById(rentUmbrellaByUserRequest.getStoreId());

        return rentRepository.save(
                History.ofCreatedByNewRent(
                        willRentUmbrella,
                        userToRent,
                        rentalStore)
        );
    }

    @Transactional
    public RentalHistoriesPageResponse findAllHistories(HistoryFilterRequest filter) {

        return RentalHistoriesPageResponse.of(findAllRentalHistory(filter));
    }

    public AllHistoryResponse findAllHistoriesByUser(long userId) {

        return AllHistoryResponse.of(findAllByUserId(userId));
    }

    private List<SingleHistoryResponse> findAllByUserId(long userId) {

        return findAllByUser(userId)
                .stream()
                .map(this::toSingleHistoryResponse)
                .collect(Collectors.toList());
    }

    private List<History> findAllByUser(long userId) {
        return rentRepository.findAllByUserId(userId);
    }

    private List<RentalHistoryResponse> findAllRentalHistory(HistoryFilterRequest filter) {

        return findAll(filter)
                .stream()
                .map(this::toRentalHistoryResponse)
                .collect(Collectors.toList());
    }

    private List<History> findAll(HistoryFilterRequest filter) {

        return rentRepository.findAll(filter);
    }

    private SingleHistoryResponse toSingleHistoryResponse(History history) {

        boolean isReturned = true;
        boolean isRefunded = false;
        LocalDateTime returnAt = history.getReturnedAt();

        if (returnAt == null) {
            isReturned = false;
            returnAt = history.getRentedAt().plusDays(14);
        }

        if (history.getRefundedAt() != null) {
            isRefunded = true;
        }

        return SingleHistoryResponse.ofUserHistory(history, returnAt, isReturned, isRefunded);
    }

    private RentalHistoryResponse toRentalHistoryResponse(History history) {

        int elapsedDay = LocalDateTime.now().getDayOfMonth() - history.getRentedAt().getDayOfMonth();
        int totalRentalDay = 0;
        boolean isReturned = false;

        if (history.getReturnedAt() != null) {

            elapsedDay = history.getReturnedAt().getDayOfMonth() - history.getRentedAt().getDayOfMonth();
            totalRentalDay = history.getReturnedAt().getDayOfMonth() - history.getRentedAt().getDayOfMonth();
            isReturned = true;

            return RentalHistoryResponse.createReturnedHistory(history, elapsedDay, totalRentalDay, isReturned);
        }

        return RentalHistoryResponse.createNonReturnedHistory(history, elapsedDay, totalRentalDay, isReturned);
    }
}
