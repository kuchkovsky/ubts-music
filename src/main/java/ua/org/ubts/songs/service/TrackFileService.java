package ua.org.ubts.songs.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import ua.org.ubts.songs.model.TrackUploadModel;

public interface TrackFileService {

    ResponseEntity<Resource> getSampleAudio(Long id);

    ResponseEntity<Resource> getAudio(Long id);

    ResponseEntity<Resource> getChords(Long id);

    ResponseEntity<Resource> getNotes(Long id);

    ResponseEntity<Resource> getPresentation(Long id);

    void saveTrack(TrackUploadModel trackUploadModel);

    void deleteTrack(Long id);

}
