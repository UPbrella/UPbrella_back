package upbrella.be.store.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AllSubClassificationResponse {

    List<SingleSubClassificationResponse> subClassifications;
}