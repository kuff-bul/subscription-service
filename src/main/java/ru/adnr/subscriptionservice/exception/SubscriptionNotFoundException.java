package ru.adnr.subscriptionservice.exception;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException(String login) {
        super("Subscription not found for login: " + login);
    }
}
