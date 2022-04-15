package com.raylabz.android;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.WriteBatch;
import com.raylabz.android.exception.BatchException;
import com.raylabz.android.exception.ClassRegistrationException;
import com.raylabz.android.exception.TooManyOperationsException;

/**
 * Enables Firestore batch writes.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class FirestormBatch extends FirestormOperation<Void> {

    private int numOfOperations = 0;
    private WriteBatch batch;

    /**
     * Initializes the batch.
     */
    public FirestormBatch() {
        this.batch = Firestorm.firestore.batch();
    }

    /**
     * Creates a Firestore document from an object as part of a batch write.
     * @param object The object containing the data.
     * @throws BatchException Thrown when the batch execution encounters an error.
     */
    public final void create(final Object object) throws BatchException {
        try {
            Firestorm.checkRegistration(object);
            final DocumentReference reference = Firestorm.firestore.collection(object.getClass().getSimpleName()).document();
            Reflector.setIDField(object, reference.getId());
            batch = batch.set(reference, object);
            numOfOperations++;
        } catch (IllegalAccessException | ClassRegistrationException | NoSuchFieldException e) {
            throw new BatchException(e);
        }
    }

    /**
     * Updates an object as part of a batch write.
     * @param object The object to update.
     * @throws BatchException Thrown when the batch execution encounters an error.
     */
    public final void update(final Object object) throws BatchException {
        try {
            Firestorm.checkRegistration(object);
            final String id = Reflector.getIDField(object);
            final DocumentReference reference = Firestorm.firestore.collection(object.getClass().getSimpleName()).document(id);
            batch = batch.set(reference, object);
            numOfOperations++;
        } catch (IllegalAccessException | ClassRegistrationException | NoSuchFieldException e) {
            throw new BatchException(e);
        }
    }

    /**
     * Deletes an object as part of a batch write.
     * @param object The object to delete.
     * @throws BatchException Thrown when the batch execution encounters an error.
     */
    public final void delete(final Object object) throws BatchException {
        try {
            Firestorm.checkRegistration(object);
            final String id = Reflector.getIDField(object);
            final DocumentReference reference = Firestorm.firestore.collection(object.getClass().getSimpleName()).document(id);
            batch = batch.delete(reference);
            Reflector.setIDField(object, null);
            numOfOperations++;
        } catch (IllegalAccessException | ClassRegistrationException | NoSuchFieldException e) {
            throw new BatchException(e);
        }
    }

    /**
     * Deletes an object using an ID.
     * @param objectClass The class of the object.
     * @param objectID The object's ID.
     * @throws BatchException Thrown when the batch execution encounters an error.
     */
    public final void delete(final Class<?> objectClass, final String objectID) throws BatchException {
        try {
            Firestorm.checkRegistration(objectClass);
            final DocumentReference reference = Firestorm.firestore.collection(objectClass.getSimpleName()).document(objectID);
            batch = batch.delete(reference);
            numOfOperations++;
        } catch (ClassRegistrationException e) {
            throw new BatchException(e);
        }
    }

    /**
     * Performs a batch operation.
     */
    Task<Void> doBatch() {
        if (numOfOperations > 500) {
            throw new TooManyOperationsException("The number of operations in a batch write cannot exceed 500.");
        }
        managedExecute();
        return batch.commit();
    }

}
