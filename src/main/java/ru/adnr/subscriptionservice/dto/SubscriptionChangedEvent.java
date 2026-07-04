package ru.adnr.subscriptionservice.dto;

public record SubscriptionChangedEvent(
        String login,
        String reason
) {
}
