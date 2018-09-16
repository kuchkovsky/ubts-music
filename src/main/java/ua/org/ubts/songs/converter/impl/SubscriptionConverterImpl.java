package ua.org.ubts.songs.converter.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.org.ubts.songs.converter.SubscriptionConverter;
import ua.org.ubts.songs.dto.SubscriptionDto;
import ua.org.ubts.songs.entity.SubscriptionEntity;

@Component
public class SubscriptionConverterImpl implements SubscriptionConverter {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SubscriptionEntity convertToEntity(SubscriptionDto dto) {
        return modelMapper.map(dto, SubscriptionEntity.class);
    }

    @Override
    public SubscriptionDto convertToDto(SubscriptionEntity entity) {
        return modelMapper.map(entity, SubscriptionDto.class);
    }

}
