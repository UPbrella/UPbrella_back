package upbrella.be.store.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AllSubClassificationResponse {

    List<SingleSubClassificationResponse> subClassifications;
}