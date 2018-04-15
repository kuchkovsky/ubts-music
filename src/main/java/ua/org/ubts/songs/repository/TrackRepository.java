package ua.org.ubts.songs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.org.ubts.songs.entity.TrackEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, Long> {

    @Query("SELECT t FROM TrackEntity t WHERE t.id IN :id")
    List<TrackEntity> findById(@Param("id") List<Long> id);

    Optional<TrackEntity> findByArtistAndTitle(String artist, String title);

}
