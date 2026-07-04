package ru.adnr.subscriptionservice.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.adnr.subscriptionservice.dto.SubscriptionResponse;
import ru.adnr.subscriptionservice.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class SubscriptionFacade {

    private final SubscriptionService subscriptionService;

    public SubscriptionResponse getSubscription(String login) {
        if (!StringUtils.hasText(login)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "login must not be blank");
        }

        return subscriptionService.getSubscription(login);
    }
}
