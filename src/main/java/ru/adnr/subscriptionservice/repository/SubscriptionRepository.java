package ru.adnr.subscriptionservice.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.adnr.subscriptionservice.entity.Subscription;
import ru.adnr.subscriptionservice.entity.SubscriptionType;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    List<Subscription> findBySubscriptionTypeAndExpiresAtLessThanEqual(SubscriptionType subscriptionType, Instant expiresAt);
}
