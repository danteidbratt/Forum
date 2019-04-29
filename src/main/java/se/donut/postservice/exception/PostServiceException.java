package se.donut.postservice.exception;

import javax.ws.rs.WebApplicationException;

public class PostServiceException extends WebApplicationException {

    private final ExceptionType exceptionType;

    public PostServiceException(ExceptionType exceptionType) {
        super(exceptionType.toString(), exceptionType.getStatus());
        this.exceptionType = exceptionType;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }
}
