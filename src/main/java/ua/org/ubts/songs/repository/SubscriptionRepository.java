package ua.org.ubts.songs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.org.ubts.songs.entity.SubscriptionEntity;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
}
