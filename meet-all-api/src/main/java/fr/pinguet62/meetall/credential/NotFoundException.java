package fr.pinguet62.meetall.credential;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
class NotFoundException extends RuntimeException {

    public NotFoundException(Class<?> type, Object id) {
        super("No " + type.getSimpleName() + " found with id " + id);
    }

}
