package fr.pinguet62.meetall.provider.happn.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HappnUserDto {

    private String id;
    private String display_name;
    private Integer age;
    private List<HappnProfileDto> profiles;
    /**
     * @example {@code 1989-06-14}
     */
    private LocalDate birth_date;

}
