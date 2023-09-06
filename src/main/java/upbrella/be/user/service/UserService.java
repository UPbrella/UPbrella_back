package upbrella.be.user.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.service.RentService;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.request.UpdateBankAccountRequest;
import upbrella.be.user.dto.response.AllBlackListResponse;
import upbrella.be.user.dto.response.AllUsersInfoResponse;
import upbrella.be.user.dto.response.SessionUser;
import upbrella.be.user.dto.response.UmbrellaBorrowedByUserResponse;
import upbrella.be.user.entity.BlackList;
import upbrella.be.user.entity.User;
import upbrella.be.user.exception.BlackListUserException;
import upbrella.be.user.exception.ExistingMemberException;
import upbrella.be.user.exception.NonExistingMemberException;
import upbrella.be.user.repository.BlackListRepository;
import upbrella.be.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    public UserService(UserRepository userRepository, BlackListRepository blackListRepository, @Lazy RentService rentService) {
        this.userRepository = userRepository;
        this.blackListRepository = blackListRepository;
        this.rentService = rentService;
    }

    private final UserRepository userRepository;
    private final BlackListRepository blackListRepository;
    private final RentService rentService;

    public SessionUser login(Long socialId) {

        User foundUser = userRepository.findBySocialId((long) socialId.hashCode())
                .orElseThrow(() -> new NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다. 회원 가입을 해주세요."));

        return SessionUser.fromUser(foundUser);
    }

    public SessionUser join(Long socialId, JoinRequest joinRequest) {

        if (userRepository.existsBySocialId((long) socialId.hashCode())) {
            throw new ExistingMemberException("[ERROR] 이미 가입된 회원입니다. 로그인 폼으로 이동합니다.");
        }
        if (blackListRepository.existsBySocialId((long) socialId.hashCode())) {
            throw new BlackListUserException("[ERROR] 정지된 회원입니다. 정지된 회원은 재가입이 불가능합니다.");
        }

        User joinedUser = userRepository.save(User.createNewUser(socialId, joinRequest));

        return SessionUser.fromUser(joinedUser);
    }

    public AllUsersInfoResponse findUsers() {

        List<User> users = userRepository.findAll().stream()
                .map(user -> user.decryptData())
                .collect(Collectors.toList());
        return AllUsersInfoResponse.fromUsers(users);
    }

    public UmbrellaBorrowedByUserResponse findUmbrellaBorrowedByUser(SessionUser sessionUser) {

        History rentalHistory = rentService.findRentalHistoryByUser(sessionUser);

        long borrowedUmbrellaUuid = rentalHistory.getUmbrella().getUuid();

        return UmbrellaBorrowedByUserResponse.of(borrowedUmbrellaUuid);
    }

    @Transactional
    public void updateUserBankAccount(Long id, UpdateBankAccountRequest request) {

        User foundUser = findUserById(id);

        foundUser.updateBankAccount(request.getBank(), request.getAccountNumber());
    }

    @Transactional
    public void deleteUser(Long id) {

        User foundUser = findUserById(id);

        foundUser.deleteUser();
    }

    @Transactional
    public void withdrawUser(Long id) {

        User foundUser = findUserById(id);
        Long socialId = foundUser.getSocialId();
        BlackList newBlackList = BlackList.createNewBlackList(socialId);
        blackListRepository.save(newBlackList);

        foundUser.withdrawUser();
    }

    public User findUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다."));
    }


    public User findDecryptedUserById(Long id) {

        // Deep copy 객체를 반환함에 유의
        return userRepository.findById(id)
                .orElseThrow(() -> new NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다."))
                .decryptData();
    }

    @Transactional
    public void deleteUserBankAccount(Long id) {

        User foundUser = findUserById(id);

        foundUser.deleteBankAccount();
    }

    @Transactional
    public AllBlackListResponse findBlackList() {

        List<BlackList> blackLists = blackListRepository.findAll();

        return AllBlackListResponse.of(blackLists);
    }

    @Transactional
    public void deleteBlackList(long blackListId) {

        blackListRepository.deleteById(blackListId);
    }

    @Transactional
    public void updateAdminStatus(Long id) {

        User foundUser = findUserById(id);

        foundUser.updateAdminStatus();
    }
}
