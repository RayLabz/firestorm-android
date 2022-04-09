package com.raylabz.firestormandroid;


import com.raylabz.firestormandroid.exception.FirestormException;

/**
 * Abstracts a Firestorm operation, its execution and callbacks.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class FirestormOperation<T> {

    /**
     * Managed execution.
     */
    public final void managedExecute() throws FirestormException {
        execute();
    }

    /**
     * Executes an operation.
     */
    public abstract T execute();

}
