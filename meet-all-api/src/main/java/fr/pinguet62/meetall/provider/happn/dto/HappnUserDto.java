package fr.pinguet62.meetall.provider.happn.dto;

import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HappnUserDto {

    private String id;
    private String display_name;
    private Integer age;
    @GraphQLField(additional = ".mode(0).width(1000).height(1000)")
    private List<HappnProfileDto> profiles;
    /**
     * @example {@code 1989-06-14}
     */
    private LocalDate birth_date;

}
