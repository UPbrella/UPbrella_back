package upbrella.be.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomResponse<T> {

    private String status;
    private int code;
    private String message;
    private T data;
}
