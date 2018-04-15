package ua.org.ubts.songs.service;

import org.springframework.security.core.Authentication;
import ua.org.ubts.songs.entity.UserEntity;

import java.security.Principal;

public interface UserService {

    UserEntity getUser(Long id);

    UserEntity getUser(Principal principal);

    UserEntity getUser(Authentication authentication);

    void createUser(UserEntity userEntity);

    void updateUser(UserEntity userEntity);

    void updateUser(UserEntity userEntity, Principal principal);

    void deleteUser(Long id);

}
