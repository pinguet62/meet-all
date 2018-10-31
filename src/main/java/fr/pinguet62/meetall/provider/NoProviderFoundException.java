package fr.pinguet62.meetall.provider;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class NoProviderFoundException extends RuntimeException {

    public NoProviderFoundException(Provider provider) {
        super("No " + ProviderService.class.getSimpleName() + " found with id " + provider.name());
    }

}
