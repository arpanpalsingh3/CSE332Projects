package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import cse332.interfaces.*;
import cse332.types.*;
import cse332.exceptions.*;

import queryresponders.*;

public class PopulationQuery {
    // next four constants are relevant to parsing
    public static final int TOKENS_PER_LINE  = 7;
    public static final int POPULATION_INDEX = 4; // zero-based indices
    public static final int LATITUDE_INDEX   = 5;
    public static final int LONGITUDE_INDEX  = 6;

    // parse the input file into a large array held in a CensusData object
    public static CensusData parse(String filename) {
        CensusData result = new CensusData();
        String dataFolderPath = "data/";
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(dataFolderPath + filename));

            // Skip the first line of the file
            // After that each line has 7 comma-separated numbers (see constants above)
            // We want to skip the first 4, the 5th is the population (an int)
            // and the 6th and 7th are latitude and longitude (floats)
            // If the population is 0, then the line has latitude and longitude of +.,-.
            // which cannot be parsed as floats, so that's a special case
            //   (we could fix this, but noisy data is a fact of life, more fun
            //    to process the real data as provided by the government)

            String oneLine = fileIn.readLine(); // skip the first line

            // read each subsequent line and add relevant data to a big array
            while ((oneLine = fileIn.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if(tokens.length != TOKENS_PER_LINE)
                    throw new NumberFormatException();
                int population = Integer.parseInt(tokens[POPULATION_INDEX]);
                if(population != 0)
                    result.add(population,
                               Float.parseFloat(tokens[LATITUDE_INDEX]),
                               Float.parseFloat(tokens[LONGITUDE_INDEX]));
            }

            fileIn.close();
        } catch(IOException ioe) {
            System.err.println("Error opening/reading/writing input or output file.");
            System.exit(1);
        } catch(NumberFormatException nfe) {
            System.err.println(nfe.toString());
            System.err.println("Error in file format");
            System.exit(1);
        }
        return result;
    }

    /**
     * Argument 1: The file containing the input data
     * Argument 2: x, the number of columns in the grid for queries
     * Argument 3: y, the number of rows in the grid for queries
     * Argument 4: The version, -v1, -v2, -v3, -v4, or -v5
     **/
    public static void main(String[] args) {
        // Check if we have exactly 4 arguments:
        if (args.length != 4) {
            System.err.println(
            "Not enough or too many arguments.\n" +
            "Usage:\n" +
            "Argument 1: The file containing the input data\n" +
            "Argument 2: the number of columns in the grid for queries\n" +
            "Argument 3: the number of rows in the grid for queries\n" +
            "Argument 4: The version, -v1, -v2, -v3, -v4, or -v5"
            );
            System.exit(1);
        }

        // Parse arguments
        String fileName = args[0];
        int numColumns = parseDim(args[1], "x");
        int numRows = parseDim(args[2], "y");

        // Parse census data based on the given file
        CensusData censusData = parse(fileName);

        // Use the third argument to execute the correct version
        QueryResponder version = null;
        switch (args[3]) {
            case "-v1":
                version = new SimpleSequential(censusData, numColumns, numRows);
                break;
            case "-v2":
                version = new SimpleParallel(censusData, numColumns, numRows);
                break;
            case "-v3":
                version = new ComplexSequential(censusData, numColumns, numRows);
                break;
            case "-v4":
                version = new ComplexParallel(censusData, numColumns, numRows);
                break;
            case "-v5":
                version = new ComplexLockBased(censusData, numColumns, numRows);
                break;
            default:
                System.err.println("Invalid version");
                System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        // Keep asking for input until program sees any input that is not 4
        // integers on a line
        while (true) {
            System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
            // Assumes there are 4 ints separated by whitespace
            int west, south, east, north;
            try {
                west = scanner.nextInt();
                south = scanner.nextInt();
                east = scanner.nextInt();
                north = scanner.nextInt();
            } catch (Exception ignored) {  // Otherwise, exits
                break;
            }
            int rectanglePopulation = version.getPopulation(west, south, east, north);
            System.out.println("population of the U.S.: " +version.getTotalPopulation());
            float percentPopulation = 100 * (float) rectanglePopulation / (float) version.getTotalPopulation();
            System.out.println("population of rectangle: " + rectanglePopulation);
            System.out.printf("percent of total population: %.2f\n", percentPopulation);
        }

    }

    /**
     * Parses a String with the number of columns or rows into an int
     *
     * @param numDim       The number of columns or rows as a String
     * @param errorMessage The error message to output in case of failed parse
     * @return             An int which is the number of columns or rows
     **/
    private static int parseDim(String numDim, String errorMessage) {
        try {
            return Integer.parseInt(numDim);
        } catch(NumberFormatException e) {
            System.err.println("Invalid " + errorMessage + " value");
            System.exit(1);
            return 0;
        }
    }
}

