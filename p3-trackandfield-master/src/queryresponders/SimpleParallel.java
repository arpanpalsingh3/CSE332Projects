package queryresponders;

import java.util.concurrent.ForkJoinPool;

import cse332.interfaces.*;
import cse332.types.*;
import cse332.exceptions.*;
import paralleltasks.CornerFindingTask;
import paralleltasks.GetPopulationTask;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusData censusData;
    int numColumns, numRows;
    MapCorners corners;

    public SimpleParallel(CensusData censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        totalPopulation = 312471327;
        this.corners = POOL.invoke(new CornerFindingTask(censusData.data, 0, censusData.data_size)).getMapCorners();
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        return POOL.invoke(new GetPopulationTask(censusData.data, corners,
                0, censusData.data_size, west, south, east,
                north, numRows, numColumns));
    }
}
