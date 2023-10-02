package upbrella.be.store.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateSubClassificationRequest {

    @NotBlank
    private String name;
}