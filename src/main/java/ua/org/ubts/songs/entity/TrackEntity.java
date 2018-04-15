package ua.org.ubts.songs.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Entity
@Table(name = "track")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(name = "artist", nullable = false)
    private String artist;

    @NotEmpty
    @Column(name = "title", nullable = false)
    private String title;

    @Digits(integer = 5, fraction = 2)
    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "sample_audio_url")
    private String sampleAudioUrl;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "files_id")
    private TrackFilesEntity files;

}
