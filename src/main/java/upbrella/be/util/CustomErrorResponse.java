package upbrella.be.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomErrorResponse {

    private String status;
    private int code;
    private String message;
    private Object data = null;

    public CustomErrorResponse(String status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
