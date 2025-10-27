import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// Students Service (Producer)
@Configuration
class RabbitMQConfig {
    @Bean
    fun studentQueue(): Queue {
        return Queue("student-queue", true)
    }
}