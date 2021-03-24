package experiment;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusData;
import queryresponders.SimpleParallel;
import queryresponders.ComplexParallel;
import queryresponders.ComplexSequential;
import queryresponders.SimpleSequential;
import tests.gitlab.QueryResponderTests;

public class PreprocessingTests extends QueryResponderTests {
    static CensusData data = readCensusdata();

    public static double simpleSequentialTiming(int queries) {
        final int NUM_TESTS = 2000;
        final int NUM_WARMUP = 1000;

        double totalTime = 0;
        for (int i = 0; i < NUM_TESTS; i++) {
            long startTime = System.currentTimeMillis(); // start timer
            QueryResponder simpSeq = new SimpleSequential(data, 100, 500);
            for ( int j = 0; j < queries; j++) {
                simpSeq.getPopulation(10, 0, 50, 50);
            }
            long endTime = System.currentTimeMillis(); // end timer

            if (NUM_WARMUP <= i) { // Throw away first NUM_WARMUP runs to exclude JVM warmup
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (NUM_TESTS - NUM_WARMUP);
    }
    public static double complexSequentialTiming(int queries) {
        final int NUM_TESTS = 2000;
        final int NUM_WARMUP = 1000;

        double totalTime = 0;
        for (int i = 0; i < NUM_TESTS; i++) {
            long startTime = System.currentTimeMillis(); // start timer
            QueryResponder compSeq = new ComplexSequential(data, 100, 500);
            for ( int j = 0; j < queries; j++) {
                compSeq.getPopulation(10, 0, 50, 50);
            }
            long endTime = System.currentTimeMillis(); // end timer

            if (NUM_WARMUP <= i) { // Throw away first NUM_WARMUP runs to exclude JVM warmup
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (NUM_TESTS - NUM_WARMUP);
    }

    public static double simpleParallelTiming(int queries) {
        final int NUM_TESTS = 2000;
        final int NUM_WARMUP = 1000;

        double totalTime = 0;
        for (int i = 0; i < NUM_TESTS; i++) {
            long startTime = System.currentTimeMillis(); // start timer
            QueryResponder simpPara = new SimpleParallel(data, 100, 500);
            for ( int j = 0; j < queries; j++) {
                simpPara.getPopulation(10, 0, 50, 50);
            }
            long endTime = System.currentTimeMillis(); // end timer

            if (NUM_WARMUP <= i) { // Throw away first NUM_WARMUP runs to exclude JVM warmup
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (NUM_TESTS - NUM_WARMUP);
    }

    public static double compParallelTiming(int queries) {
        final int NUM_TESTS = 2000;
        final int NUM_WARMUP = 1000;

        double totalTime = 0;
        for (int i = 0; i < NUM_TESTS; i++) {
            long startTime = System.currentTimeMillis(); // start timer
            QueryResponder compPara = new ComplexParallel(data, 100, 500);
            for ( int j = 0; j < queries; j++) {
                compPara.getPopulation(10, 0, 50, 50);
            }
            long endTime = System.currentTimeMillis(); // end timer

            if (NUM_WARMUP <= i) { // Throw away first NUM_WARMUP runs to exclude JVM warmup
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (NUM_TESTS - NUM_WARMUP);
    }

    public static void main(String[] args) {
        // all the tests for Sequential comparisons
        for(int i = 0; i <= 10; i ++) {
            System.out.println(simpleSequentialTiming(i) + " for SimpleSequential");
            System.out.println(complexSequentialTiming(i) + " for ComplexSequential");
            System.out.println("Above are the times for " + i);
        }

        // all the tests for Parallel comparisons
        for(int i = 0; i <= 10; i ++) {
            System.out.println(simpleParallelTiming(i) + " for SimpleParallel");
            System.out.println(compParallelTiming(i) + " for ComplexParallel");
            System.out.println("Above are the times for " + i);
        }


    }

}
