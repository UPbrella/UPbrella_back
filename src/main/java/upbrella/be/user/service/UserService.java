package upbrella.be.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import upbrella.be.login.dto.request.JoinRequest;
import upbrella.be.user.entity.User;
import upbrella.be.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public long login(Long socialId) {

        User foundUser = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 미가입된 회원입니다. 회원 가입 폼으로 이동합니다."));

        return foundUser.getId();
    }

    public long join(JoinRequest joinRequest, Long socialId) {

        if (userRepository.existsBySocialId(socialId)) {
            throw new IllegalArgumentException("[ERROR] 이미 가입된 회원입니다. 로그인 폼으로 이동합니다.");
        }
        User joinnedUser = userRepository.save(User.createNewUser(joinRequest, socialId));

        return joinnedUser.getId();
    }
}
