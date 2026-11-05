package com.commerce.order.infrastructure.config

import com.commerce.order.infrastructure.kafka.event.KafkaTopics
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConfig(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            setConsumerFactory(consumerFactory)
            // 1초 간격 3번 재시도 후 DLQ로 이동
            setCommonErrorHandler(
                DefaultErrorHandler(
                    DeadLetterPublishingRecoverer(kafkaTemplate),
                    FixedBackOff(1000L, 3L)
                )
            )
        }
    }

    // 토픽 자동 생성 (로컬 개발용)
    @Bean fun paymentEventsDlqTopic() = NewTopic(KafkaTopics.PAYMENT_EVENTS_DLQ, 1, 1)
    @Bean fun stockEventsDlqTopic() = NewTopic(KafkaTopics.STOCK_EVENTS_DLQ, 1, 1)
}
