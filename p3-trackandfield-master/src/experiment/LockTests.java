package experiment;

import cse332.types.CensusData;
import queryresponders.ComplexLockBased;
import queryresponders.ComplexParallel;
import tests.gitlab.QueryResponderTests;

public class LockTests extends QueryResponderTests {
    static CensusData data;

    public static void main(String[] args) {
        System.out.println(testComplexParallel(1, 5) + " MS for ComplexParallel");
        System.out.println(testComplexLockBased(1, 5) + " MS for ComplexLockBased");
    }

    public static double testComplexParallel(int numColumns, int numRows) {
        data = readCensusdata();
        final int NUM_TESTS = 200;
        final int NUM_WARMUP = 50;

        double totalTime = 0;
        for (int i = 0; i < NUM_TESTS; i++) {

            long startTime = System.currentTimeMillis(); // start timer

            new ComplexParallel(data, numColumns, numRows);

            long endTime = System.currentTimeMillis(); // end timer

            if (NUM_WARMUP <= i) { // Throw away first NUM_WARMUP runs to exclude JVM warmup
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (NUM_TESTS - NUM_WARMUP);
    }

    public static double testComplexLockBased(int numColumns, int numRows) {
        data = readCensusdata();
        final int NUM_TESTS = 10000;
        final int NUM_WARMUP = 3990;

        double totalTime = 0;
        for (int i = 0; i < NUM_TESTS; i++) {

            long startTime = System.currentTimeMillis(); // start timer

            new ComplexLockBased(data, numColumns, numRows);

            long endTime = System.currentTimeMillis(); // end timer

            if (NUM_WARMUP <= i) { // Throw away first NUM_WARMUP runs to exclude JVM warmup
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (NUM_TESTS - NUM_WARMUP);

    }
}
