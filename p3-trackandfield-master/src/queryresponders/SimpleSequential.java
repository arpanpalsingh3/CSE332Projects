package queryresponders;

import cse332.interfaces.*;
import cse332.types.*;

public class SimpleSequential extends QueryResponder {

    CensusData cenData;
    int numColumns;
    int numRows;
    MapCorners corners;

    public SimpleSequential(CensusData censusData, int numColumns, int numRows) {
        totalPopulation = 312471327; // set the total population
        cenData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        // create my new corners object using the first data point
        this.corners = calculateCornersSeq(cenData);

    }
    public MapCorners calculateCornersSeq(CensusData censusData) {

        MapCorners tempCorners = new MapCorners(censusData.data[0]);
        MapCorners challenger;

        for(int i = 1; i < cenData.data_size; i++) {
            challenger = new MapCorners(censusData.data[i]);
            tempCorners = tempCorners.encompass(challenger);
        }
        return tempCorners;
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        int boxPopulation = 0;

        float columnDis = (corners.east - corners.west)/ numColumns;
        float rowDis = (corners.north - corners.south)/ numRows;

        for(int i = 0; i < cenData.data_size; i++) {
            float x = (cenData.data[i].longitude - corners.west) / columnDis + 1;
            float y = (cenData.data[i].latitude - corners.south) / rowDis + 1;
            // check to see if these coordinates are actually in this box
            if (checkGrid(x,y,west,south,east,north)) {
                // if they are, add this data points population to the total population of this box
                boxPopulation += cenData.data[i].population;
            }
        }

        return boxPopulation;

    }

    public boolean checkGrid(float x, float y, int west, int south, int east, int north) {
        // the 4 cases when its in my box
        if(x >= west && x < east + 1 && y >= south && y < north + 1) {
            return true;
        }
        if(x == east + 1 && x == numColumns + 1 && y >= south && y < north + 1) {
            return true;
        }
        if(y == north + 1 && y == numRows + 1 && x >= west && x < east + 1) {
            return true;
        }
        return x == east + 1 && x == numColumns + 1 && y == north + 1 && y == numRows + 1;

    }
}
