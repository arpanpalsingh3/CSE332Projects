package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
   1) This class is used in version 5 to create the initial grid holding the total population for each grid cell
        - You should not be using the ForkJoin framework but instead should make use of threads and locks
        - Note: the resulting grid after all threads have finished running should be the same as the final grid from
          PopulateGridTask.java
 */

public class PopulateLockedGridTask extends Thread{
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;
    int[][] populationGrid;
    Lock[][] lockGrid;


    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners,
                                  double cellWidth, double cellHeight, int[][] popGrid, Lock[][] lockGrid) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.populationGrid = popGrid;
        this.lockGrid = lockGrid;
    }

    @Override
    public void run() {
        int xCord, yCord;
        for(int i = lo; i < hi; i++) {
            xCord = (int) Math.floor((censusGroups[i].longitude - corners.west) / ((corners.east - corners.west) / numColumns));
            yCord = (int) Math.floor((censusGroups[i].latitude - corners.south) / ((corners.north - corners.south) / numRows));
            if (xCord == numColumns && yCord == numRows) {
                lockGrid[xCord - 1][yCord - 1].lock();
                populationGrid[xCord - 1][yCord - 1] += censusGroups[i].population;
                lockGrid[xCord - 1][yCord - 1].unlock();
            } else if (xCord == numColumns) {
                lockGrid[xCord - 1][yCord].lock();
                populationGrid[xCord - 1][yCord] += censusGroups[i].population;
                lockGrid[xCord - 1][yCord].unlock();
            } else if (yCord == numRows) {
                lockGrid[xCord][yCord - 1].lock();
                populationGrid[xCord][yCord - 1] += censusGroups[i].population;
                lockGrid[xCord][yCord - 1].unlock();
            } else {
                lockGrid[xCord][yCord].lock();
                populationGrid[xCord][yCord] += censusGroups[i].population;
                lockGrid[xCord][yCord].unlock();
            }
        }
    }
}
