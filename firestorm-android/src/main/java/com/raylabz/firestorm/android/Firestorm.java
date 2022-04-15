package com.raylabz.firestorm.android;

import android.os.Handler;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.raylabz.firestorm.android.exception.ClassRegistrationException;
import com.raylabz.firestorm.android.exception.FirestormException;
import com.raylabz.firestorm.android.exception.FirestormObjectException;
import com.raylabz.firestorm.android.exception.NotInitializedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Firestorm is an object-oriented data access API for Firestore.
 * https://raylabz.github.io/Firestorm/.
 * This is the main class of the Firestorm API, which allows basic interactions with the Firestore.
 * Must be initialized with a FirebaseApp object using the <i>init()</i> method before interacting with the Firestore.
 *
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public final class Firestorm {

    /**
     * Static Firestore object used to make queries on Firestore.
     */
    static FirebaseFirestore firestore;

    /**
     * Stores a list of listeners registered to objects.
     */
    private static final HashMap<Object, ListenerRegistration> registeredObjectListeners = new HashMap<>();
    private static final HashMap<Class<?>, ListenerRegistration> registeredClassListeners = new HashMap<>();

    /**
     * Initializes Firestorm <b><u>after Firebase has been initialized</u></b> using <i>Firebase.initializeApp()</i>.
     */
    public static void init() {
        firestore = FirebaseFirestore.getInstance();

        /* Dummy request used for initialization:
            The initial call to Firestore has high latency so it is preferable to make a dummy request
            as soon as Firebase is initialized instead of waiting to make the first connection when an actual request is made.
         */
        firestore.getFirestoreSettings();
    }

    /**
     * Private constructor.
     */
    private Firestorm() {
    }

    /**
     * Registers a class.
     *
     * @param aClass The class to register.
     * @throws FirestormException Thrown when the class cannot be registered.
     */
    public static void register(Class<?> aClass) throws FirestormException {
        try {
            FirestormRegistry.register(aClass);
        } catch (FirestormObjectException e) {
            throw new FirestormException(e.getMessage());
        }
    }

    /**
     * Checks if an object's class is registered.
     *
     * @param object The object to check the class of.
     * @throws ClassRegistrationException Thrown when the object's class is not registered.
     */
    static void checkRegistration(final Object object) throws ClassRegistrationException {
        if (!FirestormRegistry.isRegistered(object.getClass())) {
            throw new ClassRegistrationException(object.getClass());
        }
    }

    /**
     * Checks if a class is registered.
     *
     * @param aClass The class to check.
     * @throws ClassRegistrationException Thrown when the class is not registered.
     */
    static void checkRegistration(final Class<?> aClass) throws ClassRegistrationException {
        if (!FirestormRegistry.isRegistered(aClass)) {
            throw new ClassRegistrationException(aClass);
        }
    }

    /**
     * Retrieves the Firestore instance.
     * @return Returns FirebaseFirestore.
     */
    public static FirebaseFirestore getFirestore() {
        return firestore;
    }

    /**
     * Creates a Firestore document from an object.
     *
     * @param object An object containing the data to be written in Firestore.
     * @return Returns the document ID of the created document.
     * @throws FirestormException Thrown when Firestorm encounters an error.
     */
    public static Task<String> create(final Object object) throws FirestormException {
        try {
            checkRegistration(object);
            final DocumentReference reference = firestore.collection(object.getClass().getSimpleName()).document();
            Reflector.setIDField(object, reference.getId());

            TaskCompletionSource<String> source = new TaskCompletionSource<>();
            new Handler().post(() -> {
                reference.set(object).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        source.setResult(reference.getId());
                    }
                    else {
                        if (task.getException() != null) {
                            source.setException(task.getException());
                        }
                        else {
                            source.setException(new FirestormObjectException("Could not write object with ID '" + reference.getId() + "'."));
                        }
                    }
                });
            });
            return source.getTask();

        } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException | NotInitializedException e) {
            throw new FirestormException(e);
        }
    }

    /**
     * Creates a Firestore document from an object with a specific ID.
     *
     * @param object An object containing the data to be written in Firestore.
     * @param id     The ID of the object to create.
     * @return Returns the document ID of the created document.
     */
    public static Task<String> create(final Object object, final String id) {
        try {
            checkRegistration(object);
            final DocumentReference reference = firestore.collection(object.getClass().getSimpleName()).document(id);
            Reflector.setIDField(object, reference.getId());

            TaskCompletionSource<String> source = new TaskCompletionSource<>();
            new Handler().post(() -> {
                reference.set(object).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        source.setResult(reference.getId());
                    }
                    else {
                        if (task.getException() != null) {
                            source.setException(task.getException());
                        }
                        else {
                            source.setException(new FirestormObjectException("Could not write object with ID '" + id + "'."));
                        }
                    }
                });
            });
            return source.getTask();

        } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException | NotInitializedException e) {
            throw new FirestormException(e);
        }
    }

    /**
     * Retrieves a document as an object from Firestore.
     *
     * @param objectClass The class of the object retrieved.
     * @param documentID  The documentID of the object to retrieve.
     * @param <T>         A type matching the type of objectClass.
     * @return Returns an object of type T (objectClass).
     */
    public static <T> Task<T> get(final Class<T> objectClass, final String documentID) {
        DocumentReference docRef = firestore.collection(objectClass.getSimpleName()).document(documentID);
        TaskCompletionSource<T> source = new TaskCompletionSource<>();
        new Handler().post(() -> {
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    T object = task.getResult().toObject(objectClass);
                    if (object == null) {
                        source.setException(new FirestormObjectException("Object with ID '" + documentID + "' not found."));
                    }
                    else {
                        source.setResult(object);
                    }
                }
            });
        });
        return source.getTask();
    }

    /**
     * Retrieves multiple documents of a class as a list of objects.
     *
     * @param objectClass The class of the objects.
     * @param ids         A list of IDs of the objects to retrieve.
     * @param <T>         A type matching the type of object class.
     * @return Returns a list of type T.
     */
    public static <T> Task<List<T>> getMany(final Class<T> objectClass, List<String> ids) {
        TaskCompletionSource<List<T>> source = new TaskCompletionSource<>();
        new Handler().post(() -> {
            firestore
                    .collection(objectClass.getSimpleName())
                    .whereIn("id", ids)
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<T> objects = task.getResult().toObjects(objectClass);
                    source.setResult(objects);
                }
                else {
                    if (task.getException() != null) {
                        source.setException(task.getException());
                    }
                    else {
                        source.setException(new FirestormException("Failed to retrieve items."));
                    }
                }
            });
        });

        return source.getTask();
    }

    /**
     * Retrieves multiple documents of a class as a list of objects.
     * @param objectClass The class of the objects.
     * @param ids A list of IDs of the objects to retrieve.
     * @param <T> A type matching the type of object class.
     * @return Returns a list of type T.
     */
    public static <T> Task<List<T>> getMany(final Class<T> objectClass, String... ids) {
        ArrayList<String> idsList = new ArrayList<>(Arrays.asList(ids));
        return getMany(objectClass, idsList);
    }

    /**
     * Checks if a given documentID of a given class exists.
     *
     * @param objectClass The object class.
     * @param documentID The document ID.
     * @return Returns true if the document exists on Firestore, false otherwise.
     * @throws FirestormException Thrown when Firestorm encounters an error.
     */
    public static Task<Boolean> exists(final Class<?> objectClass, final String documentID) throws FirestormException {
        DocumentReference docRef = firestore.collection(objectClass.getSimpleName()).document(documentID);
        TaskCompletionSource<Boolean> source = new TaskCompletionSource<>();
        new Handler().post(() -> {
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean exists = task.getResult().exists();
                    source.setResult(exists);
                }
                else {
                    if (task.getException() != null) {
                        source.setException(task.getException());
                    }
                    else {
                        source.setException(new FirestormObjectException("Could not check object with ID '" + documentID + "'."));
                    }
                }
            });
        });
        return source.getTask();
    }

    /**
     * Updates a document in Firestore.
     *
     * @param object An object which provides data and the document ID for the update.
     * @throws FirestormException Thrown when Firestorm encounters an error.
     */
    public static Task<String> update(final Object object) throws FirestormException {
        try {
            checkRegistration(object);
            final String documentID = Reflector.getIDField(object);
            final DocumentReference reference = firestore.collection(object.getClass().getSimpleName()).document(documentID);
            TaskCompletionSource<String> source = new TaskCompletionSource<>();
            new Handler().post(() -> {
                reference.set(object).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        source.setResult(reference.getId());
                    }
                    else {
                        if (task.getException() != null) {
                            source.setException(task.getException());
                        }
                        else {
                            source.setException(new FirestormObjectException("Could not update object with ID '" + reference.getId() + "'."));
                        }
                    }
                });
            });
            return source.getTask();
        } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException | NotInitializedException e) {
            throw new FirestormException(e);
        }
    }

    /**
     * Deletes an object from Firestore.
     *
     * @param objectClass The class of the object to delete.
     * @param objectID    The ID of the object/document in Firestore.
     * @param <T>         The type (class) of the object.
     */
    public static <T> Task<Void> delete(final Class<T> objectClass, final String objectID) {
        final DocumentReference reference = firestore.collection(objectClass.getSimpleName()).document(objectID);
        try {
            TaskCompletionSource<Void> source = new TaskCompletionSource<>();
            new Handler().post(reference::delete);
            return source.getTask();
        } catch (NotInitializedException e) {
            throw new FirestormException(e);
        }
    }

    /**
     * Lists available documents of a given type.
     *
     * @param objectClass The type of the documents to filter.
     * @param limit       The maximum number of objects to return.
     * @param <T>         A type matching the type of objectClass.
     * @return Returns an ArrayList of objects of type objectClass.
     */
    public static <T> Task<List<T>> list(final Class<T> objectClass, final int limit) {
        TaskCompletionSource<List<T>> source = new TaskCompletionSource<>();
        new Handler().post(() -> {
            firestore
                    .collection(objectClass.getSimpleName())
                    .limit(limit)
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<T> objects = task.getResult().toObjects(objectClass);
                    source.setResult(objects);
                }
                else {
                    if (task.getException() != null) {
                        source.setException(task.getException());
                    }
                    else {
                        source.setException(new FirestormException("Failed to retrieve items."));
                    }
                }
            });
        });
        return source.getTask();
    }

    /**
     * Lists ALL available documents of a given type. May incur charges for read operations for huge numbers of documents.
     *
     * @param objectClass The type of the documents to filter.
     * @param <T>         A type matching the type of objectClass.
     * @return Returns an ArrayList of objects of type objectClass.
     */
    public static <T> Task<List<T>> listAll(final Class<T> objectClass) {
        TaskCompletionSource<List<T>> source = new TaskCompletionSource<>();
        new Handler().post(() -> {
            firestore
                    .collection(objectClass.getSimpleName())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<T> objects = task.getResult().toObjects(objectClass);
                    source.setResult(objects);
                }
                else {
                    if (task.getException() != null) {
                        source.setException(task.getException());
                    }
                    else {
                        source.setException(new FirestormException("Failed to retrieve items."));
                    }
                }
            });
        });
        return source.getTask();
    }

    /**
     * Lists a set documents which match the filtering criteria provided. Returns a filter of all documents if no filters are used.
     *
     * @param objectClass The type of the documents to filter.
     * @param <T>         A type matching the type of objectClass.
     * @return Returns a FirestormFilterable which can be used to append filter parameters.
     */
    public static <T> FirestormFilterable<T> filter(final Class<T> objectClass) {
        return new FirestormFilterable<>(firestore.collection(objectClass.getSimpleName()), objectClass);
    }

    /**
     * Retrieves a DocumentReference to an object.
     *
     * @param objectClass The type of the object.
     * @param documentID The object's ID.
     * @param <T> The type of the object.
     * @return Returns DocumentReference.
     */
    public static <T> DocumentReference getObjectReference(final Class<T> objectClass, final String documentID) {
        return firestore.collection(objectClass.getSimpleName()).document(documentID);
    }

    /**
     * Retrieves a DocumentReference to an object.
     *
     * @param object The object to get the reference for.
     * @param <T> The type of the object.
     * @return Returns DocumentReference.
     * @throws FirestormException Thrown when Firestorm encounters an error.
     */
    public static <T> DocumentReference getObjectReference(Object object) throws FirestormException {
        try {
            checkRegistration(object);
            final String documentID = Reflector.getIDField(object);
            return firestore.collection(object.getClass().getSimpleName()).document(documentID);
        } catch (IllegalAccessException | NoSuchFieldException | ClassRegistrationException | NotInitializedException e) {
            throw new FirestormException(e);
        }
    }

    /**
     * Retrieves a CollectionReference to a type.
     *
     * @param objectClass The class of object to get a reference for.
     * @param <T> The Type of class.
     * @return Returns a CollectionReference.
     */
    public static <T> CollectionReference getCollectionReference(final Class<T> objectClass) {
        return firestore.collection(objectClass.getSimpleName());
    }

    /**
     * Utility method. Registers a listener in the registeredListeners map.
     * @param object The object being listened to.
     * @param listenerRegistration The listener of the object.
     */
    private static void registerObjectListener(final Object object, final ListenerRegistration listenerRegistration) {
        registeredObjectListeners.put(object, listenerRegistration);
    }

    /**
     * Unregisters a listener from the registeredListeners map for an object provided.
     * @param object The object being listened to.
     */
    private static void unregisterObjectListener(final Object object) {
        final ListenerRegistration listenerRegistration = registeredObjectListeners.get(object);
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            registeredObjectListeners.remove(object);
        }
    }

    /**
     * Utility method. Registers a class listener in the registeredListeners map.
     * @param objectClass The class being listened to.
     * @param listenerRegistration The listener of the class.
     */
    private static void registerClassListener(final Class<?> objectClass, final ListenerRegistration listenerRegistration) {
        registeredClassListeners.put(objectClass, listenerRegistration);
    }

    /**
     * Unregisters a listener for a class provided.
     * @param objectClass The class being listened to.
     */
    private static void unregisterClassListener(final Class<?> objectClass) {
        final ListenerRegistration listenerRegistration = registeredClassListeners.get(objectClass);
        listenerRegistration.remove();
        registeredClassListeners.remove(objectClass);
    }

    /**
     * Utility method. Attaches an event listener which listens for updates to an object.
     *
     * @param eventListener An implementation of a FirestormEventListener.
     * @return Returns a ListenerRegistration.
     * @throws FirestormException Thrown when Firestorm encounters an error.
     */
    public static ListenerRegistration attachListener(final ObjectListener eventListener) throws FirestormException {
        try {
            checkRegistration(eventListener.getObjectToListenFor());
            final String documentID = Reflector.getIDField(eventListener.getObjectToListenFor());
            final ListenerRegistration listenerRegistration = firestore.collection(eventListener.getObjectToListenFor().getClass().getSimpleName()).document(documentID).addSnapshotListener(eventListener);
            registerObjectListener(eventListener.getObjectToListenFor(), listenerRegistration);
            return listenerRegistration;
        } catch (NoSuchFieldException | IllegalAccessException | ClassRegistrationException | NotInitializedException e) {
            throw new FirestormException(e);
        }
    }

    /**
     * Attaches an event listener which listens for updates to an object using its class and ID.
     *
     * @param eventListener An implementation of a FirestormEventListener.
     * @return Returns a ListenerRegistration.
     */
    public static ListenerRegistration attachListener(final ReferenceListener eventListener) {
        return firestore.collection(eventListener.getObjectClass().getSimpleName()).document(eventListener.getDocumentID()).addSnapshotListener(eventListener);
    }

    /**
     * Attaches an event listener which listens for updates to a class.
     * @param eventListener The collection event listener.
     * @param <T> The class of the listener.
     * @return Returns a listener registration.
     */
    public static <T> ListenerRegistration attachListener(final ClassListener<T> eventListener) throws FirestormException {
        try {
            checkRegistration(eventListener.getObjectClass());
            ListenerRegistration listenerRegistration = firestore.
                    collection(eventListener.getObjectClass().getSimpleName())
                    .addSnapshotListener(eventListener);
            registerClassListener(eventListener.getObjectClass(), listenerRegistration);
            return listenerRegistration;
        } catch (ClassRegistrationException | NotInitializedException e) {
            throw new FirestormException(e);
        }
    }

    /**
     * Attaches a listener to a filterable.
     * @param eventListener The event listener to attach.
     * @param <T> The type of object.
     * @return Returns a ListenerRegistration.
     */
    public static <T> ListenerRegistration attachListener(final FilterableListener<T> eventListener) throws FirestormException {
        try {
            checkRegistration(eventListener.getFilterable().objectClass);
            return eventListener.getFilterable()
                    .addSnapshotListener(eventListener);
        } catch (ClassRegistrationException | NotInitializedException e) {
            throw new FirestormException(e);
        }
    }

    /**
     * Detaches a listener for a specific class.
     * @param objectClass The class.
     */
    public static void detachListener(Class<?> objectClass) {
        final ListenerRegistration listenerRegistration = registeredClassListeners.get(objectClass);
        listenerRegistration.remove();
        unregisterClassListener(objectClass);
    }

    /**
     * Unregisters a listener from an object.
     * @param object The object being listened to.
     */
    public static void detachListener(Object object) {
        final ListenerRegistration listenerRegistration = registeredObjectListeners.get(object);
        listenerRegistration.remove();
        unregisterObjectListener(listenerRegistration);
    }

    /**
     * Detaches a specified listener from a a reference.
     *
     * @param listenerRegistration The listenerRegistration to detach.
     */
    public static void detachListener(ListenerRegistration listenerRegistration) {
        listenerRegistration.remove();
    }

    /**
     * Checks if a provided object has a registered listener.
     * @param object The object.
     * @return Returns true if the object has a registered listener, false otherwise.
     */
    public static boolean hasListener(Object object) {
        return registeredObjectListeners.get(object) != null;
    }

    /**
     * Retrieves a ListenerRegistration attached to the provided object, or null if no listener is attached.
     * @param object The object.
     * @return Returns a ListenerRegistration.
     */
    public static ListenerRegistration getListener(Object object) {
        return registeredObjectListeners.get(object);
    }

    /**
     * Runs a transaction operation.
     *
     * @param transaction The transaction to run.
     */
    public static <T> Task<T> runTransaction(final FirestormTransaction<T> transaction) {
        TaskCompletionSource<T> source = new TaskCompletionSource<>();
        new Handler().post(() -> {
            Firestorm.firestore.runTransaction(transaction).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    T result = task.getResult();
                    source.setResult(result);
                }
                else {
                    if (task.getException() != null) {
                        source.setException(task.getException());
                    }
                    else {
                        source.setException(new FirestormException("Failed to run transaction."));
                    }
                }
            });
        });
        return source.getTask();
    }

    /**
     * Runs a batch write operation.
     *
     * @param batch The batch to run.
     */
    public static Task<Void> runBatch(final FirestormBatch batch) {
        return batch.doBatch();
    }

}
