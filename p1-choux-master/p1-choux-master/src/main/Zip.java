package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cse332.jazzlib.ZipEntry;
import cse332.jazzlib.ZipOutputStream;

/*
 * The Zip program requires a working SuffixTrie implementation. If you haven't
 * completed SuffixTrie, you will need to delete/move/rename SuffixTrie.java in
 * order for Eclipse to pick up and use the provided JAR file.
 */

public class Zip {

    /** Larger -> better compression ratio, slower runtime **/
    public static final int BUFFER_LENGTH = 200;

    /** This constant will spit out compression debug output if turned on **/
    public static final boolean DEBUG_OUTPUT = true;

    
    /** Constants that define the input and output **/
    public static final String ZIP_NAME = "test.zip";
    public static final String FILE_TO_COMPRESS = "testBook.txt";

    public static void main(String[] args) throws IOException {
        FileOutputStream fos = new FileOutputStream(ZIP_NAME);
        ZipOutputStream zos = new ZipOutputStream(fos);
        addToZipFile(FILE_TO_COMPRESS, zos);

        zos.close();
        fos.close();
    }

    public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
        System.out.println("Writing '" + fileName + "' to zip file");

        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        int i = 0;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
            System.out.print("Wrote 1024 bytes...");
            i++;
            if (i % 100 == 0) {
                System.out.println();
            }
        }

        zos.closeEntry();
        fis.close();

        System.out.println("Done!");
    }

}
