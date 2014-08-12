package org.phylotastic.OtherPackages;

/**
 * Author(s); Carla Stegehuis
 * Contributed to:
 * Date: 12/08/2014
 * Version: 0.1
 */

//The imports
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.InputStreamReader;


/**
 * This script is used for the preparations of the MapRed program. It uses the Nexus file as the
 *   input and returns two files. The first file is a list of all the specienames. This file is necessairy
 *   as input for the MapRed program. The second file is a list of all the specienames, with their corresponding
 *   numbers in the tree. This file is used in the MapRed program, just before the Newick tree is created, to
 *   ensure that the names are used instead of the numbers.
 *
 *   The nexus file is located in the 'data' directory:
 *      "/tolomatic-java/data/File_Phylomatictree.nex"
 *
 *   The file containing the names and corresponding numbers is written to OutputData for further use:
 *      "/tolomatic-java/OutputData/SpecieNumbers.txt"\
 *
 *   The file containing the converted specienames is written to InputData to use as inputfile for the MapRed program:
 *      "tolomatic-java/InputData/InputFile.txt"
 */

public class ReadFile {
    public static int specieNumber;
    public static String specieName;
    public static String sourceFile;
    public static String destFile;
    public static String inputFile;


    /**
     * Main method
     */
    public static void main(String[] args) throws IOException {

        /**
         *
         */
        File dir = new File(".");
        sourceFile = dir.getCanonicalPath() + File.separator + "./data/File_Phylomatictree.nex";
        destFile = dir.getCanonicalPath() + File.separator + "./OutputData/SpecieNumbers.txt";
        inputFile = dir.getCanonicalPath() + File.separator + "./InputData/InputFile2.txt";

        File newFile = new File(sourceFile);
        FileInputStream fis = new FileInputStream(newFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));

        FileWriter fstream = new FileWriter(destFile, true);
        BufferedWriter outNumber = new BufferedWriter(fstream);
        FileWriter inputStream = new FileWriter(inputFile, true);
        BufferedWriter outName = new BufferedWriter(inputStream);

        boolean inTranslate = false;
        boolean inTree = false;
        String line = null;

        /**
         * The while loop goes through evert line. By using inTranslate and inTree, the correct lines are
         * read and used. One file is created with all the specienames for use as input of the MapRed program
         * and one file with the corresponding numbers included, to translate the numbers to names for the
         * Newick tree.
         */
        while ((line = in.readLine()) != null) {

            if (line.startsWith("\tTRANSLATE")) {
                inTranslate = true;
                continue;
            }

            if (line.startsWith("\tTREE")) {
                inTree = true;
                continue;
            }

            if (inTranslate && inTree == false) {
                if (line.startsWith("\t\t")) {
                    line = removeLastChar(line);
                    line = line.trim();
                    outNumber.write(numberReturn(line) + "\t" + nameReturn(line));
                    outNumber.newLine();
                    outName.write(nameReturn(line));
                    outName.newLine();
                }
            }
        }
        // The input and both the output files are closed, to prevent memory issues
        in.close();
        outNumber.close();
        outName.close();
    }


    /**
     * The last character of a String is removed from the string. The 'new' string is returned.
     */
    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }


    /**
     * An string containing a integer is converted to an integer.
     */
    private static int stringToInt(String total){
        return Integer.parseInt(total);
    }


    /**
     * The specieNumber is converted to an int and returned. The conversion is not necessairy,
     * but he..
     */
    private static int numberReturn(String line){
        String[] totalParts = line.split(" ");
        specieNumber = stringToInt(totalParts[0]);
        return specieNumber;
    }


    /**
     * The specieName is given as argument. The first letter is changed to a capital and the underscore
     * is replaced by a space,This character is returned.
     */
    private static String safeName(String name) {
        name = name.replaceAll("_", " ");
        char letter = name.charAt(0);
        char lUpper = Character.toUpperCase(letter);
        return lUpper + name.substring(1, name.length()-0);
    }


    /**
     * The specieName is send to 'safeName'. After it is converted, the name is
     * returned.
     */
    private static String nameReturn(String line){
        String[] totalParts = line.split(" ");
        specieName = safeName(totalParts[1]);
        return specieName;
    }
}


