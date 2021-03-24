package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusData;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;

import java.util.concurrent.ForkJoinPool;

public class    ComplexParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    int cols, rows;
    CensusData censusData;
    MapCorners corners;
    private final int[][] popGrid;


    public ComplexParallel(CensusData censusData, int numColumns, int numRows) {
        totalPopulation = 312471327;
        this.cols = numColumns;
        this.rows = numRows;
        this.censusData = censusData;

        this.corners = POOL.invoke(new CornerFindingTask(censusData.data, 0, censusData.data_size)).getMapCorners();
        this.popGrid = POOL.invoke(new PopulateGridTask(censusData.data, 0, censusData.data_size, numRows, numColumns, corners, 0, 0));
        for (int i = 1; i < popGrid[0].length; i++)
            popGrid[0][i] += popGrid[0][i - 1];
        for (int j = 1; j < popGrid.length; j++)
            popGrid[j][0] += popGrid[j - 1][0];
        for (int k = 1; k < popGrid[0].length; k++) {
            for (int l = 1; l < popGrid.length; l++)
                popGrid[l][k] += popGrid[l - 1][k] + popGrid[l][k - 1] - popGrid[l - 1][k - 1];
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
