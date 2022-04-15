package com.raylabz.android;


import com.raylabz.android.exception.FirestormException;

/**
 * Abstracts a Firestorm operation, its execution and callbacks.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class FirestormOperation<T> {

    /**
     * Managed execution.
     */
    public final T managedExecute() throws FirestormException {
        return execute();
    }

    /**
     * Executes an operation.
     */
    public abstract T execute();

}
