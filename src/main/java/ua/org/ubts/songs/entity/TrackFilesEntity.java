package ua.org.ubts.songs.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "track_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackFilesEntity extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sample_audio_ext_id")
    private FileExtensionEntity sampleAudioExtension;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "audio_ext_id")
    private FileExtensionEntity audioExtension;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pdf_chords_ext_id")
    private FileExtensionEntity pdfChordsExtension;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "doc_chords_ext_id")
    private FileExtensionEntity docChordsExtension;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "notes_ext_id")
    private FileExtensionEntity notesExtension;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "presentation_id")
    private FileExtensionEntity presentationExtension;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "files")
    private TrackEntity track;

}
