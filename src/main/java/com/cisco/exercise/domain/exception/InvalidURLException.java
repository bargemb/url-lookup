package com.cisco.exercise.domain.exception;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class InvalidURLException extends RuntimeException {
    private String domain;
    public InvalidURLException(final String domain) {
        super("Malware URL Found");
        this.domain = domain;
    }
}
