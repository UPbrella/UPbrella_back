package upbrella.be.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import upbrella.be.login.dto.request.JoinRequest;
import upbrella.be.login.exception.ExistingMemberException;
import upbrella.be.user.entity.User;
import upbrella.be.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public long login(Long socialId) {

        //회원 가입되어있는 경우 DB에서 조회하며, 아닌 경우 회원을 임시 등록한다.
        User foundUser = userRepository.findBySocialId(socialId)
                .orElseGet(() -> userRepository.save(User.createNewUser(socialId)));

        return foundUser.getId();
    }

    public void join(long userId, JoinRequest joinRequest) {

        if (userRepository.existsByIdAndNameIsNotNullAndPhoneNumberIsNotNull(userId)) {
            throw new ExistingMemberException("[ERROR] 이미 가입된 회원입니다. 로그인 폼으로 이동합니다.");
        }
        userRepository.save(User.createNewUser(userId, joinRequest));
    }
}
