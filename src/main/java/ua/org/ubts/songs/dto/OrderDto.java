package ua.org.ubts.songs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto extends BaseDto {

    private Long id;

    private BigDecimal price;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto user;

    private List<TrackDto> tracks;

    private boolean pending;

    private boolean confirmed;

}
