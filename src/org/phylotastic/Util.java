package org.phylotastic;


import java.io.*;
//import java.io.FileNotFoundException;
//import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
//import jebl.evolution.trees.RootedTree;
//import jebl.evolution.trees.SimpleRootedTree;
import jebl.evolution.trees.SimpleTree;
import jebl.evolution.trees.Tree;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ini4j.*;
import org.ini4j.Ini.Section;

public class Util {
	Ini ini = new Ini();
	Logger logger = Logger.getLogger("org.phylotastic.Util");

    String slash = File.separator;
//    logger.info("separator is " + slash);

	/**
	 * This will expect to get conf/config.ini 
	 */
	
	public Util() {
		new Util(new File(System.getenv("PHYLOTASTIC_MAPREDUCE_CONFIG")));		
	}
	public Util(File iniFile) {
		try {
			Reader reader = new FileReader(iniFile); 
			ini.load(reader);
			logger.setLevel(Level.INFO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    /**
	 * This is an opaque way of turning taxon names into 
	 * "safe" strings that can be turned into paths in 
	 * the file system
	 * @param taxon
	 * @return
	 */
	String encodeTaxon(String taxon) {
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        md.update(taxon.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
        	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        logger.info(sb.toString());
        return sb.toString();
	}
	
	/**
	 * Constructs the location of the file that encodes the tip-to-root path for the 
	 * focal tip given the focal tree
	 * @param treeURL
	 * @param taxon
	 * @return
	 */
	File getTaxonDir(URL treeURL,String taxon) {

        // first need to know the tree URL string
		if ( null == treeURL ) {
			treeURL = getTree();
            System.out.println(treeURL);
		}
		String treeString = treeURL.toString();
		logger.info("tree is "+treeString);

		// construct first parts of the path
		StringBuffer sb = new StringBuffer();
		Section main = ini.get("_");
		Section treeSection = ini.get(treeString);
		String dataroot = main.get("dataroot");
		String datadir = treeSection.get("datadir");
		logger.info("Absolute data root is "+dataroot);
		logger.info("Relative data dir is "+datadir);
		sb.append(dataroot).append(slash).append(datadir).append(slash);

		// hash taxon name
		String encodedTaxon = encodeTaxon(taxon);
		logger.info("Encoded version of '"+taxon+"' is "+encodedTaxon);
		
		// get the depths to which we go when splitting the encodedTaxon
		int hashdepth = Integer.parseInt(main.get("hashdepth"));
		logger.info("Will split hash to "+hashdepth+" levels");
		
		// make the path
		for ( int i = 0; i <= hashdepth; i++ ) {
			sb.append(encodedTaxon.charAt(i)).append(slash);
		}
		logger.info("Path is "+ sb.toString());
        File tempTest = new File(sb.toString());
        logger.info("temptest is "+ tempTest);
		return new File(sb.toString());
	}
	
	/**
	 * Returns the tree URL from the environment
	 * @return
	 */
	URL getTree() {
		URL treeURL = null;
		try {
			treeURL = new URL(System.getenv("PHYLOTASTIC_MAPREDUCE_TREE"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return treeURL;
	}
	
	/**
	 * Returns the value of the tmpdir variable in the ini file
	 * @return
	 */
	Path getOutputPath() {
		Section main = ini.get("_");
        Path tmpdir = new Path(main.get("tmpdir"));
		logger.info("TMP dir is "+tmpdir);
		return tmpdir;
	}

	/**
	 * Reads the tip-to-root path from file
	 * @param taxonFile
	 * @return
	 */
	List<TreeNode> readTaxonFile(File taxonFile) {
		logger.info("taxonFile is " + taxonFile);
        String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(taxonFile));
            logger.info("reader is " + reader);
			line = reader.readLine();
			reader.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	    List<TreeNode> result = new ArrayList<TreeNode>();
        logger.info("result is "+result);
	    String[] parts = line.split("\\|");
        logger.info("Parts = " + Arrays.toString(parts));
	    for ( int i = 0; i < parts.length; i++ ) {
	    	result.add(TreeNode.parseNode(parts[i]));
	    }
        logger.info("result after is "+ result);
	    return result;
	}
	
	Tree readOutFile(File outputFile) {
		SimpleTree tree = new SimpleTree();
		try {
			
			// build list of all the ancestors for all nodes
			BufferedReader reader = new BufferedReader(new FileReader(outputFile));
			String line = reader.readLine();
			Map<Integer,List<InternalTreeNode>> ancListForTip = new HashMap<Integer,List<InternalTreeNode>>();
			Map<Integer,TreeNode> tipForLabel = new HashMap<Integer,TreeNode>();
			
			// iterate over lines
			while( line != null ) {
				logger.info("reading: "+line);
				
				// split line into tipSet and ancestor
				String[] tuple = line.split("\t");
				logger.info("tuple" + Arrays.toString(tuple));
                TreeNodeSet tipSet = TreeNodeSet.parseTreeNodeSet(tuple[0]);
				InternalTreeNode ancestor = InternalTreeNode.parseNode(tuple[1]);
				
				// iterate over tips in focal line, init/extend list of ancestors for each tip
				for ( TreeNode tip : tipSet.getTipSet() ) {
					if ( null == ancListForTip.get(tip.getLabel()) ) {
						logger.info("instantiating new list of ancestors for tip "+tip.getLabel());
						ancListForTip.put(tip.getLabel(),new ArrayList<InternalTreeNode>());
					}
					ancListForTip.get(tip.getLabel()).add(ancestor);
					logger.info("added ancestor "+ancestor.toString()+" to tip "+tip.getLabel());
					
					// cache mapping from label to node object
					if ( tipForLabel.containsKey(tip.getLabel())) {
						logger.info("already stored tip "+tip.getLabel());
					}
					else {
						logger.info("storing tip "+tip.getLabel());
						tipForLabel.put(tip.getLabel(), tip);
					}
				}
				line = reader.readLine();
			}
			reader.close();
			
			// create mapping from our internal nodes to JEBL nodes
			Map<InternalTreeNode,Node> jeblNode = new HashMap<InternalTreeNode,Node>();
			
			// iterate over tips
			for ( Integer tipLabel : ancListForTip.keySet() ) {
				logger.info("processing tip "+tipLabel);
				
				// create JEBL tip for focal tip
				Node jeblChild = tree.createExternalNode(Taxon.getTaxon(""+tipLabel));
				jeblChild.setAttribute("length", tipForLabel.get(tipLabel).getLength());
				
				// fetch sorted list of ancestors, from young to old
				List<InternalTreeNode> ancestorList = ancListForTip.get(tipLabel);
				Collections.sort(ancestorList);
				
				for ( InternalTreeNode ancestor : ancestorList ) {
					Node jeblParent = jeblNode.get(ancestor);
					
					// already seen this node along another path, don't continue farther
					if ( null != jeblParent ) {
						logger.info("already seen ancestor "+ancestor.getLabel());
						tree.addEdge(jeblChild, jeblParent, (Double)jeblChild.getAttribute("length"));
						break;
					}
					
					// instantiate new internal node
					else {
						logger.info("creating new ancestor "+ancestor.getLabel());
						jeblParent = tree.createInternalNode(new ArrayList<Node>());
						jeblNode.put(ancestor, jeblParent);
						jeblParent.setAttribute("length", ancestor.getLength());
						tree.addEdge(jeblChild, jeblParent, (Double)jeblChild.getAttribute("length"));
						jeblChild = jeblParent;
					}
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return tree;
	}
}
