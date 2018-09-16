package ua.org.ubts.songs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.org.ubts.songs.converter.TrackConverter;
import ua.org.ubts.songs.dto.TrackDto;
import ua.org.ubts.songs.service.TrackService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tracks")
public class TrackApiController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackConverter trackConverter;

    @GetMapping
    public List<TrackDto> getTracks(@RequestParam("id") Optional<List<Long>> id) {
        return trackConverter.convertToDto(trackService.getTracks(id));
    }

    @GetMapping("/{id}")
    public TrackDto getTrack(@PathVariable("id") Long id) {
        return trackConverter.convertToDto(trackService.getTrack(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteTrack(@PathVariable("id") Long id) {
        trackService.deleteTrack(id);
    }

}
