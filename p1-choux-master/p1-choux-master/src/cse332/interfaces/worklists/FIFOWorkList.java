package cse332.interfaces.worklists;

/**
 * A subclass of WorkList that stores its elements in FIFO order. All subclasses
 * of this class implicitly agree to the contract to be a FIFO queue. That is,
 * addWork() must add to the "end" and next() must remove from the "beginning".
 *
 * @author Adam Blank
 * @param <E>
 *            the type of elements in the worklist
 */
public abstract class FIFOWorkList<E> extends WorkList<E> {
}
