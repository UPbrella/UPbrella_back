package upbrella.be.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackages = ["upbrella.be.payment.external"])
class OpenFeignConfig {

}