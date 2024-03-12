package edu.clarkson.cosi.mirrorapi.error;

/**
 * A NotFoundException is thrown when a request attempts to view or modify
 * a resource that does not exist or cannot be found.
 */
@SuppressWarnings("unused")
public class NotFoundException extends RuntimeException {
    /**
     * Constructs a NotFoundException.
     */
    public NotFoundException() { super("Not found."); }

    /**
     * Show only this NotFoundException in the stack trace when it is thrown.
     * @return This NotFoundException
     */
    @Override
    public synchronized Throwable fillInStackTrace() { return this; }
}
