package ua.org.ubts.songs.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "track")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackEntity extends BaseEntity<Long> {

    @NotEmpty
    @Column(name = "artist", nullable = false)
    private String artist;

    @NotEmpty
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "sample_audio_url")
    private String sampleAudioUrl;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "track_tag",
            joinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "track_id", referencedColumnName = "id"))
    private List<TagEntity> tags;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "files_id")
    private TrackFilesEntity files;

}
