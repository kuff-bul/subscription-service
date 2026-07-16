package ru.adnr.subscriptionservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.adnr.subscriptionservice.dto.SubscriptionResponse;
import ru.adnr.subscriptionservice.facade.SubscriptionFacade;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionFacade subscriptionFacade;

    @GetMapping("/api/subscriptions/{login}")
    public SubscriptionResponse getSubscription(@PathVariable String login) {
        return subscriptionFacade.getSubscription(login);
    }
}
