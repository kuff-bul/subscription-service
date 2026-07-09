package ru.adnr.subscriptionservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.adnr.subscriptionservice.config.KafkaProperties;
import ru.adnr.subscriptionservice.dto.SubscriptionChangedEvent;
import ru.adnr.subscriptionservice.exception.KafkaPublishException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionEventProducer {

    private static final String SUBSCRIPTION_EXPIRED = "SUBSCRIPTION_EXPIRED";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaProperties kafkaProperties;

    public void publishSubscriptionExpired(String login) {
        SubscriptionChangedEvent event = new SubscriptionChangedEvent(login, SUBSCRIPTION_EXPIRED);
        String payload = toJson(event);
        kafkaTemplate.send(kafkaProperties.subscriptionEventsTopic(), login, payload);
        log.info("Published subscription event. login={}, reason={}", login, SUBSCRIPTION_EXPIRED);
    }

    private String toJson(SubscriptionChangedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new KafkaPublishException("Failed to serialize subscription event. login=" + event.login(), exception);
        }
    }
}
