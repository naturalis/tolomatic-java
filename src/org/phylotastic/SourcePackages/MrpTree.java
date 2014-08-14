/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.phylotastic.SourcePackages;

/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

import java.io.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.Tree;
import jebl.evolution.trees.SimpleTree;
//import jebl.evolution.trees.RootedTree;

public class MrpTree {

    // constructor
    // ------------------------------------------------------------------------
    /** Construct a util object
    */
    public MrpTree() {
    }

    /**
     * Reads the tip-to-root path from file
     * @param taxonFile
     * @return line
     * @throws java.io.IOException
     */
    public String readTaxonPath(File taxonFile) throws IOException
    {
        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(taxonFile))) {
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
    public List<TreeNode> getTaxonNodes(String taxonPath)
    {
        List<TreeNode> nodeList = new ArrayList<>();
        String[] parts = taxonPath.split("\\|");
        for (String part : parts) {
            nodeList.add(TreeNode.parseNode(part));
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
     * a 2-dimensional array (HashMap), like below:
     * 
     *          MrpTip
     * tip      TreeNode    ancestors
     * ----     -----------------------------------------------
     * 1000     1000        { 1001:11.0, 1002:11.0 }
     * 1003     1003        { 1004:11.0 }
     * 1005     1004        { 1007:11.0, 1006:11.0 }
     * 1009     1009        { 1014:13.0, 1011:13.0, 1012:13.0 }
     * 1010     1010        { 1012:13.0, 1011:13.0 }
     * 
     * TreeNode and ancestorlist are stored in the helper class MrpTip
     * 
     * @param mrpFile
     * @return 
     */
    public Map<Integer, MrpTip> makeTipList(File mrpFile) throws Exception{
        Map<Integer, MrpTip> tipList = new HashMap<>();
        try {
            // build list of all the ancestors for all nodes
            BufferedReader reader = new BufferedReader(new FileReader(mrpFile));
            String line = reader.readLine();

            // iterate over lines
            while( line != null ) {
                // split line into tipSet and ancestor
                String[] tuple = line.split("\t");
                TreeNodeSet tipSet = TreeNodeSet.parseTreeNodeSet(tuple[0]);
                InternalTreeNode ancestor = InternalTreeNode.parseNode(tuple[1]);

                // iterate over tips in focal line, init/extend list of ancestors for each tip
                for ( TreeNode tip : tipSet.getTipSet() )
                {
                    int tipLabel = tip.getLabel();
                    MrpTip tipNode = tipList.get(tipLabel);
                    if (tipNode == null) {
                        tipNode = new MrpTip(tip);
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
    public Map<Integer, MrpTip> tempSolveTipList(File mrpFile) throws IOException{
        Map<String, InternalTreeNode> inputLines = new HashMap<>();
        try {
            // build list of all the ancestors for all nodes
            BufferedReader reader = new BufferedReader(new FileReader(mrpFile));
            String line = reader.readLine();

            // iterate over lines
            while( line != null ) {
                // split line into tipSet and ancestor
                String[] tuple = line.split("\t");
                String tipSet = tuple[0];
                InternalTreeNode thisAncestor = InternalTreeNode.parseNode(tuple[1]);
                if (!inputLines.containsKey(tipSet)) {
                    inputLines.put(tipSet, thisAncestor);
                }
                else {
                    InternalTreeNode thatAncestor = inputLines.get(tipSet);
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
        Map<Integer, MrpTip> tipList = new HashMap<>();
        for (String key : inputLines.keySet()) 
        {                    
            TreeNodeSet tipSet = TreeNodeSet.parseTreeNodeSet(key);
            // iterate over tips in focal line, init/extend list of ancestors for each tip
            for ( TreeNode tip : tipSet.getTipSet() ) {
                int tipLabel = tip.getLabel();
                MrpTip tipNode = tipList.get(tipLabel);
                if (tipNode == null) {
                    tipNode = new MrpTip(tip);
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
     * method getJebleTree
     * ----------------------------------------------------------
     * the second part of the method loops through the given MrpTip list
     * and converts it's contents into a jebl simpleTree structure,
     * that shóuld contain the exact tree that we are after.
     * 
     * @param tipList
     * @return 
     */
    public Tree makeJebleTree(Map<Integer, MrpTip> tipList) {
        SimpleTree jebleTree = new SimpleTree();
        
            // create mapping from our internal nodes to JEBL nodes
            Map<InternalTreeNode,Node> processedAncestorNodes = new HashMap<>();
            Double childLength = 0.0;
            Double parentLength = 0.0;    
            // iterate over tips
            for ( Integer tipLabel : tipList.keySet() )
            {
                MrpTip tipNode = tipList.get(tipLabel);
                // create JEBL tip for focal tip
                Taxon tipTaxon = Taxon.getTaxon(tipNode.stringLabel);
                Node jeblChild = jebleTree.createExternalNode(tipTaxon);
                childLength = tipNode.tip.getLength();

                // fetch sorted list of ancestors, from young to old
                Collections.sort(tipNode.ancestors);
                for ( InternalTreeNode ancestor : tipNode.ancestors )
                {
                    Node jeblParent = processedAncestorNodes.get(ancestor);
                    if ( jeblParent != null ) {
                        // already seen this node along another path, don't continue farther
                        // processing youngest first, so thís parents' ancestors 
                        // should already have been done.
                        jebleTree.addEdge(jeblParent, jeblChild, childLength);
                        break;
                    }
                    else {
                        // not yet seen;
                        // instantiate new internal (ancestor) node
                        jeblParent = jebleTree.createInternalNode(new ArrayList<Node>());
                        parentLength = ancestor.getLength();
                        processedAncestorNodes.put(ancestor, jeblParent);
                        jebleTree.addEdge(jeblParent, jeblChild, childLength);
                        // continue processing this (younger) ancestors' possible own (older) ancestor
                        // so this ancestor becomes the current child and the loop continues
                        jeblChild = jeblParent;
                        childLength = parentLength;
                    }
                }
            }
        return jebleTree;
    }
}
