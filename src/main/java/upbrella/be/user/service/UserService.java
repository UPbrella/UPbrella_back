package upbrella.be.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.request.UpdateBankAccountRequest;
import upbrella.be.user.dto.response.AllUsersInfoResponse;
import upbrella.be.user.entity.BlackList;
import upbrella.be.user.entity.User;
import upbrella.be.user.exception.BlackListUserException;
import upbrella.be.user.exception.ExistingMemberException;
import upbrella.be.user.exception.NonExistingMemberException;
import upbrella.be.user.repository.BlackListRepository;
import upbrella.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BlackListRepository blackListRepository;

    public long login(Long socialId) {

        User foundUser = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다. 회원 가입을 해주세요."));

        return foundUser.getId();
    }

    public long join(long socialId, JoinRequest joinRequest) {

        if (userRepository.existsBySocialId(socialId)) {
            throw new ExistingMemberException("[ERROR] 이미 가입된 회원입니다. 로그인 폼으로 이동합니다.");
        }
        if(blackListRepository.existsBySocialId(socialId)){
            throw new BlackListUserException("[ERROR] 정지된 회원입니다. 정지된 회원은 재가입이 불가능합니다.");
        }

        User joinedUser = userRepository.save(User.createNewUser(socialId, joinRequest));

        return joinedUser.getId();
    }

    public AllUsersInfoResponse findUsers() {

        return AllUsersInfoResponse.fromUsers(userRepository.findAll());
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
        long socialId = foundUser.getSocialId();
        BlackList newBlackList = BlackList.createNewBlackList(socialId);
        blackListRepository.save(newBlackList);

        foundUser.deleteUser();
    }

    private User findUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new NonExistingMemberException("[ERROR] 존재하지 않는 회원입니다."));
    }
}
