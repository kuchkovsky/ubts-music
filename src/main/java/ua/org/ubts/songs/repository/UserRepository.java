package ua.org.ubts.songs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.org.ubts.songs.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByEmail(String email);

}
