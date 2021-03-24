package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusData;
import cse332.types.MapCorners;

public class ComplexSequential extends QueryResponder {
    private final int[][] popGrid;
    CensusData cenData;
    int numColumns;
    int numRows;
    MapCorners corners;

    public ComplexSequential(CensusData censusData, int numColumns, int numRows) {
        totalPopulation = 312471327; // set the total population
        cenData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        // create my new corners object using the first data point
        this.corners = calculateCornersSeq(cenData);
        // create the grid to store
        this.popGrid = buildGrid(cenData);

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

    public int[][] buildGrid(CensusData cd) {
        int [][] tempGrid = new int[numColumns][numRows];
        // STEP 1;
        int xCord, yCord;
        for(int i = 0; i < cd.data_size; i++) {
            // converting the values of longitude and latitude into ints for the grid
            xCord = (int) Math.floor((cd.data[i].longitude - corners.west) / ((corners.east - corners.west) / numColumns));
            yCord = (int) Math.floor((cd.data[i].latitude - corners.south) / ((corners.north - corners.south) / numRows));
            // top right corner
            if (xCord == numColumns && yCord == numRows)
                tempGrid[xCord - 1][yCord - 1] += cd.data[i].population;
            // right most column
            else if (xCord == numColumns)
                tempGrid[xCord - 1][yCord] += cd.data[i].population;
            // top row
            else if (yCord == numRows)
                tempGrid[xCord][yCord - 1] += cd.data[i].population;
            else
                tempGrid[xCord][yCord] += cd.data[i].population;
        }
        // STEP 2:
        // Calculating the most left column (add everything below it)
        for (int i = 1; i < tempGrid[0].length; i++)
            tempGrid[0][i] += tempGrid[0][i - 1];
        // Calculating the most bottom row (add everything to the left of it)
        for (int j = 1; j < tempGrid.length; j++)
            tempGrid[j][0] += tempGrid[j - 1][0];
        // the rest of the grid calculation for west south addition
        for (int k = 1; k < tempGrid[0].length; k++) {
            for (int l = 1; l < tempGrid.length; l++)
                tempGrid[l][k] += tempGrid[l - 1][k] + tempGrid[l][k - 1] - tempGrid[l - 1][k - 1];
        }
        return tempGrid;
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        int topLeft, bottomRight, bottomLeft;

        // covers all the cases where it could be outside the grid
        if (south - 2 < 0) {
            topLeft = 0; }else
            topLeft = popGrid[east - 1][south - 2];

        if (west - 2 < 0) {
            bottomRight = 0; }else
            bottomRight = popGrid[west - 2][north - 1];

        if (south - 2 < 0 || west - 2 < 0) {
            bottomLeft = 0; }else
            bottomLeft = popGrid[west - 2][south - 2];

        // top right - top left - bottom right + bottom left = population of asked box
        return popGrid[east - 1][north - 1] - topLeft - bottomRight + bottomLeft;
    }


}
