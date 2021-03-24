package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusData;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/*
   1) This class is used in version 4 to create the initial grid holding the total population for each grid cell
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) Note that merging the grids from the left and right subtasks should NOT be done in this class.
      You will need to implement the merging in parallel using a separate parallel class (MergeGridTask.java)
 */

public class PopulateGridTask extends RecursiveTask<int[][]> {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;
    final static int SEQUENTIAL_CUTOFF = 60000;

    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners, double cellWidth, double cellHeight) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    protected int[][] compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialPopulateGrid(censusGroups, lo, hi);
        }
        int mid = lo + (hi - lo) / 2;

        PopulateGridTask left = new PopulateGridTask(censusGroups, lo, mid, numRows, numColumns, corners, 0, 0);
        PopulateGridTask right = new PopulateGridTask(censusGroups, mid, hi, numRows, numColumns, corners, 0, 0);

        left.fork();
        int[][] rightResult = right.compute();
        int[][] leftResult = left.join();

        POOL.invoke(new MergeGridTask(leftResult, rightResult, 0, numRows, 0, numColumns));
        return leftResult;
    }

    private int[][] sequentialPopulateGrid(CensusGroup[] data, int lo, int hi) {
        int [][] tempGrid = new int[numColumns][numRows];
        // STEP 1;
        int xCord, yCord;
        for(int i = lo; i < hi; i++) {
            xCord = (int) Math.floor((data[i].longitude - corners.west) / ((corners.east - corners.west) / numColumns));
            yCord = (int) Math.floor((data[i].latitude - corners.south) / ((corners.north - corners.south) / numRows));
            if (xCord == numColumns && yCord == numRows)
                tempGrid[xCord - 1][yCord - 1] += data[i].population;
            else if (xCord == numColumns)
                tempGrid[xCord - 1][yCord] += data[i].population;
            else if (yCord == numRows)
                tempGrid[xCord][yCord - 1] += data[i].population;
            else
                tempGrid[xCord][yCord] += data[i].population;
        }
        return tempGrid;
    }
}

