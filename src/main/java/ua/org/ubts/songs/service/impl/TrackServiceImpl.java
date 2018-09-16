package ua.org.ubts.songs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.org.ubts.songs.entity.TrackEntity;
import ua.org.ubts.songs.exception.TrackNotFoundException;
import ua.org.ubts.songs.repository.TrackRepository;
import ua.org.ubts.songs.service.TrackFileService;
import ua.org.ubts.songs.service.TrackService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrackServiceImpl implements TrackService {

    private static final String TRACK_NOT_FOUND_MESSAGE = "Could not find track with id=";

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private TrackFileService trackFileService;

    @Override
    public TrackEntity getTrack(Long id) {
        return trackRepository.findById(id).orElseThrow(() ->
                new TrackNotFoundException(TRACK_NOT_FOUND_MESSAGE + id));
    }

    @Override
    public List<TrackEntity> getTracks(List<Long> id) {
        return trackRepository.findById(id);
    }

    @Override
    public List<TrackEntity> getTracks(Optional<List<Long>> id) {
        return id.map(trackRepository::findById).orElseGet(() -> trackRepository.findAll());
    }

    @Override
    public boolean isTrackExists(String artist, String title) {
        return trackRepository.findByArtistAndTitle(artist, title).isPresent();
    }

    @Override
    public Long createTrack(TrackEntity trackEntity) {
        return trackRepository.saveAndFlush(trackEntity).getId();
    }

    @Override
    public void deleteTrack(Long id) {
        trackRepository.findById(id).orElseThrow(() ->
                new TrackNotFoundException(TRACK_NOT_FOUND_MESSAGE + id));
        trackFileService.deleteTrack(id);
        trackRepository.deleteById(id);
    }

}
