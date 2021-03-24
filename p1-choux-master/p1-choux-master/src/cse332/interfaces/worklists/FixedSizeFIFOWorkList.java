package cse332.interfaces.worklists;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A subclass of FIFOWorkList that has a capacity. This subclass of WorkList can
 * be useful when performance is important; the limit on the number of elements
 * in the queue can allow the implementation to be faster.
 *
 * @author Adam Blank
 *
 * @param <E>
 *            the type of element in the worklist
 */
public abstract class FixedSizeFIFOWorkList<E> extends FIFOWorkList<E>
        implements Comparable<FixedSizeFIFOWorkList<E>> {
    private final int capacity;

    public FixedSizeFIFOWorkList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity should be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Adds work to the worklist. This method should conform to any additional
     * contracts that the particular type of worklist has.
     *
     * @precondition isFull() is false
     *
     * @param work
     *            the work to add to the worklist
     * @throws IllegalStateException
     *             iff isFull()
     */
    @Override
    public abstract void add(E work);

    /**
     * Returns a view of the ith element of the worklist. Since this worklist is
     * a FIFO worklist, it has a well-defined order.
     *
     * @precondition 0 <= i < size()
     * @postcondition the structure of this worklist remains unchanged
     * @throws NoSuchElementException
     *             if hasWork() is false (this exception takes precedence over
     *             all others)
     * @throws IndexOutOfBoundsException
     *             if i < 0 or i >= size()
     *
     * @param i
     *            the index of the element to peek at
     * @return the ith element in this worklist
     */
    public abstract E peek(int i);

    /**
     * Replaces the ith element of this worklist with value. Since this worklist
     * is a FIFO worklist it has a well-defined order.
     *
     * @precondition 0 <= i < size()
     * @postcondition only the ith element of the structure is changed
     * @throws NoSuchElementException
     *             if hasWork() is false (this exception takes precedence over
     *             all others)
     * @throws IndexOutOfBoundsException
     *             if i < 0 or i >= size()
     *
     * @param i
     *            the index of the element to update
     * @param value
     *            the value to update index i with
     */
    public abstract void update(int i, E value);

    /**
     * This element returns the capacity of the worklist.
     *
     * @return the capacity of the worklist
     */
    public int capacity() {
        return this.capacity;
    }

    /**
     * Returns true iff this worklist cannot accommodate another element.
     *
     * @return true if this worklist is full
     */
    public boolean isFull() {
        return size() >= this.capacity();
    }

    @Override
    public Iterator<E> iterator() {
        return new FixedSizeWorkListIterator();
    }

    private class FixedSizeWorkListIterator implements Iterator<E> {
        int idx;

        @Override
        public boolean hasNext() {
            return this.idx < size();
        }

        @Override
        public E next() {
            return FixedSizeFIFOWorkList.this.peek(this.idx++);
        }
    }
}
