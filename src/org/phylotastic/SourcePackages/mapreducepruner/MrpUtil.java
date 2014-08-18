/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

package org.phylotastic.SourcePackages.mapreducepruner;

import java.io.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phylotastic.SourcePackages.mrppath.*;
import org.phylotastic.SourcePackages.mrptree.*;

public class MrpUtil {

    // constructor
    // ------------------------------------------------------------------------
    /** Construct a util object
    */
    public MrpUtil() {
    }

    /**
     * Reads the tip-to-root path from file
     * @param taxonFile
     * @return line
     * @throws java.io.IOException
     */
    public String readTaxonPath(File pathFile) throws IOException
    {
        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(pathFile))) {
            line = reader.readLine();
        } catch ( IOException e ) {
            throw e;
        }
        return line;
    }

    /**
     * Reads the tip-to-root path from file
     * @param taxonPath
     * @return nodeList
     */
    public List<PathNode> getTaxonNodes(String taxonPath)
    {
        List<PathNode> nodeList = new ArrayList<>();
        String[] parts = taxonPath.split("\\|");
        for (String part : parts) {
            nodeList.add(PathNode.parseNode(part));
        }
        // System.out.println("nodeList =" + nodeList);
        return nodeList;
    }
    
    /**
     * method makeTipList
     * ----------------------------------------------------------
     * input is a text file with one record per line
     * each record holds one tiplist/ancestor pair 
     * separated by a tab
     * 
     * example:
     * add new example!!
     * 
     * first all records are read in stored into tipList
 a 2-dimensional array (HashMap), like below:
 
          PathTip
 tip      PathNode    ancestors
 ----     -----------------------------------------------
 1000     1000        { 1001:11.0, 1002:11.0 }
 1003     1003        { 1004:11.0 }
 1005     1004        { 1007:11.0, 1006:11.0 }
 1009     1009        { 1014:13.0, 1011:13.0, 1012:13.0 }
 1010     1010        { 1012:13.0, 1011:13.0 }
 
 PathNode and ancestorlist are stored in the helper class PathTip
     * 
     * @param mrpFile
     * @return 
     */
    public Map<Integer, PathTip> makeTipList(File mrpFile) throws Exception{
        Map<Integer, PathTip> tipList = new HashMap<>();
        try {
            // build list of all the ancestors for all nodes
            BufferedReader reader = new BufferedReader(new FileReader(mrpFile));
            String line = reader.readLine();

            // iterate over lines
            while( line != null ) {
                // split line into tipSet and ancestor
                String[] tuple = line.split("\t");
                PathNodeSet tipSet = PathNodeSet.parseTreeNodeSet(tuple[0]);
                PathNodeInternal ancestor = PathNodeInternal.parseNode(tuple[1]);

                // iterate over tips in focal line, init/extend list of ancestors for each tip
                for ( PathNode tip : tipSet.getTipSet() )
                {
                    int tipLabel = tip.getLabel();
                    PathTip tipNode = tipList.get(tipLabel);
                    if (tipNode == null) {
                        tipNode = new PathTip(tip);
                        tipList.put(tipLabel, tipNode);
                    }
                    tipNode.ancestors.add(ancestor);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            throw e;
        }
        return tipList;
    }
    
    /**
     * method solveTipLines
     * ----------------------------------------------------------
     * 
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Een tijdelijke oplossing voor het probleem dat Reduce van Combine tipsets
     * ontvangt in de vorm van meerdere list per set i.p.v. één list per set
     * Deze oplossing combineert de verschillende lists alsnog tot één tipset,
     * alvorens er een correcte tipList voor te maken.
     * De oplossing is niet geschikt voor zéér grote aantallen tipsets en vormt
     * dus alleen een tijdelijke oplossing een Hadoop oplossing gevonden is.
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * 
     * @param mrpFile
     * @return 
     * @throws java.io.IOException 
     */
    public Map<Integer, PathTip> tempSolveTipList(File mrpFile) throws IOException{
        Map<String, PathNodeInternal> inputLines = new HashMap<>();
        try {
            // build list of all the ancestors for all nodes
            BufferedReader reader = new BufferedReader(new FileReader(mrpFile));
            String line = reader.readLine();

            // iterate over lines
            while( line != null ) {
                // split line into tipSet and ancestor
                String[] tuple = line.split("\t");
                String tipSet = tuple[0];
                PathNodeInternal thisAncestor = PathNodeInternal.parseNode(tuple[1]);
                if (!inputLines.containsKey(tipSet)) {
                    inputLines.put(tipSet, thisAncestor);
                }
                else {
                    PathNodeInternal thatAncestor = inputLines.get(tipSet);
                    if (thatAncestor.getLabel() > thisAncestor.getLabel())
                        thatAncestor.setLength(thisAncestor.getLength() + thatAncestor.getLength());
                    else {
                        thisAncestor.setLength(thisAncestor.getLength() + thatAncestor.getLength());
                        inputLines.put(tipSet, thisAncestor);
                    } 
                }
                line = reader.readLine();
            }
            reader.close();
        } catch ( IOException e ) {
            throw e;
        }
        Map<Integer, PathTip> tipList = new HashMap<>();
        for (String key : inputLines.keySet()) 
        {                    
            PathNodeSet tipSet = PathNodeSet.parseTreeNodeSet(key);
            // iterate over tips in focal line, init/extend list of ancestors for each tip
            for ( PathNode tip : tipSet.getTipSet() ) {
                int tipLabel = tip.getLabel();
                PathTip tipNode = tipList.get(tipLabel);
                if (tipNode == null) {
                    tipNode = new PathTip(tip);
                    tipList.put(tipLabel, tipNode);
                }
                tipNode.ancestors.add(inputLines.get(key));
            }
        }
        return tipList;
    }
    
    /**
     * method solveTipLines
     * ----------------------------------------------------------
     * @param mrpFile
     * @return 
     */

    /**
     * method makeTree
 ----------------------------------------------------------
 the second part of the method loops through the given PathTip list
 and converts it's contents into a jebl simpleTree structure,
 that shóuld contain the exact tree that we are after.
     * 
     * @param tipList
     * @return 
     */
    public Tree makeTree(Map<Integer, PathTip> tipList) {
        Tree tree = new Tree();
        
            // create mapping from our pathnodes to treenodes
            // iterate over tips
            for ( Integer tipLabel : tipList.keySet() )
            {
                PathTip tipNode = tipList.get(tipLabel);
                // create node for focal tip
                TreeNode child = tree.addNode(tipNode.tip.getLabel(), "", tipNode.tip.getLength());
                // iterate over ancestors, sorted from young to old
                Collections.sort(tipNode.ancestors);
                for ( PathNodeInternal ancestor : tipNode.ancestors )
                {
                    int ancestorID = ancestor.getLabel();
                    if (tree.hasNode(ancestorID)) {
                        // already seen this node along another path
                        TreeNode parent = tree.getNode(ancestorID);
                        tree.setChild(parent, child);
                        // don't continue farther, processing youngest first, 
                        // so thís parents' ancestors should already have been done.
                        break;
                    }
                    else {
                        // not yet seen this ancestor;
                        // instantiate new ancestor node
                        TreeNode parent = tree.addNode(ancestorID, "", ancestor.getLength());
                        tree.setChild(parent, child);
                        // continue processing this (younger) ancestors' possible own (older) ancestor
                        // so this ancestor becomes the current child and the loop continues
                        child = parent;
                    }
                }
            }
        return tree;
    }
}
