package ua.org.ubts.songs.service;

import ua.org.ubts.songs.dto.SubscriptionRequestDto;
import ua.org.ubts.songs.entity.SubscriptionEntity;

import java.security.Principal;
import java.util.List;

public interface SubscriptionService {

    List<SubscriptionEntity> getSubscriptions();

    SubscriptionEntity getSubscription(Principal principal);

    void updateSubscription(SubscriptionRequestDto subscriptionRequestDto, String userEmail);

    void requestSubscription(SubscriptionRequestDto subscriptionRequestDto, Principal principal);

    void checkSubscriptions();

}
