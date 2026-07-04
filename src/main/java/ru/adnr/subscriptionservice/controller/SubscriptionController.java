package ru.adnr.subscriptionservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.adnr.subscriptionservice.dto.SubscriptionResponse;
import ru.adnr.subscriptionservice.facade.SubscriptionFacade;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionFacade subscriptionFacade;

    @GetMapping("/{login}")
    public SubscriptionResponse getSubscription(@PathVariable String login) {
        return subscriptionFacade.getSubscription(login);
    }
}
