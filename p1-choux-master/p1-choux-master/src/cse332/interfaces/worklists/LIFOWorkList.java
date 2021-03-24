package cse332.interfaces.worklists;

/**
 * A subclass of WorkList that stores its elements in LIFO order. All subclasses
 * of this class implicitly agree to the contract to be a LIFO queue. That is,
 * addWork() must add to the "top" and next() must remove from the "top".
 *
 * @author Adam Blank
 * @param <E>
 *            the type of elements in the worklist
 */
public abstract class LIFOWorkList<E> extends WorkList<E> {
}
