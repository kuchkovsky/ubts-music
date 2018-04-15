package ua.org.ubts.songs.service;

import org.springframework.security.core.Authentication;
import ua.org.ubts.songs.dto.TokenDto;

public interface TrackTokenService {

    TokenDto getDownloadToken(Long id, Authentication authentication);

    boolean verifyToken(String token);

}
