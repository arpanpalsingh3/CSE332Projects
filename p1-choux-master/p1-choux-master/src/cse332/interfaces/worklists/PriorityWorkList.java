package cse332.interfaces.worklists;

/**
 * A subclass of WorkList that stores its elements in priority order. All
 * subclasses of this class implicitly agree to the contract to be a priority
 * queue. The "priority" of an element should be determined via compareTo(). The
 * direction (min or max) is up to the implementation.
 *
 * @author Adam Blank
 *
 * @param <E>
 *            the type of elements in the worklist; must be comparable
 */
public abstract class PriorityWorkList<E> extends WorkList<E> {
}
