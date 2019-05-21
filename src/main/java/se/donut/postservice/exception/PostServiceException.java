package se.donut.postservice.exception;

import javax.ws.rs.WebApplicationException;

public final class PostServiceException extends WebApplicationException {

    private final ExceptionType exceptionType;

    public PostServiceException(ExceptionType exceptionType, String message) {
        super(message, exceptionType.getStatus());
        this.exceptionType = exceptionType;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }
}
