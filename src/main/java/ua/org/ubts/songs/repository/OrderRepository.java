package ua.org.ubts.songs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.org.ubts.songs.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
