package com.raylabz.android.exception;

/**
 * An exception thrown during a transaction.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public class BatchException extends FirestormException {

    public BatchException(final Exception e) {
        super(e);
    }

}
