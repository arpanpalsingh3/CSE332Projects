package experiments;

import cse332.jazzlib.ZipOutputStream;
import main.Zip;

import java.io.FileOutputStream;
import java.io.IOException;

public class Time {

    public static void main(String[] args) throws IOException {
        Zip test = new Zip();
        FileOutputStream fos = new FileOutputStream(test.ZIP_NAME);
        ZipOutputStream zos = new ZipOutputStream(fos);
        double totalTime = 0;
        int NUM_WARM = 3;
        int NUM_TESTS = 6 ;
        for(int j = 0; j < NUM_TESTS; j++) {
            long startTime = System.currentTimeMillis();
            test.addToZipFile(test.FILE_TO_COMPRESS, zos);
            long endTime = System.currentTimeMillis();
            if(NUM_WARM <= j) {
                totalTime += (endTime - startTime);
            }
            double average = totalTime / (NUM_TESTS / NUM_WARM);
            System.out.println(average);
        }
    }
}
