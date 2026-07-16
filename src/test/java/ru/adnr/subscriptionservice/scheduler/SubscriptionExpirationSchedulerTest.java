package ru.adnr.subscriptionservice.scheduler;

import org.junit.jupiter.api.Test;
import ru.adnr.subscriptionservice.service.SubscriptionService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubscriptionExpirationSchedulerTest {

    private final SubscriptionService subscriptionService = mock(SubscriptionService.class);
    private final SubscriptionExpirationScheduler scheduler = new SubscriptionExpirationScheduler(subscriptionService);

    @Test
    void expirePaidSubscriptionsDelegatesToService() {
        when(subscriptionService.expirePaidSubscriptions()).thenReturn(2);

        scheduler.expirePaidSubscriptions();

        verify(subscriptionService).expirePaidSubscriptions();
    }
}
