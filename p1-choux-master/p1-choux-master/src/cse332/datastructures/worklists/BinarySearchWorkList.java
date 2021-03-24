package cse332.datastructures.worklists;

import cse332.interfaces.worklists.WorkList;

public class BinarySearchWorkList<E extends Comparable<E>> extends WorkList<E> {
    private int lo, hi;
    private int location;
    private E[] work;
    private E item;

    public BinarySearchWorkList(E[] work) {
        this.work = work;
        java.util.Arrays.sort(work);
        this.location = -1;
    }
    
    public void setItem(E item) {
        this.lo = 0;
        this.location = this.work.length / 2;
        this.hi = this.work.length;
        this.item = item;
    }

    @Override
    public boolean hasWork() {
        return this.location >= 0;
    }

    @Override
    public void add(E work) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E peek() {
        return this.work[location];
    }

    @Override
    public E next() {
        if (location < 0) {
            return null;
        }
        E next = this.work[location];
        int result = item.compareTo(next);
        if (result == 0) {
            location = -1;
            return next;
        }

        // item < next
        if (result < 0) {
            hi = location;
        }
        // item > next
        else {
            lo = location + 1;
        }
        
        if (hi - lo < 1) {
            location = -1;
        }
        else {
            location = (hi - lo)/2;
        }
        
        return next;
    }

    @Override
    public int size() {
        return this.work.length;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
