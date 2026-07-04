package ru.adnr.subscriptionservice.dto;

public record ErrorResponse(
        String code,
        String message
) {
}
