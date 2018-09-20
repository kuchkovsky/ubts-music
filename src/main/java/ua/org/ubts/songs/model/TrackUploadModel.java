package ua.org.ubts.songs.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ua.org.ubts.songs.dto.TagDto;

import java.util.List;

@Data
public class TrackUploadModel {

    private String artist;
    private String title;
    private String sampleAudioUrl;
    private String tags;
    private MultipartFile sampleAudio;
    private MultipartFile audio;
    private MultipartFile pdfChords;
    private MultipartFile docChords;
    private MultipartFile notes;
    private MultipartFile presentation;

}
