package upbrella.be.user.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.AssertionsForClassTypes.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import upbrella.be.user.entity.BlackList
import upbrella.be.user.exception.BlackListUserException
import upbrella.be.user.repository.BlackListReader
import upbrella.be.user.repository.BlackListWriter
import java.time.LocalDateTime
import java.time.ZoneId

@ExtendWith(MockitoExtension::class)
class BlackListServiceTest {

    @Mock
    private lateinit var blackListReader: BlackListReader

    @Mock
    private lateinit var blackListWriter: BlackListWriter

    @InjectMocks
    private lateinit var blackListService: BlackListService

    @Test
    @DisplayName("사용자가 블랙리스트에 등록되어 있으면 예외가 발생한다.")
    fun checkBlackListThrowTest() {
        // given
        val blackList = BlackList.createNewBlackList(1L)

        given(blackListReader.findById(1L))
            .willReturn(blackList)

        // when & then
        assertThatThrownBy { blackListService.checkBlackList(1L) }
            .isInstanceOf(BlackListUserException::class.java)
    }

    @Test
    @DisplayName("사용자가 블랙리스트에 없으면 예외가 발생하지 않는다.")
    fun checkBlackListNotThrowTest() {
        // given
        given(blackListReader.findById(1L))
            .willReturn(null)

        // when & then
        assertThatCode {
            blackListService.checkBlackList(1L)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 조회할 수 있다.")
    fun blackListTest() {
        // given
        val now = LocalDateTime.now()

        given(blackListReader.findAll()).willReturn(
            listOf(
                BlackList(1L, now, 1L)
            )
        )

        val expectedKst = now.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime()

        // when & then
        assertAll(
            { assertThat(blackListService.findBlackList().blackList.size).isEqualTo(1) },
            { assertThat(blackListService.findBlackList().blackList[0].blockedAt).isEqualTo(expectedKst) }
        )
    }

    @Test
    @DisplayName("사용자는 블랙리스트의 유저를 삭제할 수 있다.")
    fun deleteBlackListTest() {
        // given
        val blackListId = 1L
        doNothing().`when`(blackListWriter).deleteById(blackListId)

        // when
        blackListService.deleteBlackList(blackListId)

        // then
        verify(blackListWriter, times(1)).deleteById(blackListId)
    }
}
