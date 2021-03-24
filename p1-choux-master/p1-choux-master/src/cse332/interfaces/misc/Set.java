package cse332.interfaces.misc;

import java.util.Iterator;

import cse332.datastructures.containers.Item;

public abstract class Set<E> implements Iterable<E> {
    protected Dictionary<E, Boolean> map;

    @SuppressWarnings("unused")
    protected Set() {
    }

    protected Set(Dictionary<E, Boolean> backingMap) {
        this.map = backingMap;
    }

    public final void add(E e) {
        this.map.insert(e, true);
    }

    public final void delete(E e) {
        this.map.delete(e);
    }

    public final boolean contains(E e) {
        return this.map.find(e) != null;
    }

    public final int size() {
        return this.map.size();
    }

    public final boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new SetIterator();
    }

    private class SetIterator implements Iterator<E> {
        private final Iterator<Item<E, Boolean>> mapIterator = Set.this.map.iterator();

        @Override
        public boolean hasNext() {
            return this.mapIterator.hasNext();
        }

        @Override
        public E next() {
            return this.mapIterator.next().key;
        }
    }

    public void clear() {
        this.map.clear();
    }

    @Override
    public String toString() {
        return this.map.toString();
    }
}
