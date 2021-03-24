package tests.gitlab.ckpt1;

import cse332.types.CensusData;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import queryresponders.SimpleParallel;
import tests.gitlab.QueryResponderTests;

public class SimpleParallelTests extends QueryResponderTests {

    static CensusData data;

    @BeforeClass
    public static void init() {
        data = readCensusdata();
        STUDENT_100_500 = new SimpleParallel(data, 100, 500);
        STUDENT_20_40 = new SimpleParallel(data, 20, 40);
    }

    /**
     * 100 x 500 grid
     */

    @Test
    public void testFullMap_100_500() {
        assertEquals(312471327, STUDENT_100_500.getPopulation(1, 1,100, 500));
    }

    @Test
    public void testLeftHalf_100_500() {
        assertEquals(27820072, STUDENT_100_500.getPopulation(1, 1, 50, 500));
    }

    @Test
    public void testRightHalf_100_500() {
        assertEquals(284651255, STUDENT_100_500.getPopulation(51, 1, 100, 500));
    }

    @Test
    public void testCenter_100_500() {
        assertEquals(7084297, STUDENT_100_500.getPopulation(20, 200, 60, 300));
        assertEquals(179539805, STUDENT_100_500.getPopulation(50, 100, 90, 250));
    }

    @Test
    public void testCorners_100_500() {
        assertEquals(0, STUDENT_100_500.getPopulation(1, 1, 1, 1));
        assertEquals(207111, STUDENT_100_500.getPopulation(100, 1, 100, 1));
        assertEquals(0, STUDENT_100_500.getPopulation(1, 500, 1, 500));
        assertEquals(0, STUDENT_100_500.getPopulation(100, 500, 100, 500));
    }

    @Test
    public void testHawaii_100_500() {
        // Query Rectangle Coordinates: -163.33711  0.31838202 -151.48657 0.4372290
        assertEquals(1360301, STUDENT_100_500.getPopulation(10, 1, 20, 40));
    }

    @Test
    public void testCanada_100_500() {
        // Query Rectangle Coordinates: -120.24425  1.0582047 -65.30085 1.6554109
        assertEquals(0, STUDENT_100_500.getPopulation(50, 250, 100, 450));
    }

    @Test
    public void testTotalPopulation_100_500() {
        assertEquals(312471327, STUDENT_100_500.getTotalPopulation());
    }

    /**
     * 20 x 40 grid
     */

    @Test
    public void testLeftHalf_20_40() {
        assertEquals(27820072, STUDENT_20_40.getPopulation(1, 1, 10, 40));
    }

    @Test
    public void testRightHalf_20_40() {
        assertEquals(284651255, STUDENT_20_40.getPopulation(11, 1, 20, 40));
    }

    @Test
    public void testCenter_20_40() {
        assertEquals(62861861, STUDENT_20_40.getPopulation(5, 10, 15, 30));
    }

    @Test
    public void testCorners_20_40() {
        assertEquals(0, STUDENT_100_500.getPopulation(1, 1, 1, 1));
        assertEquals(0, STUDENT_100_500.getPopulation(20, 1, 20, 1));
        assertEquals(0, STUDENT_100_500.getPopulation(1, 40, 1, 40));
        assertEquals(0, STUDENT_100_500.getPopulation(20, 40, 20, 40));
    }

    @Test
    public void testHawaii_20_40() {
        // Query Rectangle Coordinates: -163.33711  0.31838202 -151.48657 0.4372290
        assertEquals(1360301, STUDENT_20_40.getPopulation(2, 1, 4, 3));
    }

    @Test
    public void testCanada_20_40() {
        // Query Rectangle Coordinates: -120.24425  1.0582047 -65.30085 1.6554109
        assertEquals(0, STUDENT_20_40.getPopulation(10, 20, 20, 35));
    }

    @Test
    public void testTotalPopulation_20_40() {
        assertEquals(312471327, STUDENT_20_40.getTotalPopulation());
    }
}