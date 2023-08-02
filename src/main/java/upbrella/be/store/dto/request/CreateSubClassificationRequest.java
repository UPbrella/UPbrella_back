package upbrella.be.store.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateSubClassificationRequest {

    @NotBlank
    private String name;
}