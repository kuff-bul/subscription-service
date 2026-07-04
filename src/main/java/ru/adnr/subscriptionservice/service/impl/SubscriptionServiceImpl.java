package ru.adnr.subscriptionservice.service.impl;

import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.adnr.subscriptionservice.dto.SubscriptionResponse;
import ru.adnr.subscriptionservice.entity.Subscription;
import ru.adnr.subscriptionservice.entity.SubscriptionType;
import ru.adnr.subscriptionservice.exception.SubscriptionNotFoundException;
import ru.adnr.subscriptionservice.repository.SubscriptionRepository;
import ru.adnr.subscriptionservice.service.SubscriptionService;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
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
}
