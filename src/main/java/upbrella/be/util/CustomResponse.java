package upbrella.be.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomResponse<T> {

    private String status;
    private int code;
    private String message;
    private T data;

    public CustomResponse(String status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
