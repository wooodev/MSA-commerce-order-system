package com.commerce.notification.infrastructure.config

import com.commerce.notification.infrastructure.kafka.event.KafkaTopics
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
class KafkaConfig(private val kafkaTemplate: KafkaTemplate<String, String>) {

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>
    ): ConcurrentKafkaListenerContainerFactory<String, String> =
        ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            setConsumerFactory(consumerFactory)
            setCommonErrorHandler(
                DefaultErrorHandler(DeadLetterPublishingRecoverer(kafkaTemplate), FixedBackOff(1000L, 3L))
            )
        }

    @Bean fun notificationEventsDlqTopic() = NewTopic(KafkaTopics.NOTIFICATION_EVENTS_DLQ, 1, 1)
}
