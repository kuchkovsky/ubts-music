package ua.org.ubts.songs.service.impl;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.org.ubts.songs.dto.TokenDto;
import ua.org.ubts.songs.entity.UserEntity;
import ua.org.ubts.songs.exception.SubscriptionNotActivatedException;
import ua.org.ubts.songs.service.TrackTokenService;
import ua.org.ubts.songs.service.UserService;
import ua.org.ubts.songs.util.AuthUtil;

import javax.crypto.SecretKey;
import javax.transaction.Transactional;

import java.util.Date;

@Service
@Transactional
public class TrackTokenServiceImpl implements TrackTokenService {

    private static final String SUBSCRIPTION_NOT_ACTIVATED_MESSAGE = "You don't have permission to obtain this token. "
            + "Please activate your subscription first.";

    private static final long TOKEN_EXPIRATION_TIME = 2000; // 2s

    @Autowired
    private UserService userService;

    @Autowired
    private SecretKey secretKey;

    @Override
    public TokenDto getDownloadToken(Long id, Authentication authentication) {
        UserEntity userEntity = userService.getUser(authentication);
        if (!AuthUtil.isAdmin(authentication) && !userEntity.getSubscription().isActive()) {
            throw new SubscriptionNotActivatedException(SUBSCRIPTION_NOT_ACTIVATED_MESSAGE);
        }
        Claims claims = Jwts.claims().setSubject(String.valueOf(userEntity.getId()));
        claims.put("trackId", id);
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return new TokenDto(token);
    }

    @Override
    public boolean verifyToken(String token) {
        Long userId;
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            userId = Long.parseLong(claims.getBody().getSubject());
        } catch (JwtException | NumberFormatException e) {
            return false;
        }
        UserEntity user = userService.getUser(userId);
        return AuthUtil.isAdmin(user) || user.getSubscription().isActive();
    }

}
