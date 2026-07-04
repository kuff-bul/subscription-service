package ru.adnr.subscriptionservice.service;

import org.junit.jupiter.api.Test;
import ru.adnr.subscriptionservice.entity.Subscription;
import ru.adnr.subscriptionservice.entity.SubscriptionType;
import ru.adnr.subscriptionservice.repository.SubscriptionRepository;
import ru.adnr.subscriptionservice.service.impl.SubscriptionServiceImpl;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SubscriptionServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-07-04T00:00:00Z"), ZoneOffset.UTC);
    private final SubscriptionService subscriptionService = new SubscriptionServiceImpl(mock(SubscriptionRepository.class), clock);

    @Test
    void paidSubscriptionWithFutureExpirationAllowsLargeFiles() {
        Subscription subscription = subscription(SubscriptionType.PAID, Instant.parse("2026-07-05T00:00:00Z"));

        assertThat(subscriptionService.toResponse(subscription).paidActive()).isTrue();
        assertThat(subscriptionService.toResponse(subscription).canUploadLargeFiles()).isTrue();
    }

    @Test
    void paidSubscriptionWithPastExpirationDoesNotAllowLargeFiles() {
        Subscription subscription = subscription(SubscriptionType.PAID, Instant.parse("2026-07-03T23:59:59Z"));

        assertThat(subscriptionService.toResponse(subscription).paidActive()).isFalse();
        assertThat(subscriptionService.toResponse(subscription).canUploadLargeFiles()).isFalse();
    }

    @Test
    void freeSubscriptionDoesNotAllowLargeFiles() {
        Subscription subscription = subscription(SubscriptionType.FREE, null);

        assertThat(subscriptionService.toResponse(subscription).paidActive()).isFalse();
        assertThat(subscriptionService.toResponse(subscription).canUploadLargeFiles()).isFalse();
    }

    private Subscription subscription(SubscriptionType type, Instant expiresAt) {
        Subscription subscription = new Subscription();
        subscription.setLogin("user1");
        subscription.setSubscriptionType(type);
        subscription.setExpiresAt(expiresAt);
        return subscription;
    }
}
