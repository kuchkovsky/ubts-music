package ua.org.ubts.songs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.org.ubts.songs.converter.SubscriptionConverter;
import ua.org.ubts.songs.dto.SubscriptionDto;
import ua.org.ubts.songs.dto.SubscriptionRequestDto;
import ua.org.ubts.songs.service.SubscriptionService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionConverter subscriptionConverter;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<SubscriptionDto> getSubscriptions() {
        return subscriptionConverter.convertToDto(subscriptionService.getSubscriptions());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public SubscriptionDto getSubscription(Principal principal) {
        return subscriptionConverter.convertToDto(subscriptionService.getSubscription(principal));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/requests")
    public void requestSubscription(@RequestBody SubscriptionRequestDto subscriptionRequestDto, Principal principal) {
        subscriptionService.requestSubscription(subscriptionRequestDto, principal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/requests/{userEmail}")
    public void updateSubscription(@RequestBody SubscriptionRequestDto subscriptionRequestDto, @PathVariable String userEmail) {
        subscriptionService.updateSubscription(subscriptionRequestDto, userEmail);
    }

}
