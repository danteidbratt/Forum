package se.donut.postservice.exception;

import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.*;

public enum ExceptionType {

    USER_NOT_FOUND(NOT_FOUND),
    POST_NOT_FOUND(NOT_FOUND),
    COMMENT_NOT_FOUND(NOT_FOUND),
    NAME_ALREADY_TAKEN(BAD_REQUEST),
    LOGIN_FAILED(UNAUTHORIZED);

    private final Status status;

    ExceptionType(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
