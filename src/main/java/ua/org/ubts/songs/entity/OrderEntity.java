package ua.org.ubts.songs.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "order_")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "order_track",
            joinColumns = @JoinColumn(
                    name = "order_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "track_id", referencedColumnName = "id"))
    private List<TrackEntity> tracks;

    @Column(name = "is_pending")
    private boolean pending;

    @Column(name = "is_confirmed")
    private boolean confirmed;

}
