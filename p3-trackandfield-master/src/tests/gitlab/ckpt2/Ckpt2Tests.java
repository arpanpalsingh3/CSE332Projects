package tests.gitlab.ckpt2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        ComplexSequentialTests.class,
        ComplexParallelTests.class,
        ComplexLockBasedTests.class
})

public class Ckpt2Tests {
}
