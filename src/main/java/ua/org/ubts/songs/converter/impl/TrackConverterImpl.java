package ua.org.ubts.songs.converter.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.org.ubts.songs.converter.TrackConverter;
import ua.org.ubts.songs.dto.TrackDto;
import ua.org.ubts.songs.entity.TrackEntity;


@Component
public class TrackConverterImpl implements TrackConverter {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TrackEntity convertToEntity(TrackDto dto) {
        return modelMapper.map(dto, TrackEntity.class);
    }

    @Override
    public TrackDto convertToDto(TrackEntity entity) {
        return modelMapper.map(entity, TrackDto.class);
    }

}
