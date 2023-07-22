package upbrella.be.login.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Properties {

    // TODO : 비즈앱 등록 후 이름과 전화번호로 수정, 카카오에서 Properteis로 보내주는데 처리할 방법 고민해보기
    private String nickname;
    private String profileImage;
}
