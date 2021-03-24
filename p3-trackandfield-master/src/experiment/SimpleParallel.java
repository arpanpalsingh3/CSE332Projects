package experiment;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusData;
import cse332.types.MapCorners;
import experiment.CornerFindingTask;
import experiment.GetPopulationTask;

import java.util.concurrent.ForkJoinPool;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusData censusData;
    int numColumns, numRows;
    MapCorners corners;
    int CUTOFF;

    public SimpleParallel(CensusData censusData, int numColumns, int numRows, int CUTOFF) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        totalPopulation = 312471327;
        this.CUTOFF = CUTOFF;
        this.corners = POOL.invoke(new CornerFindingTask(censusData.data, 0, censusData.data_size, CUTOFF)).getMapCorners();
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        return POOL.invoke(new GetPopulationTask(censusData.data, corners,
                0, censusData.data_size, west, south, east,
                north, numRows, numColumns, CUTOFF));
    }
}
