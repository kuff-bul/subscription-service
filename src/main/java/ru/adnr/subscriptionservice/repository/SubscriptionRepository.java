package ru.adnr.subscriptionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.adnr.subscriptionservice.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
}
