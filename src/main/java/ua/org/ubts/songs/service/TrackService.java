package ua.org.ubts.songs.service;

import ua.org.ubts.songs.entity.TrackEntity;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface TrackService {

    TrackEntity getTrack(Long id);

    List<TrackEntity> getTracks(List<Long> id);

    List<TrackEntity> getTracks(Optional<List<Long>> id);

    boolean isTrackExists(String artist, String title);

    Long createTrack(TrackEntity trackEntity);

    void deleteTrack(Long id);

}
