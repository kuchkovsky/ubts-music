package ua.org.ubts.songs.converter.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.org.ubts.songs.converter.OrderConverter;
import ua.org.ubts.songs.dto.OrderDto;
import ua.org.ubts.songs.entity.OrderEntity;
import ua.org.ubts.songs.entity.TrackEntity;

import java.math.BigDecimal;

@Component
public class OrderConverterImpl implements OrderConverter {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrderEntity convertToEntity(OrderDto dto) {
        return modelMapper.map(dto, OrderEntity.class);
    }

    @Override
    public OrderDto convertToDto(OrderEntity entity) {
        OrderDto orderDto = modelMapper.map(entity, OrderDto.class);
        BigDecimal price = BigDecimal.ZERO;
        for (TrackEntity trackEntity : entity.getTracks()) {
            price = price.add(trackEntity.getPrice());
        }
        orderDto.setPrice(price);
        return orderDto;
    }

}
