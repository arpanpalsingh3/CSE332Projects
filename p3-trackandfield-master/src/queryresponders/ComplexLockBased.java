package queryresponders;

import cse332.interfaces.*;
import cse332.types.*;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateLockedGridTask;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ComplexLockBased extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    final static int NUM_THREADS = 4;
    private final int[][] popGrid;
    CensusData censusData;
    int numColumns, numRows;
    MapCorners corners;

    public ComplexLockBased(CensusData censusData, int numColumns, int numRows) {
        totalPopulation = 312471327;
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.corners = POOL.invoke(new CornerFindingTask(censusData.data, 0, censusData.data_size)).getMapCorners();
        int section = censusData.data_size / NUM_THREADS;
        Lock[][] lockGrid = new Lock[numColumns][numRows];
        for (int i = 0; i < lockGrid.length; i++) {
            for (int j = 0; j < lockGrid[0].length; j++) {
                lockGrid[i][j] = new ReentrantLock();
            }
        }
        this.popGrid = new int[numColumns][numRows];
        PopulateLockedGridTask data_1 = new PopulateLockedGridTask(censusData.data, 0, section, numRows, numColumns,
                corners, numColumns, numRows, popGrid, lockGrid);
        PopulateLockedGridTask data_2 = new PopulateLockedGridTask(censusData.data, section, section*2, numRows, numColumns,
                corners, numColumns, numRows, popGrid, lockGrid);
        PopulateLockedGridTask data_3 = new PopulateLockedGridTask(censusData.data, section*2, section*3, numRows, numColumns,
                corners, numColumns, numRows, popGrid, lockGrid);
        PopulateLockedGridTask data_4 = new PopulateLockedGridTask(censusData.data, section*3, section*4, numRows, numColumns,
                corners, numColumns, numRows, popGrid, lockGrid);

        data_2.start();
        data_3.start();
        data_4.start();
        data_1.run();
        try {
            data_2.join();
            data_3.join();
            data_4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        for (int i = 1; i < popGrid[0].length; i++) {
            popGrid[0][i] += popGrid[0][i - 1];
        }
        for (int j = 1; j < popGrid.length; j++) {
            popGrid[j][0] += popGrid[j - 1][0];
        }
        for (int k = 1; k < popGrid[0].length; k++) {
            for (int l = 1; l < popGrid.length; l++) {
                popGrid[l][k] += popGrid[l - 1][k] + popGrid[l][k - 1] - popGrid[l - 1][k - 1];
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        int topLeft, bottomRight, bottomLeft;

        if (south - 2 < 0) {
            topLeft = 0;
        } else
            topLeft = popGrid[east - 1][south - 2];
        if (west - 2 < 0) {
            bottomRight = 0;
        } else
            bottomRight = popGrid[west - 2][north - 1];
        if (south - 2 < 0 || west - 2 < 0) {
            bottomLeft = 0;
        } else
            bottomLeft = popGrid[west - 2][south - 2];

        return popGrid[east - 1][north - 1] - topLeft - bottomRight + bottomLeft;
    }
}
