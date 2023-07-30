package upbrella.be.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import upbrella.be.login.dto.request.JoinRequest;
import upbrella.be.login.exception.ExistingMemberException;
import upbrella.be.user.entity.User;
import upbrella.be.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public long login(Long socialId) {

        //회원 가입되어있는 경우 DB에서 조회하며, 아닌 경우 회원을 임시 등록한다.
        User foundUser = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 회원입니다. 회원 가입을 해주세요."));

        return foundUser.getId();
    }

    public long join(long socialId, JoinRequest joinRequest) {

        if (userRepository.existsBySocialId(socialId)) {
            throw new ExistingMemberException("[ERROR] 이미 가입된 회원입니다. 로그인 폼으로 이동합니다.");
        }

        return userRepository.save(User.createNewUser(socialId, joinRequest)).getId();
    }
}
