package ua.org.ubts.songs.converter;

import ua.org.ubts.songs.dto.StoreTrackDto;
import ua.org.ubts.songs.entity.TrackEntity;

import java.security.Principal;
import java.util.List;

public interface StoreTrackConverter extends GenericConverter<StoreTrackDto, TrackEntity> {

    List<StoreTrackDto> convertToDto(List<TrackEntity> tracks, Principal principal);

}
