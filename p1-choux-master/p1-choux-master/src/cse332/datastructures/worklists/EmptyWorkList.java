package cse332.datastructures.worklists;

import cse332.interfaces.worklists.WorkList;

public class EmptyWorkList<E> extends WorkList<E> {
    public EmptyWorkList() {}
    
    @Override
    public boolean hasWork() {
        return false;
    }

    @Override
    public void add(E work) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public E next() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {}
}
