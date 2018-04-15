package ua.org.ubts.songs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.org.ubts.songs.entity.OrderEntity;
import ua.org.ubts.songs.entity.TrackEntity;
import ua.org.ubts.songs.entity.UserEntity;
import ua.org.ubts.songs.exception.AccessViolationException;
import ua.org.ubts.songs.exception.OrderNotFoundException;
import ua.org.ubts.songs.repository.OrderRepository;
import ua.org.ubts.songs.service.OrderService;
import ua.org.ubts.songs.service.TrackService;
import ua.org.ubts.songs.service.UserService;
import ua.org.ubts.songs.util.AuthUtil;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final String ACCESS_VIOLATION_MESSAGE = "Access Denied. You don't have permission to view this order";
    private static final String ORDER_NOT_FOUND_MESSAGE = "Could not find order with id=";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TrackService trackService;

    @Override
    public void createOrder(List<Long> id, Principal principal) {
        UserEntity user = userService.getUser(principal);
        List<TrackEntity> tracks = trackService.getTracks(id);
        orderRepository.save(OrderEntity.builder().user(user).tracks(tracks).pending(true).build());
    }

    @Override
    public List<OrderEntity> getOrders(Principal principal) {
        return userService.getUser(principal).getOrders();
    }

    @Override
    public OrderEntity getOrder(Long id, Authentication authentication) {
        if (!AuthUtil.isAdmin(authentication)) {
            UserEntity userEntity = userService.getUser(authentication);
            boolean hasOrder = userEntity.getOrders().stream().anyMatch(o -> o.getId().equals(id));
            if (!hasOrder) {
                throw new AccessViolationException(ACCESS_VIOLATION_MESSAGE);
            }
        }
        return orderRepository.findById(id).orElseThrow(() ->
                new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));
    }

    @Override
    public List<OrderEntity> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void updateOrder(OrderEntity orderEntity) {
        OrderEntity order = orderRepository.findById(orderEntity.getId()).orElseThrow(() ->
                new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + orderEntity.getId()));
        if (orderEntity.isConfirmed()) {
            order.getUser().setTracks(new ArrayList<>(order.getTracks()));
            order.setConfirmed(true);
        }
        order.setPending(orderEntity.isPending());
        orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() ->
                new OrderNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));
        UserEntity user = order.getUser();
        user.getTracks().removeAll(order.getTracks());
        userService.updateUser(user);
        orderRepository.deleteById(id);
    }

}
