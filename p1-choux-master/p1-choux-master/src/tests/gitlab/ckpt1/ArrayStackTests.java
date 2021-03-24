package tests.gitlab.ckpt1;

import cse332.interfaces.worklists.WorkList;
import datastructures.worklists.ArrayStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayStackTests extends WorklistGradingTests {

    @Before
    public void init() {
        STUDENT_STR = new ArrayStack<>();
        STUDENT_DOUBLE = new ArrayStack<>();
        STUDENT_INT = new ArrayStack<>();
    }

    @Test(timeout = 3000)
    public void checkStructure() {
        WorkList<Integer> stack = new ArrayStack<>();

        // Fill
        for (int i = 0; i < 1000; i++) {
            stack.add(i);
            assertEquals(i, stack.peek().intValue());
            assertTrue(stack.hasWork());
            assertEquals((i + 1), stack.size());
        }

        // Empty
        for (int i = 999; i >= 0; i--) {
            assertTrue(stack.hasWork());
            assertEquals (i, stack.peek().intValue());
            assertEquals (i, stack.next().intValue());
            assertEquals (i, stack.size());
        }
    }
}
