package fr.pinguet62.meetall.provider.once.dto;

import lombok.Data;

import java.util.List;

@Data
public class OnceUserDto {

    private String id;
    private String first_name;
    private Long age;
    private List<OncePictureDto> pictures;

}
