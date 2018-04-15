package ua.org.ubts.songs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.org.ubts.songs.dto.TokenDto;
import ua.org.ubts.songs.service.TrackTokenService;

@RestController
@RequestMapping("/api/tokens/tracks")
public class TrackTokenApiController {

    @Autowired
    private TrackTokenService trackTokenService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public TokenDto getDownloadToken(@PathVariable("id") Long id, Authentication authentication) {
        return trackTokenService.getDownloadToken(id, authentication);
    }

}
