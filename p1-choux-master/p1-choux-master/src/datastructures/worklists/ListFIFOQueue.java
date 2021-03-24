package datastructures.worklists;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.worklists.FIFOWorkList;

import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/FIFOWorkList.java
 * for method specifications.
 */
public class ListFIFOQueue<E> extends FIFOWorkList<E> {
    // Initialize the front and end of linked list and size
    private Node front;
    private Node end;
    private int size;

    // Constructor
    public ListFIFOQueue() {
        front = null;
        end = null;
        size = 0;
    }

    // Add work to queue
    @Override
    public void add(E work) {
        size++;
        // As long as front is empty ( first case ), put work in
        if (front == null) {
            // initialize front and end
            front = new Node(work, null);
            end = front;
        }
        // Keep adding to the back of the list
        else {
            end.next = new Node(work, null);
            // update new end
            end = end.next;
        }
    }
    // Checking whats at the front of the queue
    @Override
    public E peek() {
        if (front == null) {
            throw new NoSuchElementException();
        } else {
            // send out data if not empty (work that you put in)
            return front.data;
        }
    }

    // Move the queue (move front back by 1) and return previous front
    @Override
    public E next() {
        if (front == null) {
            throw new NoSuchElementException();
        } else {
            size--;
            Node prev = front;
            front = front.next;
            return prev.data;
        }
    }
    // check size
    @Override
    public int size() {
        return size;
    }
    // clear the queue
    @Override
    public void clear() {
        front = null;
        end = null;
        size = 0;
    }

    // Inner node class
    public class Node {
        public E data;
        public Node next;

        public Node(E data, Node next) {
            this.data = data;
            this.next = next;
        }
    }
}
