package org.phylotastic.SourcePackages;
/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.SimpleTree;
import jebl.evolution.trees.Tree;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.ini4j.*;
import org.ini4j.Ini.Section;

/**
 * Util class
 * ----description----
 */
public class Util
{
    //Creates ini object
    Ini ini = new Ini();

    //Creates a logger object for the Util class
    Logger logger = Logger.getLogger("org.org.phylotastic.SourcePackages.Util");

    //determines which separator the system(OS) uses
    String slash = File.separator;

    /**
     * -------------------------------------------------------------------------------------------
     *     Constructor
     *     - No-argument constructor
     *     This will expect to get conf/config.ini from an environmental variable
     * -------------------------------------------------------------------------------------------
     */
    public Util()
    {
//        new Util(new File(System.getenv("PHYLOTASTIC_MAPREDUCE_CONFIG")));
        new Util(new File("/home/carla/IdeaProjects/tolomatic-java/conf/conf.ini"));
    }

   /**
    * -------------------------------------------------------------------------------------------
    *     Constructor
    *     - Argument constructor
    *     This will expect to get conf/config.ini from the commandline variables
    * -------------------------------------------------------------------------------------------
    */
    public Util(File iniFile) {
        try {
            Reader reader = new FileReader(iniFile);
            ini.load(reader);
//			logger.setLevel(Level.INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * -------------------------------------------------------------------------------------------
     * This is an opaque way of turning taxon names into
     * "safe" strings that can be turned into paths in
     * the file system
     * @param taxon
     * @return
     * -------------------------------------------------------------------------------------------
     */
    public String getEncodeTaxon(String taxon)
    {
        logger.info("1");
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.info("2");
        md.update(taxon.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++)
        {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        logger.info("3");
        String sbToString = sb.toString();
        return sbToString;
    }

    /**
     * -------------------------------------------------------------------------------------------
     * Constructs the location of the file that encodes the tip-to-root path for the
     * focal tip given the focal tree
     * @param treeURL
     * @param taxon
     * @return
     * -------------------------------------------------------------------------------------------
     */
    public File getTaxonDir(URL treeURL,String taxon) {

        // first need to know the tree URL string
        logger.info("4");
        if ( null == treeURL )
        {
            treeURL = getTree();
            System.out.println(treeURL);
        }
        String treeString = treeURL.toString();

        // construct first parts of the path
        StringBuffer sb = new StringBuffer();
        Section main = ini.get("_");
        Section treeSection = ini.get(treeString);
        String dataroot = main.get("dataroot");
        String datadir = treeSection.get("datadir");
        sb.append(dataroot).append(slash).append(datadir).append(slash);
        logger.info("5");
        // hash taxon name
        String encodedTaxon = getEncodeTaxon(taxon);

        // get the depths to which we go when splitting the encodedTaxon
        int hashdepth = Integer.parseInt(main.get("hashdepth"));

        // capitalize encodedTaxon
        encodedTaxon = encodedTaxon.substring(0, 1).toUpperCase() + encodedTaxon.substring(1);

        // make the path
        for ( int i = 0; i <= hashdepth; i++ )
        {
            sb.append(encodedTaxon.charAt(i)).append(slash);
        }
        logger.info("6");
        File tempTest = new File(sb.toString());
        return new File(sb.toString());
    }

    /**
     * -------------------------------------------------------------------------------------------
     * Returns the tree URL from the environment
     * @return
     * -------------------------------------------------------------------------------------------
     */
    public URL getTree()
    {
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
     * -------------------------------------------------------------------------------------------
     * Returns the value of the inputDir variable in the ini file
     * @return
     * -------------------------------------------------------------------------------------------
     */
//    Path getInputPath()
//    {
//        Section main = ini.get("_");
//        Path inputDir = new Path(main.get("tmpdir"));
//        return inputDir;
//    }

    /**
     * -------------------------------------------------------------------------------------------
     * Returns the value of the tmpdir variable in the ini file
     * @return
     * -------------------------------------------------------------------------------------------
     */
    public Path getOutputPath()
    {
        Section main = ini.get("_");
        Path tmpdir = new Path(main.get("tmpdir"));
        return tmpdir;
    }

    /**
     * Reads the tip-to-root path from file
     * @param taxonFile
     * @return
     */
    public List<TreeNode> readTaxonFile(File taxonFile)
    {
        String line = null;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(taxonFile));
            line = reader.readLine();
            reader.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        List<TreeNode> result = new ArrayList<TreeNode>();
        String[] parts = line.split("\\|");
        for ( int i = 0; i < parts.length; i++ )
        {
            result.add(TreeNode.parseNode(parts[i]));
        }
        System.out.println("result =" + result);
        return result;
    }

    Tree readOutFile(File outputFile)
    {
        SimpleTree tree = new SimpleTree();
        try
        {
            // build list of all the ancestors for all nodes
            BufferedReader reader = new BufferedReader(new FileReader(outputFile));
            String line = reader.readLine();
            Map<Integer,List<InternalTreeNode>> ancListForTip = new HashMap<Integer,List<InternalTreeNode>>();
            Map<Integer,TreeNode> tipForLabel = new HashMap<Integer,TreeNode>();

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
                jeblChild.setAttribute("length", tipForLabel.get(tipLabel).getLength());

                // fetch sorted list of ancestors, from young to old
                List<InternalTreeNode> ancestorList = ancListForTip.get(tipLabel);
                Collections.sort(ancestorList);

                for ( InternalTreeNode ancestor : ancestorList )
                {
                    Node jeblParent = jeblNode.get(ancestor);

                    // already seen this node along another path, don't continue farther
                    if ( null != jeblParent )
                    {
                        tree.addEdge(jeblChild, jeblParent, (Double)jeblChild.getAttribute("length"));
                        break;
                    }

                    // instantiate new internal node
                    else
                    {
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
