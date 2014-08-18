package org.phylotastic.OtherPackages; /**
 * Created by carla on 13-5-14.
 */

import java.io.*;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Scanner;

public class Species {
    static ArrayList<String> lijst = new ArrayList<String>();

    public static void main(String[] args)  throws IOException {
        /**
         * Given a file including the taxon names of the phylo tree, this method changes the names
         * to the official style. The steps taken are;
         * - splitting the names on the "space"
         * - changing the first letter of every taxon to a capital letter
         * - in every taxon name that includes an underscore, this has been changed to a space
         */

        String fileName = "data/Species.txt";
        File file = new File(fileName);
        Scanner inFile = new Scanner(file);
        StringBuffer sb = new StringBuffer();
        while (inFile.hasNext()){
            String i = inFile.next();
            sb.append(i + " ");
        }
        String text = sb.toString();

        String uitTekst[] = text.split(" ");
        for(String i : uitTekst){
            i = i.replaceAll("_", " ");
            char letter = i.charAt(0);
            char lUpper = Character.toUpperCase(letter);
            lijst.add(lUpper + i.substring(1,i.length()-0));
        }

        try {
            PrintStream out = new PrintStream(new FileOutputStream(
                    "data/OutFileSpecies.txt"));
            for (String Soort: lijst){
                out.println(Soort);
            }
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

