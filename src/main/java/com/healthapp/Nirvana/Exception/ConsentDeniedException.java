package com.healthapp.Nirvana.Exception;

public class ConsentDeniedException
extends RuntimeException {
    public ConsentDeniedException(String message) {
        super(message);
    }
}
