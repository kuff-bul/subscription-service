package ru.adnr.subscriptionservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.adnr.subscriptionservice.service.SubscriptionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionExpirationScheduler {

    private final SubscriptionService subscriptionService;

    @Scheduled(fixedDelayString = "${app.subscriptions.expiration-check-fixed-delay-ms:60000}")
    @SchedulerLock(
            name = "subscriptionExpirationScheduler",
            lockAtMostFor = "${app.subscriptions.expiration-lock-at-most-for:PT5M}",
            lockAtLeastFor = "${app.subscriptions.expiration-lock-at-least-for:PT5S}"
    )
    public void expirePaidSubscriptions() {
        int expiredCount = subscriptionService.expirePaidSubscriptions();
        if (expiredCount > 0) {
            log.info("Expired paid subscriptions. count={}", expiredCount);
        }
    }
}
