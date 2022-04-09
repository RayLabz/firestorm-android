package com.raylabz.firestormandroid;

import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;

/**
 * Models a result returned from a query.
 * @param <T> The type of objects in the result.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public class QueryResult<T> {

    private final List<T> items;
    private final List<DocumentSnapshot> snapshots;
    private final String lastDocumentID;

    public QueryResult(List<T> items, List<DocumentSnapshot> snapshots, String lastDocumentID) {
        this.items = items;
        this.snapshots = snapshots;
        this.lastDocumentID = lastDocumentID;
    }

    public List<T> getItems() {
        return items;
    }

    public List<DocumentSnapshot> getSnapshots() {
        return snapshots;
    }

    public String getLastDocumentID() {
        return lastDocumentID;
    }

    public boolean hasItems() {
        return (!items.isEmpty());
    }

}
