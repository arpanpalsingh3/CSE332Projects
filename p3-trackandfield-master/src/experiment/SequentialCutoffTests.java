package experiment;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusData;
import queryresponders.SimpleSequential;
import tests.gitlab.QueryResponderTests;

public class SequentialCutoffTests extends QueryResponderTests {
    static CensusData data;

    public static double testCutoffsParallel(int CUTOFF) {
        data = readCensusdata();
        final int NUM_TESTS = 10000;
        final int NUM_WARMUP = 3990;

        double totalTime = 0;
        for (int i = 0; i < NUM_TESTS; i++) {
            QueryResponder test;
            long startTime = System.currentTimeMillis(); // start timer

            test = new SimpleParallel(data, 100, 500, CUTOFF);

            long endTime = System.currentTimeMillis(); // end timer

            if (NUM_WARMUP <= i) { // Throw away first NUM_WARMUP runs to exclude JVM warmup
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (NUM_TESTS - NUM_WARMUP);
    }

    public static double testSequentialSimple() {
        data = readCensusdata();
        final int NUM_TESTS = 10000;
        final int NUM_WARMUP = 3990;

        double totalTime = 0;
        for (int i = 0; i < NUM_TESTS; i++) {

            long startTime = System.currentTimeMillis(); // start timer

            new SimpleSequential(data, 100, 500);

            long endTime = System.currentTimeMillis(); // end timer

            if (NUM_WARMUP <= i) { // Throw away first NUM_WARMUP runs to exclude JVM warmup
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (NUM_TESTS - NUM_WARMUP);

    }

    public static void main(String[] args) {
        // testing parallel times with different cutoffs
        for(int i = 1; i <= 20; i ++) {
            System.out.println(testCutoffsParallel(i) + " MS for parallel with CUTOFF " + i );
        }
        System.out.println(testCutoffsParallel(100) + " MS for parallel with CUTOFF " + 100 );
        System.out.println(testCutoffsParallel(500) + " MS for parallel with CUTOFF " + 500 );
        System.out.println(testCutoffsParallel(1000) + " MS for parallel with CUTOFF " + 1000 );
        System.out.println(testCutoffsParallel(5000) + " MS for parallel with CUTOFF " + 5000 );
        for(int i = 10000; i < 220000; i+= 10000) {
            System.out.println(testCutoffsParallel(i) + " MS for parallel with CUTOFF " + i );
        }
        System.out.println(testCutoffsParallel(219404) + " MS for parallel with CUTOFF " + 219404 );
        System.out.println(testCutoffsParallel(219405) + " MS for parallel with CUTOFF " + 219405 );

        System.out.println(testCutoffsParallel(220000) + " MS for parallel with CUTOFF " + 220000 );
        System.out.println(testCutoffsParallel(230000) + " MS for parallel with CUTOFF " + 230000 );
        System.out.println(testCutoffsParallel(240000) + " MS for parallel with CUTOFF " + 240000 );

        // testing sequential time
        System.out.println(testSequentialSimple() + " MS for sequential");
    }
}
