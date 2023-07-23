package upbrella.be.store.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreImage;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.store.repository.StoreImageRepository;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ExtendWith(MockitoExtension.class)
class StoreImageServiceTest {

    @Mock
    private S3Client s3Client;
    @Mock
    private StoreImageRepository storeImageRepository;
    @Mock
    private StoreDetailRepository storeDetailRepository;
    @InjectMocks
    private StoreImageService storeImageService;


    @Test
    @DisplayName("파일 업로드 테스트")
    void uploadFileTest() {
        // given
        long storeDetailId = 1L;
        MockMultipartFile file = new MockMultipartFile("image", "filename.jpg", "image/jpg", "some-image".getBytes());
        StoreDetail storeDetail = StoreDetail.builder()
                .build();
        String randomId = storeImageService.makeRandomId();
        String expectedUrl = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg" + randomId;

        given(storeDetailRepository.getReferenceById(storeDetailId)).willReturn(storeDetail);
        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).willReturn(PutObjectResponse.builder().build());

        // when
        String result = storeImageService.uploadFile(file, storeDetailId, randomId);


        // then
        Assertions.assertThat(result).isEqualTo(expectedUrl);
        verify(storeDetailRepository, times(1)).getReferenceById(storeDetailId);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(storeImageRepository, times(1)).save(any(StoreImage.class));
    }

    @Test
    @DisplayName("새로 사진을 등록할 때 기존에 있던 사진들을 삭제하는 테스트")
    void deleteFileTest() {
        // given
        StoreImage url1 = StoreImage.builder()
                .storeDetail(StoreDetail.builder().build())
                .imageUrl("url1")
                .build();
        StoreImage url2 = StoreImage.builder()
                .storeDetail(StoreDetail.builder().build())
                .imageUrl("url1")
                .build();
        storeImageRepository.save(url1);
        storeImageRepository.save(url2);

        // when
        storeImageService.deleteImagesBeforeSave(1L);

        // then
        Assertions.assertThat(storeImageRepository.findAll().size()).isEqualTo(0);
    }
}
