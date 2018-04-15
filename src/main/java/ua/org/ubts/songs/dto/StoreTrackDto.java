package ua.org.ubts.songs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Objects;

@Data
public class StoreTrackDto extends TrackDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean purchased;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean orderPending;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean orderRejected;

    public boolean idEquals(StoreTrackDto track) {
        return Objects.equals(getId(), track.getId());
    }

}
