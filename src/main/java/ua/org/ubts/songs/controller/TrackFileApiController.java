package ua.org.ubts.songs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.org.ubts.songs.model.TrackUploadModel;
import ua.org.ubts.songs.service.TrackFileService;

@RestController
@RequestMapping("/api/files/tracks")
public class TrackFileApiController {

    @Autowired
    private TrackFileService trackFileService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void uploadTrackFiles(@ModelAttribute TrackUploadModel trackUploadModel) {
        trackFileService.saveTrack(trackUploadModel);
    }

    @GetMapping("/sample/{id}")
    public ResponseEntity<Resource> getTrackSampleAudio(@PathVariable("id") Long id) {
        return trackFileService.getSampleAudio(id);
    }

    @GetMapping("/audio/{id}")
    public ResponseEntity<Resource> getTrackAudio(@PathVariable("id") Long id) {
        return trackFileService.getAudio(id);
    }

    @GetMapping("/chords/pdf/{id}")
    public ResponseEntity<Resource> getTrackPdfChords(@PathVariable("id") Long id) {
        return trackFileService.getPdfChords(id);
    }

    @GetMapping("/chords/doc/{id}")
    public ResponseEntity<Resource> getTrackDocChords(@PathVariable("id") Long id) {
        return trackFileService.getDocChords(id);
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<Resource> getTrackNotes(@PathVariable("id") Long id) {
        return trackFileService.getNotes(id);
    }

    @GetMapping("/presentation/{id}")
    public ResponseEntity<Resource> getTrackPresentation(@PathVariable("id") Long id) {
        return trackFileService.getPresentation(id);
    }

}
