package se.donut.postservice.exception;

import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public enum ExceptionType {

    USER_NOT_FOUND(NOT_FOUND),
    POST_NOT_FOUND(NOT_FOUND),
    FORUM_NOT_FOUND(NOT_FOUND),
    COMMENT_NOT_FOUND(NOT_FOUND),
    USERNAME_ALREADY_TAKEN(BAD_REQUEST),
    FORUM_NAME_ALREADY_TAKEN(BAD_REQUEST),
    INVALID_USERNAME(BAD_REQUEST),
    INVALID_PASSWORD(BAD_REQUEST),
    INVALID_TITLE(BAD_REQUEST),
    INVALID_CONTENT(BAD_REQUEST),
    INVALID_FORUM_NAME(BAD_REQUEST),
    INVALID_FORUM_DESCRIPTION(BAD_REQUEST);

    private final Status status;

    ExceptionType(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
