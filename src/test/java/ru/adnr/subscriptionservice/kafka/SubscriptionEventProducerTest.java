package ru.adnr.subscriptionservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import ru.adnr.subscriptionservice.config.KafkaProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SubscriptionEventProducerTest {

    private final KafkaTemplate<String, String> kafkaTemplate = kafkaTemplate();
    private final SubscriptionEventProducer producer = new SubscriptionEventProducer(
            kafkaTemplate,
            new ObjectMapper(),
            new KafkaProperties("subscription-events")
    );

    @Test
    void publishSubscriptionExpiredSendsKafkaEvent() {
        producer.publishSubscriptionExpired("user1");

        verify(kafkaTemplate).send(
                "subscription-events",
                "user1",
                "{\"login\":\"user1\",\"reason\":\"SUBSCRIPTION_EXPIRED\"}"
        );
    }

    @SuppressWarnings("unchecked")
    private KafkaTemplate<String, String> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}
