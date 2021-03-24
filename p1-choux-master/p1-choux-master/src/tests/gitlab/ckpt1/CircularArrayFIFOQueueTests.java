package tests.gitlab.ckpt1;

import cse332.interfaces.worklists.FixedSizeFIFOWorkList;
import cse332.interfaces.worklists.WorkList;
import datastructures.worklists.CircularArrayFIFOQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CircularArrayFIFOQueueTests extends WorklistGradingTests {
    public static final int DEFAULT_CAPACITY = 1000;

    @Before
    public void init() {
        STUDENT_STR = new CircularArrayFIFOQueue<>(DEFAULT_CAPACITY);
        STUDENT_DOUBLE = new CircularArrayFIFOQueue<>(DEFAULT_CAPACITY);
        STUDENT_INT = new CircularArrayFIFOQueue<>(100000);
    }

    @Test(timeout = 3000)
    public void testClear() {
        FixedSizeFIFOWorkList<String> queue = new CircularArrayFIFOQueue<>(5);
        addAll(queue, new String[] {"Beware", "the", "Jabberwock", "my", "son!"});
        assertTrue(queue.hasWork());
        assertEquals(5, queue.size());
        assertTrue(queue.isFull());
        assertEquals(5, queue.capacity());

        queue.clear();

        assertFalse(queue.hasWork());
        assertEquals(0, queue.size());
        assertFalse(queue.isFull());
        assertEquals(5, queue.capacity());
        assertTrue(doesPeekThrowException(queue));
        assertTrue(doesNextThrowException(queue));
    }

    @Test(timeout = 3000)
    public void testUpdate() {
        FixedSizeFIFOWorkList<Integer> queue = new CircularArrayFIFOQueue<>(10);

        for (int i = 0; i < 8; i++) {
            queue.add(i);
            assertEquals(i + 1, queue.size());
        }

        for (int i = 0; i < 8; i++) {
            assertEquals(i, queue.peek(i).intValue());
        }

        assertEquals(8, queue.size());
        assertFalse(queue.isFull());

        for (int i = 0; i < 8; i += 2) {
            queue.update(i, -i);
        }

        for (int i = 0; i < 8; i++) {
            int expected = i * ((i % 2 == 0) ? -1 : 1);
            assertEquals(expected, queue.peek(i).intValue());

        }

        assertEquals(8, queue.size());

        for (int i = 0; i < 8; i++) {
            int expected = i * ((i % 2 == 0) ? -1 : 1);
            assertEquals(expected,  queue.peek().intValue());
            assertEquals(expected, queue.next().intValue());

        }
        assertEquals(0, queue.size());
    }

    @Test(timeout = 3000)
    public void testCycle() {
        testCycle(1);
        testCycle(2);
        testCycle(9);
        testCycle(10);
    }


    private void testCycle(int capacity) {
        FixedSizeFIFOWorkList<Integer> queue = new CircularArrayFIFOQueue<>(capacity);

        for (int i = 0; i < capacity; i++) {
            queue.add(i);
            assertEquals(i + 1, queue.size());
        }

        assertTrue(queue.hasWork());
        assertTrue(queue.isFull());

        for (int i = capacity; i < 100000; i++) {
            assertEquals((i - capacity), queue.peek().intValue());
            assertEquals((i - capacity), queue.next().intValue());
            assertFalse(queue.isFull());
            queue.add(i);
            assertEquals(capacity, queue.size());
            assertTrue(queue.isFull());
        }
    }

    @Test(timeout = 3000)
    public void checkStructure() {
        FixedSizeFIFOWorkList<Integer> queue = new CircularArrayFIFOQueue<>(1000);

        // Fill
        for (int i = 0; i < 1000; i++) {
            assertFalse(queue.isFull());
            queue.add(i);
            assertEquals(0, queue.peek().intValue());
            assertEquals(0, queue.peek(0).intValue());
            assertEquals(i, queue.peek(i).intValue());
            assertTrue(queue.hasWork());
            assertEquals((i + 1), queue.size());
        }
        assertEquals(1000, queue.size());
        assertTrue(queue.isFull());

        try {
            queue.add(2000);
            Assert.fail("Expected an IllegalStateException");
        } catch (IllegalStateException ex) {
            // Queue throws correct exception when full; move on
        }

        // Check peek
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, queue.peek(i).intValue());
        }

        // Empty
        for (int i = 0; i < 999; i++) {
            assertTrue(queue.hasWork());
            assertEquals(i, queue.peek().intValue());
            assertEquals(i, queue.peek(0).intValue());
            assertEquals(i, queue.next().intValue());
            assertEquals(999 - i, queue.size());
            assertFalse(queue.isFull());
        }
    }
}
