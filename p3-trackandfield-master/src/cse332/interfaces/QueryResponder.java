package cse332.interfaces;

import cse332.types.CensusData;

/**
 *
 *
 * This class provides a skeletal implementation of the QueryResponder
 * interface needed to store the population of an area and get the
 * population of a subset of that area
 *
 * @author Winston Jodjana <winj@cs.washington.edu>
 * @author Richard Jiang <rjiang98@cs.washington.edu>
 * @author Hamsa Shankar <hamsas@cs.washington.edu>
 */

public abstract class QueryResponder {
    protected int totalPopulation;  // Total population of the land mass

    /**
     * Method that returns the total population in the entire grid
     * @return the total population in the entire grid
     */
    public int getTotalPopulation() {
        return this.totalPopulation;
    }

    /**
     * Method that returns the total population in a query rectangle on
     * the grid defined by the four parameters passed in
     * @param west The Western-most grid column that is part of the query rectangle; error if this is less than 1 or greater than numColumns.
     * @param south The Southern-most grid row that is part of the query rectangle; error if this is less than 1 or greater than numRows.
     * @param east The Eastern-most grid column that is part of the query rectangle; error if this is less than the Western-most column (equal is okay) or greater than numColumns.
     * @param north The Northern-most grid row that is part of the query rectangle; error if this is less than the Southern-most column (equal is okay) or greater than numRows.
     * @return the total population in the query rectangle
     */
    public abstract int getPopulation(int west, int south, int east, int north);
}
