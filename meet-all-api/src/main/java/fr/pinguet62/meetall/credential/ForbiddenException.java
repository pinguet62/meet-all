package fr.pinguet62.meetall.credential;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@ResponseStatus(FORBIDDEN)
class ForbiddenException extends RuntimeException {
}
