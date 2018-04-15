package ua.org.ubts.songs.service;

import org.springframework.security.core.Authentication;
import ua.org.ubts.songs.entity.OrderEntity;

import java.security.Principal;
import java.util.List;

public interface OrderService {

    void createOrder(List<Long> id, Principal principal);

    List<OrderEntity> getOrders(Principal principal);

    OrderEntity getOrder(Long id, Authentication authentication);

    List<OrderEntity> getOrders();

    void updateOrder(OrderEntity orderEntity);

    void deleteOrder(Long id);

}
