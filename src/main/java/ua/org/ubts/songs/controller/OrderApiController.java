package ua.org.ubts.songs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.org.ubts.songs.converter.OrderConverter;
import ua.org.ubts.songs.dto.IdListDto;
import ua.org.ubts.songs.dto.OrderDto;
import ua.org.ubts.songs.service.OrderService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderConverter orderConverter;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public void createUserOrder(@RequestBody IdListDto idListDto, Principal principal) {
        orderService.createOrder(idListDto.getId(), principal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<OrderDto> getAllUserOrders() {
        return orderConverter.convertToDto(orderService.getOrders());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public void modifyUserOrder(@RequestBody OrderDto orderDto) {
        orderService.updateOrder(orderConverter.convertToEntity(orderDto));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public OrderDto getUserOrder(@PathVariable Long id, Authentication authentication) {
        return orderConverter.convertToDto(orderService.getOrder(id, authentication));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUserOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public List<OrderDto> getCurrentUserOrders(Principal principal) {
        return orderConverter.convertToDto(orderService.getOrders(principal));
    }

}
