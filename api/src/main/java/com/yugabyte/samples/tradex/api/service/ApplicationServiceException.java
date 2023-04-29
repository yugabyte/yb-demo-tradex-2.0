package com.yugabyte.samples.tradex.api.service;

public class ApplicationServiceException extends Throwable {
    public ApplicationServiceException(String message) {
        super(message);
    }
}
