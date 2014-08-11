/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mapreducepruner;
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

    Tree readOutFile(File outputFile)
    /**
     * input is a text file with one record per line
     * each record holds one tip/ancestor pair,
     * in the form of two nodes separated by a tab
     * 
     * example:
     * 1000:1.0     1001:11.0
     * 1000:1.0     1002:11.0
     * 1003:1.0     1004:11.0
     * 1005:1.0     1007:11.0
     * 1005:1.0     1006:11.0
     * 1009:1.0     1014:13.0
     * 1009:1.0     1011:13.0
     * 1009:1.0     1012:13.0
     * 1010:1.0     1012:13.0
     * 1010:1.0     1011:13.0
     * 
     * first all records are read in stored into "ancListForTip"
     * a 2-dimensional array (HashMap), like below:
     * 
     * tip      ancestors
     * ----     -----------------------------------
     * 1000     { 1001:11.0, 1002:11.0 }
     * 1003     { 1004:11.0 }
     * 1005     { 1007:11.0, 1006:11.0 }
     * 1009     { 1014:13.0, 1011:13.0, 1012:13.0 }
     * 1010     { 1012:13.0, 1011:13.0 }
     * 
     * a helper array (HashMap) is also created, that holds the
     * tip nodes keyed on label (string) for retreival purposes
     * 
     * the second part of the method loops through "ancListForTip"
     * and converts it's contents into a jebl simpleTree structure,
     * that shóuld contain the exact tree that we are after.
     * 
     */
    {
        SimpleTree tree = new SimpleTree();
        Map<Integer,List<InternalTreeNode>> ancListForTip = new HashMap<Integer,List<InternalTreeNode>>();
        Map<Integer,TreeNode> tipForLabel = new HashMap<Integer,TreeNode>();
        try
        {
            // build list of all the ancestors for all nodes
            BufferedReader reader = new BufferedReader(new FileReader(outputFile));
            String line = reader.readLine();

            // iterate over lines
            while( line != null )
            {
                // split line into tipSet and ancestor
                String[] tuple = line.split("\t");
                TreeNodeSet tipSet = TreeNodeSet.parseTreeNodeSet(tuple[0]);
                InternalTreeNode ancestor = InternalTreeNode.parseNode(tuple[1]);

                // iterate over tips in focal line, init/extend list of ancestors for each tip
                for ( TreeNode tip : tipSet.getTipSet() )
                {
                    if ( null == ancListForTip.get(tip.getLabel()) )
                    {
                        ancListForTip.put(tip.getLabel(),new ArrayList<InternalTreeNode>());
                    }
                    ancListForTip.get(tip.getLabel()).add(ancestor);

                    // cache mapping from label to node object
                    if ( tipForLabel.containsKey(tip.getLabel()))
                    {
//						logger.info("already stored tip "+tip.getLabel());
                    }
                    else
                    {
                        tipForLabel.put(tip.getLabel(), tip);
                    }
                }
                line = reader.readLine();
            }
            reader.close();

            // create mapping from our internal nodes to JEBL nodes
            Map<InternalTreeNode,Node> jeblNode = new HashMap<InternalTreeNode,Node>();

            // iterate over tips
            for ( Integer tipLabel : ancListForTip.keySet() )
            {
                // create JEBL tip for focal tip
                Node jeblChild = tree.createExternalNode(Taxon.getTaxon(""+tipLabel));
                jeblChild.setAttribute("length"
                        + "", tipForLabel.get(tipLabel).getLength());

                // fetch sorted list of ancestors, from young to old
                List<InternalTreeNode> ancestorList = ancListForTip.get(tipLabel);
                Collections.sort(ancestorList);

                for ( InternalTreeNode ancestor : ancestorList )
                {
                    Node jeblParent = jeblNode.get(ancestor);

                    // already seen this node along another path, don't continue farther
                    // processing youngest first, so thís parents' ancestors 
                    // should already have been done.
                    if ( null != jeblParent )
                    {
                        tree.addEdge(jeblChild, jeblParent, (Double)jeblChild.getAttribute("length"));
                        break;
                    }

                    // instantiate new internal (ancestor) node
                    else
                    {
                        jeblParent = tree.createInternalNode(new ArrayList<Node>());
                        jeblNode.put(ancestor, jeblParent);
                        jeblParent.setAttribute("length", ancestor.getLength());
                        tree.addEdge(jeblChild, jeblParent, (Double)jeblChild.getAttribute("length"));
                        // continue processing this (younger) ancestors' possible own (older) ancestor
                        // so this ancestor becomes the current child and the loop continues
                        jeblChild = jeblParent;
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return tree;
    }
    
    public Map<Integer, MrpTip> readMrpFile(File mrpFile){
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
        }
        return tipList;
    }

    public Tree getJebleTree(Map<Integer, MrpTip> tipList) {
        SimpleTree jebleTree = new SimpleTree();
        
            // create mapping from our internal nodes to JEBL nodes
            Map<InternalTreeNode,Node> processedAncestorNodes = new HashMap<>();
    
            // iterate over tips
            for ( Integer tipLabel : tipList.keySet() )
            {
                MrpTip tipNode = tipList.get(tipLabel);
                // create JEBL tip for focal tip
                Taxon tipTaxon = Taxon.getTaxon(tipNode.stringLabel);
                Node jeblTip = jebleTree.createExternalNode(tipTaxon);
                jeblTip.setAttribute("length", tipNode.tip.getLength());

                // fetch sorted list of ancestors, from young to old
                Collections.sort(tipNode.ancestors);
                for ( InternalTreeNode ancestor : tipNode.ancestors )
                {
                    Node jeblParent = processedAncestorNodes.get(ancestor);
                    if ( jeblParent != null ) {
                        // already seen this node along another path, don't continue farther
                        // processing youngest first, so thís parents' ancestors 
                        // should already have been done.
                        jebleTree.addEdge(jeblTip, jeblParent, (Double)jeblTip.getAttribute("length"));
                        break;
                    }
                    else {
                        // not yet seen;
                        // instantiate new internal (ancestor) node
                        jeblParent = jebleTree.createInternalNode(new ArrayList<Node>());
                        jeblParent.setAttribute("length", ancestor.getLength());
                        processedAncestorNodes.put(ancestor, jeblParent);
                        jebleTree.addEdge(jeblTip, jeblParent, (Double)jeblTip.getAttribute("length"));
                        // continue processing this (younger) ancestors' possible own (older) ancestor
                        // so this ancestor becomes the current child and the loop continues
                        jeblTip = jeblParent;
                    }
                }
            }
        return jebleTree;
    }
}
