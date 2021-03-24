package tests.gitlab;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusData;
import main.PopulationQuery;


public class QueryResponderTests {
    protected static QueryResponder STUDENT_100_500;
    protected static QueryResponder STUDENT_20_40;

    protected static CensusData readCensusdata() {
        return PopulationQuery.parse("CenPop2010.txt");
    }
}
