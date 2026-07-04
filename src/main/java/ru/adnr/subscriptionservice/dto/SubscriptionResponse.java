package ru.adnr.subscriptionservice.dto;

import ru.adnr.subscriptionservice.entity.SubscriptionType;

import java.time.Instant;

public record SubscriptionResponse(
        String login,
        SubscriptionType subscriptionType,
        Instant expiresAt,
        boolean paidActive,
        boolean canUploadLargeFiles
) {
}
