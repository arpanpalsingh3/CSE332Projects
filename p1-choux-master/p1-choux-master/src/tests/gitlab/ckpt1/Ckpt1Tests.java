package tests.gitlab.ckpt1;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        ArrayStackTests.class,
        ListFIFOQueueTests.class,
        CircularArrayFIFOQueueTests.class
})

public class Ckpt1Tests {

}