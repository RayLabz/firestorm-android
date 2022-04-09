package com.raylabz.firestormandroid;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

/**
 * Implements logic for Firestore update events.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class OnReferenceUpdateListener implements EventListener<DocumentSnapshot> {

    private static final String NO_SNAPSHOT_EXISTS_MESSAGE = "This object does not exist [No snapshot].";

    /**
     * The listener is listening for changes to this object.
     */
    private final Class<?> objectClass;

    /**
     * The document ID of the object to listen to.
     */
    private final String documentID;

    /**
     * Instantiates an OnReferenceUpdateListener.
     * @param objectClass The type of object this listener will be attached to.
     * @param documentID The document ID of the object.
     */
    public OnReferenceUpdateListener(final Class<?> objectClass, final String documentID) {
        this.objectClass = objectClass;
        this.documentID = documentID;
    }

    /**
     * Executes when an update to an object is made.
     * @param documentSnapshot The document snapshot retrieved upon update.
     * @param e An exception thrown by Firestore if the data retrieval was not successful.
     */
    @Override
    public final void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            onFailure(e.getMessage());
            return;
        }

        if (documentSnapshot != null && documentSnapshot.exists()) {
            Object fetchedObject = documentSnapshot.toObject(objectClass);

            if (fetchedObject != null) {

                if (fetchedObject.getClass() != objectClass) {
                    onFailure("The type of the event listener's received object does not match the type provided.");
                    return;
                }

                onSuccess(fetchedObject);

            }
            else {
                onFailure("Failed to retrieve update to object.");
            }
        }
    }

    /**
     * Retrieves the object being listened at by this listener.
     * @return Returns the object being listened at by this listener.
     */
    public Class<?> getObjectClass() {
        return objectClass;
    }

    /**
     * Retrieves the ID of the document being listened to by this listener.
     * @return Returns the ID of the document listened to.
     */
    public String getDocumentID() {
        return documentID;
    }

    /**
     * Implements logic upon success of data update delivery.
     * @param object The updated object.
     */
    public abstract void onSuccess(Object object);

    /**
     * Implements logic upon failure of data update delivery.
     * @param failureMessage The message of the failure.
     */
    public abstract void onFailure(final String failureMessage);

}
