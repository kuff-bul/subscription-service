package ru.adnr.subscriptionservice.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.adnr.subscriptionservice.entity.Subscription;
import ru.adnr.subscriptionservice.entity.SubscriptionType;
import ru.adnr.subscriptionservice.kafka.SubscriptionEventProducer;
import ru.adnr.subscriptionservice.repository.SubscriptionRepository;
import ru.adnr.subscriptionservice.service.impl.SubscriptionServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubscriptionServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-07-04T00:00:00Z"), ZoneOffset.UTC);
    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final SubscriptionEventProducer subscriptionEventProducer = mock(SubscriptionEventProducer.class);
    private final SubscriptionService subscriptionService = new SubscriptionServiceImpl(
            subscriptionRepository,
            subscriptionEventProducer,
            clock
    );

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

    @Test
    void expirePaidSubscriptionsChangesExpiredPaidSubscriptionsToFreeAndPublishesEvents() {
        Subscription expired = subscription(SubscriptionType.PAID, Instant.parse("2026-07-03T23:59:59Z"));
        when(subscriptionRepository.findBySubscriptionTypeAndExpiresAtLessThanEqual(
                SubscriptionType.PAID,
                Instant.parse("2026-07-04T00:00:00Z")
        )).thenReturn(List.of(expired));

        int expiredCount = subscriptionService.expirePaidSubscriptions();

        assertThat(expiredCount).isEqualTo(1);
        assertThat(expired.getSubscriptionType()).isEqualTo(SubscriptionType.FREE);
        verify(subscriptionEventProducer).publishSubscriptionExpired("user1");
    }

    @Test
    void expirePaidSubscriptionsDoesNothingWhenNoExpiredSubscriptionsFound() {
        when(subscriptionRepository.findBySubscriptionTypeAndExpiresAtLessThanEqual(
                SubscriptionType.PAID,
                Instant.parse("2026-07-04T00:00:00Z")
        )).thenReturn(List.of());

        int expiredCount = subscriptionService.expirePaidSubscriptions();

        assertThat(expiredCount).isZero();
        verify(subscriptionEventProducer, never()).publishSubscriptionExpired("user1");
    }

    private Subscription subscription(SubscriptionType type, Instant expiresAt) {
        Subscription subscription = new Subscription();
        subscription.setLogin("user1");
        subscription.setSubscriptionType(type);
        subscription.setExpiresAt(expiresAt);
        return subscription;
    }
}
