package ua.org.ubts.songs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.org.ubts.songs.entity.TrackEntity;
import ua.org.ubts.songs.entity.UserEntity;
import ua.org.ubts.songs.exception.TrackNotFoundException;
import ua.org.ubts.songs.repository.OrderRepository;
import ua.org.ubts.songs.repository.TrackRepository;
import ua.org.ubts.songs.repository.UserRepository;
import ua.org.ubts.songs.service.TrackFileService;
import ua.org.ubts.songs.service.TrackService;
import ua.org.ubts.songs.service.UserService;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrackServiceImpl implements TrackService {

    private static final String TRACK_NOT_FOUND_MESSAGE = "Could not find track with id=";

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackFileService trackFileService;

    @Autowired
    private UserService userService;

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
    public List<TrackEntity> getUserTracks(Principal principal) {
        return userService.getUser(principal).getTracks();
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
        orderRepository.findAll().stream()
                .filter(o -> o.getTracks().stream().anyMatch(t -> t.getId().equals(id)))
                .forEach(orderRepository::delete);
        userRepository.findAll().stream()
                .filter(u -> u.getTracks().stream().anyMatch(t -> t.getId().equals(id)))
                        .forEach(u -> {
                            List<TrackEntity> tracks = u.getTracks();
                            tracks.removeIf(t -> t.getId().equals(id));
                            userRepository.save(u);
                        });
        trackRepository.deleteById(id);
    }

}
