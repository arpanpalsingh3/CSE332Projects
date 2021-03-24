package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;
/*
   1) This class is the parallel version of the getPopulation() method from version 1 for use in version 2
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The double parameters(w, s, e, n) represent the bounds of the query rectangle
   4) The compute method returns an Integer representing the total population contained in the query rectangle
 */
public class GetPopulationTask extends RecursiveTask<Integer> {
    CensusGroup[] censusGroups;
    MapCorners corners;
    int lo, hi, rows, cols;
    double w, s, e, n;
    final static int SEQUENTIAL_CUTOFF = 60000;

    public GetPopulationTask(CensusGroup[] censusGroups, MapCorners corners, int lo, int hi, double w,
                             double s, double e, double n, int rows, int cols) {
        this.censusGroups = censusGroups;
        this.corners = corners;
        this.lo = lo;
        this.hi = hi;
        this.w = w;
        this.s = s;
        this.e = e;
        this.n = n;
        this.rows = rows;
        this.cols = cols;
    }

    // Returns a number for the total population
    @Override
    protected Integer compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialGetPopulation(censusGroups, lo, hi, w, s, e, n);
        }
        int mid = lo + (hi - lo) / 2;

        GetPopulationTask left = new GetPopulationTask(censusGroups, corners, lo, mid, w, s, e, n, rows, cols);
        GetPopulationTask right = new GetPopulationTask(censusGroups, corners, mid, hi, w, s, e, n, rows, cols);

        left.fork();
        int rightResult = right.compute();
        int leftResult = left.join();
        return leftResult + rightResult;
    }

    private Integer sequentialGetPopulation(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n) {
        int pop = 0;
        float colDist = (corners.east - corners.west) / cols;
        float rowDist = (corners.north - corners.south) / rows;

        for (int i = lo; i < hi; i++) {
            float x = (censusGroups[i].longitude - corners.west) /  colDist + 1;
            float y = (censusGroups[i].latitude - corners.south) / rowDist + 1;

            if (checkGrid(x, y, w, s, e, n)) {
                pop += censusGroups[i].population;
            }
        }
        return pop;
    }

    private boolean checkGrid(float x, float y, double west, double south, double east, double north) {
        if(x >= west && x < east + 1 && y >= south && y < north + 1) {
            return true;
        }
        if(x == east + 1 && x == cols + 1 && y >= south && y < north + 1) {
            return true;
        }
        if(y == north + 1 && y == rows + 1 && x >= west && x < east + 1) {
            return true;
        }
        return x == east + 1 && x == cols + 1 && y == north + 1 && y == rows + 1;
    }
}
