/**
 * Created by carla on 13-5-14.
 */

import java.io.*;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Scanner;

public class Soorten {
    static ArrayList<String> lijst = new ArrayList<String>();

    public static void main(String[] args)  throws IOException {
        String fileName = "data/Soorten.txt";
        File file = new File(fileName);
        Scanner inFile = new Scanner(file);
        StringBuffer sb = new StringBuffer();
        while (inFile.hasNext()){
            String i = inFile.next();
            sb.append(i + " ");
        }
        String tekst = sb.toString();

        String uitTekst[] = tekst.split(" ");
        for(String i : uitTekst){
            i = i.replaceAll("_", " ");
            char letter = i.charAt(0);
            char lUpper = Character.toUpperCase(letter);
            lijst.add(lUpper + i.substring(1,i.length()-0));
        }

        try {
            PrintStream out = new PrintStream(new FileOutputStream(
                    "data/OutFileSoorten.txt"));
            for (String Soort: lijst){
                out.println(Soort);
            }
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

