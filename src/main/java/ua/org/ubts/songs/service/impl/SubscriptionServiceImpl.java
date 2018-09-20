package ua.org.ubts.songs.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ua.org.ubts.songs.dto.SubscriptionRequestDto;
import ua.org.ubts.songs.entity.SubscriptionEntity;
import ua.org.ubts.songs.entity.UserEntity;
import ua.org.ubts.songs.repository.SubscriptionRepository;
import ua.org.ubts.songs.service.SubscriptionService;
import ua.org.ubts.songs.service.UserService;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    public JavaMailSender emailSender;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Override
    public List<SubscriptionEntity> getSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Override
    public SubscriptionEntity getSubscription(Principal principal) {
        return userService.getUser(principal).getSubscription();
    }

    @Override
    public void updateSubscription(SubscriptionRequestDto subscriptionRequestDto, String userEmail) {
        SubscriptionEntity subscriptionEntity = userService.getUser(userEmail).getSubscription();
        subscriptionEntity.setActive(subscriptionRequestDto.isActivate());
        subscriptionEntity.setRequestPending(false);
        if (subscriptionRequestDto.isActivate()) {
            subscriptionEntity.setExpirationDate(LocalDate.now().plusYears(1));
            subscriptionEntity.setExpired(false);
            subscriptionEntity.setBanned(false);
        } else {
            subscriptionEntity.setBanned(true);
        }
        subscriptionRepository.save(subscriptionEntity);
    }

    @Override
    public void requestSubscription(SubscriptionRequestDto subscriptionRequestDto, Principal principal) {
        SubscriptionEntity subscriptionEntity = getSubscription(principal);
        if (subscriptionRequestDto.isActivate() && !subscriptionEntity.isActive() && !subscriptionEntity.isBanned()) {
            subscriptionEntity.setRequestPending(true);
            UserEntity userEntity = subscriptionEntity.getUser();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("Нова заявка на активацію підписки");
            message.setText("Користувач: " + userEntity.getFirstName() + " " + userEntity.getLastName());
            emailSender.send(message);
        } else if (!subscriptionRequestDto.isActivate()) {
            subscriptionEntity.setActive(false);
            subscriptionEntity.setRequestPending(false);
        }
        subscriptionRepository.save(subscriptionEntity);
    }

    @Override
    public void checkSubscriptions() {
        log.info("Checking for expired subscriptions...");
        subscriptionRepository.findAll().forEach(subscriptionEntity -> {
            if (subscriptionEntity.getExpirationDate() != null) {
                if (!subscriptionEntity.isExpired() && subscriptionEntity.getExpirationDate().isBefore(LocalDate.now())) {
                    subscriptionEntity.setExpired(true);
                    subscriptionEntity.setActive(false);
                    subscriptionRepository.save(subscriptionEntity);
                    UserEntity userEntity = subscriptionEntity.getUser();
                    log.info("Blocked expired subscription for user {} {}", userEntity.getFirstName(), userEntity.getLastName());
                }
            }
        });
        log.info("Subscription check finished");
    }

}
