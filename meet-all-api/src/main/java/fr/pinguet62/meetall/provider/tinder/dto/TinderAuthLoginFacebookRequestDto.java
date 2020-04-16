package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class TinderAuthLoginFacebookRequestDto {

    @NonNull
    private final String token;

}
