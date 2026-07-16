package ru.adnr.subscriptionservice.service.impl;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.adnr.subscriptionservice.dto.SubscriptionResponse;
import ru.adnr.subscriptionservice.entity.Subscription;
import ru.adnr.subscriptionservice.entity.SubscriptionType;
import ru.adnr.subscriptionservice.exception.SubscriptionNotFoundException;
import ru.adnr.subscriptionservice.kafka.SubscriptionEventProducer;
import ru.adnr.subscriptionservice.repository.SubscriptionRepository;
import ru.adnr.subscriptionservice.service.SubscriptionService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionEventProducer subscriptionEventProducer;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscription(String login) {
        Subscription subscription = subscriptionRepository.findById(login)
                .orElseThrow(() -> new SubscriptionNotFoundException(login));

        return toResponse(subscription);
    }

    @Override
    public SubscriptionResponse toResponse(Subscription subscription) {
        boolean paidActive = isPaidActive(subscription);
        return new SubscriptionResponse(
                subscription.getLogin(),
                subscription.getSubscriptionType(),
                subscription.getExpiresAt(),
                paidActive,
                paidActive
        );
    }

    @Override
    public boolean isPaidActive(Subscription subscription) {
        if (subscription.getSubscriptionType() != SubscriptionType.PAID) {
            return false;
        }

        Instant expiresAt = subscription.getExpiresAt();
        return expiresAt != null && expiresAt.isAfter(Instant.now(clock));
    }

    @Override
    @Transactional
    public int expirePaidSubscriptions() {
        Instant now = Instant.now(clock);
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findBySubscriptionTypeAndExpiresAtLessThanEqual(SubscriptionType.PAID, now);

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setSubscriptionType(SubscriptionType.FREE);
            subscriptionEventProducer.publishSubscriptionExpired(subscription.getLogin());
            log.info("Subscription expired. login={}, expiresAt={}", subscription.getLogin(), subscription.getExpiresAt());
        }

        return expiredSubscriptions.size();
    }
}
