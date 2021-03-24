package cse332.interfaces.misc;

import java.util.Iterator;

/**
 *  Interface for a simpler iterator for returning E elements from a
 *  data structure one at a time. We do not use Java's iterators
 *  don't want to obey all the rules for correct iterators.
 *  
 * @author Adam Blank
 *
 * @param <E> the type of elements to iterate through
 */
public abstract class SimpleIterator<E> implements Iterator<E>, Iterable<E> {
    /**
     * Returns the next element in the iterator
     * @return next element in collection
     * @throws java.util.NoSuchElementException if no next element
     */
    public abstract E next();

    /**
     * Returns whether there are more elements to iterate through
     * @return true if there are more elements
     * @return false if there are not more elements
     */
    public abstract boolean hasNext();

    @Override
    public final void remove() {
        throw new UnsupportedOperationException();

    }

    public final Iterator<E> iterator() {
        return this;
    }
}
