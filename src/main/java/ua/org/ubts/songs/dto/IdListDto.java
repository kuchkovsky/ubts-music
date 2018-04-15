package ua.org.ubts.songs.dto;

import lombok.Data;

import java.util.List;

@Data
public class IdListDto extends BaseDto {

    List<Long> id;

}
