package upbrella.be.config.event

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher

/**
 * 테스트 전용 No-Operation Publisher
 * 실제로 아무 이벤트도 발행하지 않음
 */
object NoOpPublisher : ApplicationEventPublisher {
    override fun publishEvent(event: Any) { /* no-op */ }
    override fun publishEvent(event: ApplicationEvent) { /* no-op */ }
}
