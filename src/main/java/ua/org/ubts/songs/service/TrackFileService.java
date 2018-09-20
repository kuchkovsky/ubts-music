package ua.org.ubts.songs.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import ua.org.ubts.songs.model.TrackUploadModel;

public interface TrackFileService {

    ResponseEntity<Resource> getSampleAudio(Long id);

    ResponseEntity<Resource> getPdfChords(Long id);

    ResponseEntity<Resource> getDocChords(Long id);

    ResponseEntity<Resource> getNotes(Long id);

    ResponseEntity<Resource> getPresentation(Long id);

    void saveTrack(TrackUploadModel trackUploadModel);

    void editTrack(TrackUploadModel trackUploadModel, Long id);

    void deleteTrack(Long id);

}
