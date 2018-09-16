package ua.org.ubts.songs.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ua.org.ubts.songs.serializer.LocalDateSerializer;

import java.time.LocalDate;

@Data
public class SubscriptionDto extends BaseDto {

    private boolean active;

    private boolean requestPending;

    private boolean expired;

    private boolean banned;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate expirationDate;

    private UserDto user;

}
