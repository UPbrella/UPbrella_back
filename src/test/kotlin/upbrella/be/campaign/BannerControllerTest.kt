package upbrella.be.campaign

import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.assertThat
import org.springframework.web.server.ResponseStatusException
import java.time.*
class BannerControllerTest {
    private val repo=mock<BannerRepository>()
    private val clock=Clock.fixed(Instant.parse("2026-10-11T00:00:00Z"),ZoneId.of("Asia/Seoul"))
    private val controller=BannerController(repo,clock,"/tmp/upbrella-banner-test")
    @Test fun `unsafe schemes and scheme relative links are rejected`() {
        for (url in listOf("javascript:alert(1)","//evil.example","https://user@evil.example","/\\evil.example")) {
            assertThatThrownBy { controller.create(BannerRequest("테스트","https://example.com/image.png",url,0,true,null,null)) }.isInstanceOf(ResponseStatusException::class.java)
        }
        verifyNoInteractions(repo)
    }
    @Test fun `only banners inside their scheduled active window are shown`() {
        val now=LocalDateTime.now(clock)
        val active=Banner("활성","https://example.com/a.png","/stamp",0,true,now,now.plusMinutes(1))
        whenever(repo.findAllByDeletedIsFalseOrderByDisplayOrderAscIdAsc()).thenReturn(listOf(active,
            Banner("종료","https://example.com/a.png","/stamp",0,true,null,now),
            Banner("예약","https://example.com/a.png","/stamp",0,true,now.plusSeconds(1),null),
            Banner("숨김","https://example.com/a.png","/stamp",0,false,null,null)))
        assertThat(controller.active().data).containsExactly(active)
    }
}
