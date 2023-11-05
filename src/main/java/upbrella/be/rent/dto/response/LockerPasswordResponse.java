package upbrella.be.rent.dto.response;

import lombok.Getter;

@Getter
public class LockerPasswordResponse {

    private String password;

    public LockerPasswordResponse(String password) {
        this.password = password.substring(0, 4);
    }
}
