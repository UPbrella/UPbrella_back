package upbrella.be.store.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
public class ImagesRequest {

    List<MultipartFile> images;
}
