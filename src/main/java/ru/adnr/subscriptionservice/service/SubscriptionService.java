package ru.adnr.subscriptionservice.service;

import ru.adnr.subscriptionservice.dto.SubscriptionResponse;
import ru.adnr.subscriptionservice.entity.Subscription;

public interface SubscriptionService {

    SubscriptionResponse getSubscription(String login);

    SubscriptionResponse toResponse(Subscription subscription);

    boolean isPaidActive(Subscription subscription);
}
