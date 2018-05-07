package ua.org.ubts.songs.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TrackUploadModel {

    private String artist;
    private String title;
    private double price;
    private String sampleAudioUrl;
    private MultipartFile sampleAudio;
    private MultipartFile audio;
    private MultipartFile pdfChords;
    private MultipartFile docChords;
    private MultipartFile notes;
    private MultipartFile presentation;

}
