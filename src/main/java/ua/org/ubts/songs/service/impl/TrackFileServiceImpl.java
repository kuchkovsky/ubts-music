package ua.org.ubts.songs.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;
import ua.org.ubts.songs.entity.FileExtensionEntity;
import ua.org.ubts.songs.entity.TrackEntity;
import ua.org.ubts.songs.entity.TrackFilesEntity;
import ua.org.ubts.songs.exception.*;
import ua.org.ubts.songs.model.TrackUploadModel;
import ua.org.ubts.songs.repository.FileExtensionRepository;
import ua.org.ubts.songs.repository.TrackRepository;
import ua.org.ubts.songs.service.TrackFileService;
import ua.org.ubts.songs.service.TrackService;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Transactional
@Slf4j
public class TrackFileServiceImpl implements TrackFileService {

    private static final String TRACKS_DIRECTORY = "tracks";

    private static final String SAMPLE_AUDIO_FILENAME = "sampleAudio";
    private static final String AUDIO_FILENAME = "audio";
    private static final String CHORDS_FILENAME = "chords";
    private static final String NOTES_FILENAME = "notes";
    private static final String PRESENTATION_FILENAME = "presentation";

    private static final String SAMPLE_AUDIO_NOT_FOUND_MESSAGE = "Could not find sample audio for track with id=";
    private static final String AUDIO_NOT_FOUND_MESSAGE = "Could not find audio for track with id=";
    private static final String CHORDS_NOT_FOUND_MESSAGE = "Could not find chords for track with id=";
    private static final String NOTES_NOT_FOUND_MESSAGE = "Could not find notes for track with id=";
    private static final String PRESENTATION_NOT_FOUND_MESSAGE = "Could not find presentation for track with id=";
    private static final String CREATE_TRACK_ERROR_MESSAGE = "Could not save track info in database";
    private static final String READ_FILE_ERROR_MESSAGE = "Could not read requested file";
    private static final String WRITE_FILE_ERROR_MESSAGE = "Could not save files on server";
    private static final String TRACK_FILE_DELETE_ERROR_MESSAGE = "Could not delete files for track with id=";

    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private FileExtensionRepository fileExtensionRepository;

    @Autowired
    private TrackService trackService;

    @Autowired
    private String appDirectory;

    @Override
    public ResponseEntity<Resource> getSampleAudio(Long id) {
        TrackFilesEntity trackFilesEntity = trackRepository.findById(id).map(TrackEntity::getFiles).orElseThrow(() ->
                new FileNotFoundException(SAMPLE_AUDIO_NOT_FOUND_MESSAGE + id));
        if (trackFilesEntity.getSampleAudioExtension() == null) {
            throw new FileNotFoundException(SAMPLE_AUDIO_NOT_FOUND_MESSAGE + id);
        }
        String extension = trackFilesEntity.getSampleAudioExtension().getName();
        String mimeType = getMimeType(trackFilesEntity.getSampleAudioExtension());
        Path path = getPath(id, SAMPLE_AUDIO_FILENAME, extension);
        String downloadType = " (" + SAMPLE_AUDIO_FILENAME + ")";
        return buildResponseEntity(getFileName(id) + downloadType + extension, mimeType, path);
    }

    @Override
    public ResponseEntity<Resource> getAudio(Long id) {
        TrackFilesEntity trackFilesEntity = trackRepository.findById(id).map(TrackEntity::getFiles).orElseThrow(() ->
                new FileNotFoundException(AUDIO_NOT_FOUND_MESSAGE + id));
        String extension = trackFilesEntity.getAudioExtension().getName();
        String mimeType = getMimeType(trackFilesEntity.getAudioExtension());
        Path path = getPath(id, AUDIO_FILENAME, extension);
        String downloadType = " (" + AUDIO_FILENAME + ")";
        return buildResponseEntity(getFileName(id) + downloadType + extension, mimeType, path);
    }

    @Override
    public ResponseEntity<Resource> getChords(Long id) {
        TrackFilesEntity trackFilesEntity = trackRepository.findById(id).map(TrackEntity::getFiles).orElseThrow(() ->
                new FileNotFoundException(CHORDS_NOT_FOUND_MESSAGE + id));
        String extension = trackFilesEntity.getChordsExtension().getName();
        String mimeType = getMimeType(trackFilesEntity.getChordsExtension());
        Path path = getPath(id, CHORDS_FILENAME, extension);
        String downloadType = " (" + CHORDS_FILENAME + ")";
        return buildResponseEntity(getFileName(id) + downloadType + extension, mimeType, path);
    }

    @Override
    public ResponseEntity<Resource> getNotes(Long id) {
        TrackFilesEntity trackFilesEntity = trackRepository.findById(id).map(TrackEntity::getFiles).orElseThrow(() ->
                new FileNotFoundException(NOTES_NOT_FOUND_MESSAGE + id));
        String extension = trackFilesEntity.getNotesExtension().getName();
        String mimeType = getMimeType(trackFilesEntity.getNotesExtension());
        Path path = getPath(id, NOTES_FILENAME, extension);
        String downloadType = " (" + NOTES_FILENAME + ")";
        return buildResponseEntity(getFileName(id) + downloadType + extension, mimeType, path);
    }

    @Override
    public ResponseEntity<Resource> getPresentation(Long id) {
        TrackFilesEntity trackFilesEntity = trackRepository.findById(id).map(TrackEntity::getFiles).orElseThrow(() ->
                new FileNotFoundException(PRESENTATION_NOT_FOUND_MESSAGE + id));
        String extension = trackFilesEntity.getPresentationExtension().getName();
        String mimeType = getMimeType(trackFilesEntity.getPresentationExtension());
        Path path = getPath(id, PRESENTATION_FILENAME, extension);
        String downloadType = " (" + PRESENTATION_FILENAME + ")";
        return buildResponseEntity(getFileName(id) + downloadType + extension, mimeType, path);
    }

    @Override
    public void saveTrack(TrackUploadModel trackUploadModel) {
        checkTrackUploadModel(trackUploadModel);
        checkIfTrackExists(trackUploadModel);
        Long id = saveTrackToDb(trackUploadModel);
        saveFiles(trackUploadModel, id);
    }

    @Override
    public void deleteTrack(Long id) {
        try {
            FileUtils.deleteDirectory(new File(getTrackDirectory(id)));
        } catch (IOException e) {
            log.error(TRACK_FILE_DELETE_ERROR_MESSAGE + id, e);
            throw new FileDeleteException(TRACK_FILE_DELETE_ERROR_MESSAGE + id);
        }
    }

    private String getTrackDirectory(Long id) {
        return appDirectory + File.separator + TRACKS_DIRECTORY + File.separator + id;
    }

    private String getFileName(Long id) {
        TrackEntity trackEntity = trackService.getTrack(id);
        return trackEntity.getArtist() + " - " + trackEntity.getTitle();
    }

    private String getMimeType(FileExtensionEntity fileExtensionEntity) {
        return fileExtensionEntity.getMimeType() != null ? fileExtensionEntity.getMimeType() : DEFAULT_MIME_TYPE;
    }

    private Path getPath(Long id, String fileName, String extension) {
        return Paths.get(getTrackDirectory(id) + File.separator + fileName + extension);
    }

    private void checkTrackUploadModel(TrackUploadModel trackUploadModel) {
        if (StringUtils.isEmpty(trackUploadModel.getArtist()) || StringUtils.isEmpty(trackUploadModel.getTitle())) {
            throw new IcorrectFormDataException();
        }
    }

    private void checkIfTrackExists(TrackUploadModel trackUploadModel) {
        if (trackService.isTrackExists(trackUploadModel.getArtist(), trackUploadModel.getTitle())) {
            throw new TrackAlreadyExistsException();
        }
    }

    private String getFileExtension(String fileName) {
        return FilenameUtils.EXTENSION_SEPARATOR + FilenameUtils.getExtension(fileName);
    }

    private FileExtensionEntity getFileExtensionEntity(String fileExtension) {
        return fileExtensionRepository.findByName(fileExtension)
                .orElseGet(() -> fileExtensionRepository.saveAndFlush(new FileExtensionEntity(fileExtension)));
    }

    private Long saveTrackToDb(TrackUploadModel trackUploadModel) {
        TrackFilesEntity trackFilesEntity = TrackFilesEntity.builder()
                .sampleAudioExtension(trackUploadModel.getSampleAudio() != null ?
                        getFileExtensionEntity(
                                getFileExtension(trackUploadModel.getSampleAudio().getOriginalFilename())) : null)
                .audioExtension(getFileExtensionEntity(
                        getFileExtension(trackUploadModel.getAudio().getOriginalFilename())))
                .chordsExtension(getFileExtensionEntity(
                        getFileExtension(trackUploadModel.getChords().getOriginalFilename())))
                .notesExtension(getFileExtensionEntity(
                        getFileExtension(trackUploadModel.getNotes().getOriginalFilename())))
                .presentationExtension(getFileExtensionEntity(
                        getFileExtension(trackUploadModel.getPresentation().getOriginalFilename())))
                .build();
        TrackEntity trackEntity = TrackEntity.builder()
                .artist(trackUploadModel.getArtist())
                .title(trackUploadModel.getTitle())
                .price(BigDecimal.valueOf(trackUploadModel.getPrice()))
                .sampleAudioUrl(trackUploadModel.getSampleAudioUrl())
                .files(trackFilesEntity)
                .build();
        return trackService.createTrack(trackEntity);
    }

    private void saveFiles(TrackUploadModel trackUploadModel, Long id) {
        if (id == null) {
            log.error(CREATE_TRACK_ERROR_MESSAGE);
            throw new TrackCreateException(CREATE_TRACK_ERROR_MESSAGE);
        }
        try {
            Files.createDirectories(Paths.get(getTrackDirectory(id)));
            if (trackUploadModel.getSampleAudio() != null) {
                Files.write(getPath(id, SAMPLE_AUDIO_FILENAME,
                        getFileExtension(trackUploadModel.getSampleAudio().getOriginalFilename())),
                        trackUploadModel.getSampleAudio().getBytes());
            }
            Files.write(getPath(id, AUDIO_FILENAME,
                    getFileExtension(trackUploadModel.getAudio().getOriginalFilename())),
                    trackUploadModel.getAudio().getBytes());
            Files.write(getPath(id, CHORDS_FILENAME,
                    getFileExtension(trackUploadModel.getChords().getOriginalFilename())),
                    trackUploadModel.getChords().getBytes());
            Files.write(getPath(id, NOTES_FILENAME,
                    getFileExtension(trackUploadModel.getNotes().getOriginalFilename())),
                    trackUploadModel.getNotes().getBytes());
            Files.write(getPath(id, PRESENTATION_FILENAME,
                    getFileExtension(trackUploadModel.getPresentation().getOriginalFilename())),
                    trackUploadModel.getPresentation().getBytes());
        } catch (IOException e) {
            log.error(WRITE_FILE_ERROR_MESSAGE, e);
            throw new FileWriteException(WRITE_FILE_ERROR_MESSAGE);
        }
    }

    private ResponseEntity<Resource> buildResponseEntity(String fileName, String mimeType, Path path) {
        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment;filename=" + UriUtils.encodePath(fileName,"UTF-8"))
                    .contentType(MediaType.parseMediaType(mimeType)).contentLength(Files.size(path))
                    .body(new PathResource(path));
        } catch (IOException e) {
            log.error(READ_FILE_ERROR_MESSAGE, e);
            throw new FileReadException(READ_FILE_ERROR_MESSAGE);
        }
    }

}
