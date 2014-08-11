/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mapreducepruner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

public class MrpTrace {
    private final ArrayList<String> mapResults = new ArrayList<>();
    private final ArrayList<String> combineResults = new ArrayList<>();
    private final ArrayList<String> reduceResults = new ArrayList<>();

    // constructor
    // ------------------------------------------------------------------------
    /** Construct a config object
    */
    public MrpTrace() {
    }
    
    // taxonNames
    // ------------------------------------------------------------------------
    public void recordTaxonName(String _taxonName) {
        mapResults.add(_taxonName + "\t0:");
    }
    
    // taxonFiles
    // ------------------------------------------------------------------------
    public void recordTaxonFile(String _taxonName, File _taxonFile) {
        mapResults.add(_taxonName + "\t1:\t" + _taxonFile.toString());
    }
    
    // taxonPaths
    // ------------------------------------------------------------------------
    public void recordTaxonPath(String _taxonName, String _taxonPath) {
        mapResults.add(_taxonName + "\t2:\t" + _taxonPath);
    }
    
    // mapOutput
    // ------------------------------------------------------------------------
    public void recordMapOutput(String _taxon, String _node, String _tip) {
        mapResults.add(_taxon + "\t3:\t" + _node + "\t:\t" + _tip);
    }
    
    public void printMapResults() {
        Collections.sort(mapResults);
        System.out.println();
        System.out.println("Map results");
        System.out.println("------------------------------");
        for (String result : mapResults) {
            System.out.println(result.toString());
        }
        System.out.println("------------------------------");
    }
    
    // combineResults
    // ------------------------------------------------------------------------
    public void recordCombineInput(String _node, String _tip) {
        combineResults.add(_node + "\tin:\t" + _tip);
    }
    
    public void recordCombineOutput(String _node, String _tipSet, String _ancestor) {
        combineResults.add(_node + "\tout:\t" + _tipSet + " : " + _ancestor);
    }
    
    public void printCombineResults() {
        Collections.sort(combineResults);
        System.out.println();
        System.out.println("Combine results");
        System.out.println("------------------------------");
        for (String result : combineResults) {
            System.out.println(result.toString());
        }
        System.out.println("------------------------------");
    }
    
    // reduceResults
    // ------------------------------------------------------------------------
    public void recordReduceInput(String _tips, String _node) {
        reduceResults.add(_tips + "\tin:\t" + _node);
    }
     
    public void recordReduceOutput(String _tips, String _mrca) {
        reduceResults.add(_tips + "\tout:\t" + _mrca);
    }
    
    public void printReduceResults() {
        Collections.sort(reduceResults);
        System.out.println();
        System.out.println("Reduce results");
        System.out.println("------------------------------");
        for (String result : reduceResults) {
            System.out.println(result.toString());
        }
        System.out.println("------------------------------");
    }
    
    // resultMap
    // ------------------------------------------------------------------------
    public void printTipList(Map<Integer, MrpTip> tipList) {
        System.out.println();
        System.out.println("Tip list");
        System.out.println("------------------------------");
        for (int tipLabel : tipList.keySet()) {
            MrpTip tip = tipList.get(tipLabel);
            StringBuilder tipTekst = new StringBuilder(tip.stringLabel + " : " + String.valueOf(tip.tip.getLength()) + " :");
            Collections.sort(tip.ancestors);
            for (InternalTreeNode ancestor : tip.ancestors) {
                tipTekst.append(" => ");
                tipTekst.append(ancestor.toString());
            }
            System.out.println(tipTekst.toString());
        }
        System.out.println("------------------------------");
    }
    
}
