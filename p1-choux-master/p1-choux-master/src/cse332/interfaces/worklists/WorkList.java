package cse332.interfaces.worklists;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An object that stores work to be done in some specified order. This class
 * should never be sub-classed directly. Further sub=classes indicate exactly
 * how the elements should be handled.
 *
 * @author Adam Blank
 * @param <E>
 *            the type of elements in the worklist
 */
public abstract class WorkList<E> implements Iterable<E> {
    public WorkList() {
    }

    /**
     * Returns true iff this worklist has any remaining work
     *
     * @return true iff there is at least one piece of work in the worklist.
     */
    public boolean hasWork() {
        return this.size() > 0;
    }

    /**
     * Adds work to the worklist. This method should conform to any additional
     * contracts that the particular type of worklist has.
     *
     * @param work
     *            the work to add to the worklist
     */
    public abstract void add(E work);

    /**
     * Returns a view to the next element of the worklist.
     *
     * @precondition hasWork() is true
     * @postcondition return(peek()) is return(next())
     * @postcondition the structure of this worklist remains unchanged.
     * @throws NoSuchElementException
     *             if hasWork() is false
     * @return the next element in this worklist
     */
    public abstract E peek();

    /**
     * Returns and removes the next element of the worklist
     *
     * @precondition hasWork() is true
     * @postcondition return(next()) ++ after(next()) == before(next())
     * @postcondition after(size()) + 1 == before(size())
     * @throws NoSuchElementException
     *             if hasWork() is false
     * @return the next element in this worklist
     */
    public abstract E next();

    /**
     * Returns the number of elements of work remaining in this worklist
     *
     * @return the size of this worklist
     */
    public abstract int size();

    /**
     * Resets this worklist to the same state it was in right after
     * construction.
     */
    public abstract void clear();

    private class WorkListIterator implements Iterator<E> {
        @Override
        public boolean hasNext() {
            return WorkList.this.hasWork();
        }

        @Override
        public E next() {
            return WorkList.this.next();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new WorkListIterator();
    }

    /**
     * Returns some basic information about this particular worklist.
     *
     * Calling this method does not consume the worklist.
     *
     * @return a string representation of this worklist
     */
    @Override
    public String toString() {
        if (this.hasWork()) {
            return String.format("%s(size = %d, peek = %s)",
                    this.getClass().getSimpleName(),
                    this.size(),
                    this.peek());
        } else {
            return String.format("%s(size = %d)",
                    this.getClass().getSimpleName(),
                    this.size());
        }
    }
}
