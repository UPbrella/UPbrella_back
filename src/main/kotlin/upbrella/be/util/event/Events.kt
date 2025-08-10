package upbrella.be.util.event

import org.springframework.context.ApplicationEventPublisher

object Events {
    private var publisher: ApplicationEventPublisher? = null

    fun setPublisher(publisher: ApplicationEventPublisher) {
        this.publisher = publisher
    }

    fun raise(event: Any) {
        publisher?.publishEvent(event)
            ?: throw IllegalStateException("ApplicationEventPublisher가 설정되지 않았습니다.")
    }
}
