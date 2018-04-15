package ua.org.ubts.songs.converter.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.org.ubts.songs.converter.StoreTrackConverter;
import ua.org.ubts.songs.dto.StoreTrackDto;
import ua.org.ubts.songs.entity.OrderEntity;
import ua.org.ubts.songs.entity.TrackEntity;
import ua.org.ubts.songs.entity.UserEntity;
import ua.org.ubts.songs.service.UserService;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StoreTrackConverterImpl implements StoreTrackConverter {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Override
    public TrackEntity convertToEntity(StoreTrackDto dto) {
        return modelMapper.map(dto, TrackEntity.class);
    }

    @Override
    public StoreTrackDto convertToDto(TrackEntity entity) {
        return modelMapper.map(entity, StoreTrackDto.class);
    }

    @Override
    public List<StoreTrackDto> convertToDto(List<TrackEntity> tracks, Principal principal) {
        List<StoreTrackDto> allTracks = convertToDto(tracks);
        if (principal != null) {
            allTracks.forEach(t -> {
                t.setPurchased(false);
                t.setOrderPending(false);
                t.setOrderRejected(false);
            });
            UserEntity user = userService.getUser(principal);
            List<StoreTrackDto> orderPendingTracks = convertToDto(user.getOrders().stream()
                    .filter(OrderEntity::isPending).map(OrderEntity::getTracks)
                    .flatMap(Collection::stream).collect(Collectors.toList()));
            orderPendingTracks.forEach(t -> {
                t.setOrderPending(true);
                t.setOrderRejected(false);
                t.setPurchased(false);
            });
            List<StoreTrackDto> orderRejectedTracks = convertToDto(user.getOrders().stream()
                    .filter(o -> !o.isPending() && !o.isConfirmed()).map(OrderEntity::getTracks)
                    .flatMap(Collection::stream).collect(Collectors.toList()));
            orderRejectedTracks.forEach(t -> {
                t.setOrderRejected(true);
                t.setOrderPending(false);
                t.setPurchased(false);
            });
            List<StoreTrackDto> purchasedTracks = convertToDto(user.getTracks());
            purchasedTracks.forEach(t -> {
                t.setPurchased(true);
                t.setOrderRejected(false);
                t.setOrderPending(false);
            });
            allTracks.replaceAll(track -> orderPendingTracks.stream()
                    .filter(t -> t.idEquals(track)).findFirst().orElse(orderRejectedTracks.stream()
                            .filter(t -> t.idEquals(track)).findFirst().orElse(purchasedTracks.stream()
                                    .filter(t -> t.idEquals(track)).findFirst().orElse(track))));
        }
        return allTracks;
    }

}
