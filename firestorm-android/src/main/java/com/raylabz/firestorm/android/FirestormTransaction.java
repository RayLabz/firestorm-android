package com.raylabz.firestorm.android;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.raylabz.firestorm.android.exception.ClassRegistrationException;
import com.raylabz.firestorm.android.exception.FirestormException;
import com.raylabz.firestorm.android.exception.TransactionException;


/**
 * Enables Firestore transactions.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class FirestormTransaction<T> extends FirestormOperation<T> implements Transaction.Function<T> {

    private Transaction transaction;

    /**
     * Creates a Firestore document from an object as part of a transaction.
     * @param object The object containing the data.
     * @throws TransactionException Thrown when the transaction encounters an error.
     */
    public final void create(final Object object) throws TransactionException {
        try {
            Firestorm.checkRegistration(object);
            final DocumentReference reference = Firestorm.firestore.collection(object.getClass().getSimpleName()).document();
            Reflector.setIDField(object, reference.getId());
            transaction = transaction.set(reference, object);
        } catch (ClassRegistrationException | IllegalAccessException | NoSuchFieldException e) {
            throw new TransactionException(e);
        }
    }

    /**
     * Retrieves an object using its documentID as part of a transaction.
     * @param objectClass The class of the object.
     * @param documentID The object's document ID.
     * @param <T> The type of the object (same with objectClass).
     * @return Returns an object of type T/objectClass.
     * @throws TransactionException Thrown when the transaction encounters an error.
     */
    public final <T> T get(final Class<T> objectClass, final String documentID) throws TransactionException {
        try {
            final DocumentReference documentReference = Firestorm.firestore.collection(objectClass.getSimpleName()).document(documentID);
            Firestorm.checkRegistration(objectClass);
            DocumentSnapshot snapshot = transaction.get(documentReference);
            if (snapshot.exists()) {
                return snapshot.toObject(objectClass);
            }
            else {
                return null;
            }
        } catch (ClassRegistrationException | FirebaseFirestoreException e) {
            throw new TransactionException(e);
        }
    }

    /**
     * Updates an object as part of a transaction.
     * @param object The object to update.
     * @throws TransactionException Thrown when the transaction encounters an error.
     */
    public final void update(final Object object) throws TransactionException {
        try {
            Firestorm.checkRegistration(object);
            final String id = Reflector.getIDField(object);
            final DocumentReference reference = Firestorm.firestore.collection(object.getClass().getSimpleName()).document(id);
            transaction = transaction.set(reference, object);
        } catch (IllegalAccessException | NoSuchFieldException | ClassRegistrationException e) {
            throw new TransactionException(e);
        }
    }

    /**
     * Deletes an object as part of a transaction.
     * @param object The object to delete.
     * @throws TransactionException Thrown when the transaction encounters an error.
     */
    public final void delete(final Object object) throws TransactionException {
        try {
            Firestorm.checkRegistration(object);
            final String id = Reflector.getIDField(object);
            final DocumentReference reference = Firestorm.firestore.collection(object.getClass().getSimpleName()).document(id);
            transaction = transaction.delete(reference);
            Reflector.setIDField(object, null);
        } catch (IllegalAccessException | ClassRegistrationException | NoSuchFieldException e) {
            throw new TransactionException(e);
        }
    }

    /**
     * Deletes an object as part of a transaction.
     * @param objectClass The class of the object to delete.
     * @param objectID The ID of the object to delete.
     * @param <T> The type of the object to delete.
     * @throws TransactionException Thrown when the transaction encounters an error.
     */
    public <T> void delete(final Class<T> objectClass, final String objectID) throws TransactionException {
        try {
            Firestorm.checkRegistration(objectClass);
            final DocumentReference reference = Firestorm.firestore.collection(objectClass.getSimpleName()).document(objectID);
            transaction = transaction.delete(reference);
        } catch (ClassRegistrationException e) {
            throw new TransactionException(e);
        }
    }

    /**
     * Overrides method <b>apply()</b> of the Transaction.Function interface.
     * Initializes the transaction object used to carry out the transaction and then executes the transaction code
     * provided by the developer.
     * @param transaction The transaction object.
     * @return Returns null.
     */
    @Nullable
    @Override
    public T apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
        this.transaction = transaction;
        try {
            return managedExecute();
        } catch (FirestormException e) {
            return null;
        }
    }

}
