package ua.org.ubts.songs.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionEntity extends BaseEntity<Long> {

    @Column(name = "active")
    private boolean active;

    @Column(name = "request_pending")
    private boolean requestPending;

    @Column(name = "expired")
    private boolean expired;

    @Column(name = "banned")
    private boolean banned;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "subscription")
    private UserEntity user;

}
