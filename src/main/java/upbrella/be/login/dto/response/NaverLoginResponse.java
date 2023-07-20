package upbrella.be.login.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginResponse {

    private LoggedInUser response;
}
