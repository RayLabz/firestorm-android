package com.raylabz.firestormandroid;

import com.google.android.gms.tasks.Task;

/**
 * Abstracts the fetch method, used by filtered query objects to fetch the results.
 * @param <T> The type of objects used in this filterable.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface Filterable<T> {

    /**
     * Fetches the results of a filterable.
     * @return An ArrayList containing the results of a filter.
     */
    Task<QueryResult<T>> fetch();

}
