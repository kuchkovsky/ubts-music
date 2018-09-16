package ua.org.ubts.songs.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.org.ubts.songs.service.SubscriptionService;

@Component
public class SubscriptionStatusCheckScheduleUtil {

    @Autowired
    private SubscriptionService subscriptionService;

    @Scheduled(cron = "0 0 8 * * *") // 8 o'clock of every day
    public void checkSubscriptions() {
        subscriptionService.checkSubscriptions();
    }

}
