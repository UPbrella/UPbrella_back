package upbrella.be.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.user.entity.BlackList;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AllBlackListResponse {

    private List<SingleBlackListResponse> blackList;

    public static AllBlackListResponse of(List<BlackList> blackList) {

        List<SingleBlackListResponse> blackLists = blackList.stream()
                .map(SingleBlackListResponse::of)
                .collect(Collectors.toList());

        return AllBlackListResponse.builder()
                .blackList(blackLists)
                .build();
    }
}
