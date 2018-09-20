package ua.org.ubts.songs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
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
import ua.org.ubts.songs.entity.TagEntity;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TrackFileServiceImpl implements TrackFileService {

    private static final String TRACKS_DIRECTORY = "tracks";

    private static final String SAMPLE_AUDIO_FILENAME = "sampleAudio";
    private static final String AUDIO_FILENAME = "audio";
    private static final String PDF_CHORDS_FILENAME = "pdfChords";
    private static final String DOC_CHORDS_FILENAME = "docChords";
    private static final String NOTES_FILENAME = "notes";
    private static final String PRESENTATION_FILENAME = "presentation";

    private static final String INCORRECT_FORM_DATA_MESSAGE = "Incorrect form data";
    private static final String TRACK_ALREADY_EXISTS_MESSAGE = "Track already exists";
    private static final String SAMPLE_AUDIO_NOT_FOUND_MESSAGE = "Could not find sample audio for track with id=";
    private static final String AUDIO_NOT_FOUND_MESSAGE = "Could not find audio for track with id=";
    private static final String PDF_CHORDS_NOT_FOUND_MESSAGE = "Could not find pdf chords for track with id=";
    private static final String DOC_CHORDS_NOT_FOUND_MESSAGE = "Could not find doc chords for track with id=";
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
    public ResponseEntity<Resource> getPdfChords(Long id) {
        TrackFilesEntity trackFilesEntity = trackRepository.findById(id).map(TrackEntity::getFiles).orElseThrow(() ->
                new FileNotFoundException(PDF_CHORDS_NOT_FOUND_MESSAGE + id));
        String extension = trackFilesEntity.getPdfChordsExtension().getName();
        String mimeType = getMimeType(trackFilesEntity.getPdfChordsExtension());
        Path path = getPath(id, PDF_CHORDS_FILENAME, extension);
        String downloadType = " (" + PDF_CHORDS_FILENAME + ")";
        return buildResponseEntity(getFileName(id) + downloadType + extension, mimeType, path);
    }

    @Override
    public ResponseEntity<Resource> getDocChords(Long id) {
        TrackFilesEntity trackFilesEntity = trackRepository.findById(id).map(TrackEntity::getFiles).orElseThrow(() ->
                new FileNotFoundException(DOC_CHORDS_NOT_FOUND_MESSAGE + id));
        String extension = trackFilesEntity.getDocChordsExtension().getName();
        String mimeType = getMimeType(trackFilesEntity.getDocChordsExtension());
        Path path = getPath(id, DOC_CHORDS_FILENAME, extension);
        String downloadType = " (" + DOC_CHORDS_FILENAME + ")";
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
        Long id = saveTrackToDb(trackUploadModel, null);
        saveFiles(trackUploadModel, id);
    }

    @Override
    public void editTrack(TrackUploadModel trackUploadModel, Long id) {
        checkTrackUploadModel(trackUploadModel);
        saveTrackToDb(trackUploadModel, id);
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
            throw new IcorrectFormDataException(INCORRECT_FORM_DATA_MESSAGE);
        }
    }

    private void checkIfTrackExists(TrackUploadModel trackUploadModel) {
        if (trackService.isTrackExists(trackUploadModel.getArtist(), trackUploadModel.getTitle())) {
            throw new TrackAlreadyExistsException(TRACK_ALREADY_EXISTS_MESSAGE);
        }
    }

    private String getFileExtension(String fileName) {
        return FilenameUtils.EXTENSION_SEPARATOR + FilenameUtils.getExtension(fileName);
    }

    private FileExtensionEntity getFileExtensionEntity(String fileExtension) {
        return fileExtensionRepository.findByName(fileExtension)
                .orElseGet(() -> fileExtensionRepository.saveAndFlush(new FileExtensionEntity(fileExtension)));
    }

    private Long saveTrackToDb(TrackUploadModel trackUploadModel, Long id) {
        TrackFilesEntity trackFilesEntity = TrackFilesEntity.builder()
                .sampleAudioExtension(trackUploadModel.getSampleAudio() != null ?
                        getFileExtensionEntity(
                                getFileExtension(trackUploadModel.getSampleAudio().getOriginalFilename())) : null)
                .audioExtension(trackUploadModel.getAudio() != null ?
                        getFileExtensionEntity(
                                getFileExtension(trackUploadModel.getAudio().getOriginalFilename())) : null)
                .pdfChordsExtension(trackUploadModel.getPdfChords() != null ?
                        getFileExtensionEntity(
                                getFileExtension(trackUploadModel.getPdfChords().getOriginalFilename())): null)
                .docChordsExtension(trackUploadModel.getDocChords() != null ?
                        getFileExtensionEntity(
                                getFileExtension(trackUploadModel.getDocChords().getOriginalFilename())): null)
                .notesExtension(trackUploadModel.getNotes() != null ?
                        getFileExtensionEntity(
                                getFileExtension(trackUploadModel.getNotes().getOriginalFilename())): null)
                .presentationExtension(trackUploadModel.getPresentation() != null ?
                        getFileExtensionEntity(
                                getFileExtension(trackUploadModel.getPresentation().getOriginalFilename())): null)
                .build();
        if (id != null) {
            TrackFilesEntity trackFilesEntityFromDb = trackService.getTrack(id).getFiles();
            if (trackFilesEntity.getSampleAudioExtension() == null) {
                trackFilesEntity.setSampleAudioExtension(trackFilesEntityFromDb.getSampleAudioExtension());
            }
            if (trackFilesEntity.getAudioExtension() == null) {
                trackFilesEntity.setAudioExtension(trackFilesEntityFromDb.getAudioExtension());
            }
            if (trackFilesEntity.getDocChordsExtension() == null) {
                trackFilesEntity.setDocChordsExtension(trackFilesEntityFromDb.getDocChordsExtension());
            }
            if (trackFilesEntity.getPdfChordsExtension() == null) {
                trackFilesEntity.setPdfChordsExtension(trackFilesEntityFromDb.getPdfChordsExtension());
            }
            if (trackFilesEntity.getNotesExtension() == null) {
                trackFilesEntity.setNotesExtension(trackFilesEntityFromDb.getNotesExtension());
            }
            if (trackFilesEntity.getPresentationExtension() == null) {
                trackFilesEntity.setPresentationExtension(trackFilesEntityFromDb.getPresentationExtension());
            }
        }
        TrackEntity trackEntity = TrackEntity.builder()
                .artist(trackUploadModel.getArtist())
                .title(trackUploadModel.getTitle())
                .sampleAudioUrl(trackUploadModel.getSampleAudioUrl())
                .files(trackFilesEntity)
                .build();
        trackEntity.setId(id);
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<TagEntity> tagEntityList;
        try {
            tagEntityList = objectMapper
                    .readValue(trackUploadModel.getTags(), typeFactory.constructCollectionType(List.class, TagEntity.class));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        trackEntity.setTags(tagEntityList);
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
            if (trackUploadModel.getAudio() != null) {
                Files.write(getPath(id, AUDIO_FILENAME,
                        getFileExtension(trackUploadModel.getAudio().getOriginalFilename())),
                        trackUploadModel.getAudio().getBytes());
            }
            if (trackUploadModel.getPdfChords() != null) {
                Files.write(getPath(id, PDF_CHORDS_FILENAME,
                        getFileExtension(trackUploadModel.getPdfChords().getOriginalFilename())),
                        trackUploadModel.getPdfChords().getBytes());
            }
            if (trackUploadModel.getDocChords() != null) {
                Files.write(getPath(id, DOC_CHORDS_FILENAME,
                        getFileExtension(trackUploadModel.getDocChords().getOriginalFilename())),
                        trackUploadModel.getDocChords().getBytes());
            }
            if (trackUploadModel.getNotes() != null) {
                Files.write(getPath(id, NOTES_FILENAME,
                        getFileExtension(trackUploadModel.getNotes().getOriginalFilename())),
                        trackUploadModel.getNotes().getBytes());
            }
            if (trackUploadModel.getPresentation() != null) {
                Files.write(getPath(id, PRESENTATION_FILENAME,
                        getFileExtension(trackUploadModel.getPresentation().getOriginalFilename())),
                        trackUploadModel.getPresentation().getBytes());
            }
        } catch (IOException e) {
            log.error(WRITE_FILE_ERROR_MESSAGE, e);
            throw new FileWriteException(WRITE_FILE_ERROR_MESSAGE);
        }
    }

    private ResponseEntity<Resource> buildResponseEntity(String fileName, String mimeType, Path path) {
        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment;filename=" + UriUtils.encodePath(fileName.replace(",", "_"),"UTF-8"))
                    .contentType(MediaType.parseMediaType(mimeType)).contentLength(Files.size(path))
                    .body(new PathResource(path));
        } catch (IOException e) {
            log.error(READ_FILE_ERROR_MESSAGE, e);
            throw new FileReadException(READ_FILE_ERROR_MESSAGE);
        }
    }

}
