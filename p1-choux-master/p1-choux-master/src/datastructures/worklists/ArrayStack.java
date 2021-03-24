package datastructures.worklists;

import cse332.interfaces.worklists.LIFOWorkList;

import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/LIFOWorkList.java
 * for method specifications.
 */
public class ArrayStack<E> extends LIFOWorkList<E> {
    private int size;
    private int maxCap;
    private int top;
    private E[] arrayStack;

    // Constructor
    public ArrayStack() {
        maxCap = 10;
        size = 0;
        top = 0;
        this.arrayStack= (E[])new Object[maxCap];
    }

    // Add to the top of the stack
    @Override
    public void add(E work) {
        // If we reach maxCap, have to place data in placeholder array, then copy into new larger array
        if (top == maxCap) {
            E[] copy = (E[]) new Object[maxCap];
            // copy into placeholder
            for(int i = 0; i < size; i++) {
                copy[i] = this.arrayStack[i];
            }
            // double the cap
            maxCap = maxCap*2;
            // make new array
            this.arrayStack = (E[])new Object[maxCap];
            // copy back
            for (int i = 0; i < size; i++) {
                this.arrayStack[i] = copy[i];
            }
        }
        // add to the top, increase size and top
        arrayStack[top] = work;
        size++;
        top++;
    }

    @Override
    public E peek() {
        if (top == 0 ) {
            throw new NoSuchElementException();
        }
        return arrayStack[top - 1];
    }

    // Basically popping off stack
    @Override
    public E next() {
        // if its 0, array is empty
        if (top == 0 ) {
            throw new NoSuchElementException();
        }
        // grab the highest value on stack
        E topVal = arrayStack[top-1];
        // make that position empty now
        arrayStack[top-1] = null;
        // move the top down
        top--;
        size--;
        // if top is now below size, meaning MAX capacity array
        if (maxCap != 1 && top < maxCap / 2 ) {
            // drop the capacity so when next time we add, the array will be recopied into a smaller array
            maxCap = maxCap/2;
        }
        // return what you took off stack
        return topVal;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        maxCap = 10;
        top = 0;
        size = 0;
        this.arrayStack = (E[])new Object[maxCap];
    }
}
