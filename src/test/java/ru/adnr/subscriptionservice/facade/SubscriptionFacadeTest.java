package ru.adnr.subscriptionservice.facade;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.adnr.subscriptionservice.service.SubscriptionService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class SubscriptionFacadeTest {

    private final SubscriptionFacade subscriptionFacade = new SubscriptionFacade(mock(SubscriptionService.class));

    @Test
    void blankLoginIsRejectedBeforeServiceLayer() {
        assertThatThrownBy(() -> subscriptionFacade.getSubscription(" "))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("login must not be blank");
    }
}
