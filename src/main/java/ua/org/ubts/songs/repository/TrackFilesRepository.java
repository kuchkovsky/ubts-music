package ua.org.ubts.songs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.org.ubts.songs.entity.TrackFilesEntity;

public interface TrackFilesRepository extends JpaRepository<TrackFilesEntity, Long> {
}
