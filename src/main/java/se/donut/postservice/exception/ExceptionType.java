package se.donut.postservice.exception;

import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public enum ExceptionType {
    USER_NOT_FOUND(NOT_FOUND),
    NAME_ALREADY_TAKEN(BAD_REQUEST);

    private final Status status;

    ExceptionType(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
