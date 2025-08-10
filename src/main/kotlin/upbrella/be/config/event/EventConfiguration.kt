package upbrella.be.config.event

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import upbrella.be.util.event.Events

@Configuration
class EventsConfiguration(
    private val applicationContext: ApplicationContext
) {

    @Bean
    fun eventsInitializer(): InitializingBean {
        return InitializingBean { Events.setPublisher(applicationContext) }
    }
}
