package org.phylotastic.mapreducepruner;
//package org.phylotastic.SourcePackages.mapreducepruner;

import org.phylotastic.mrppath.*;
//import org.phylotastic.SourcePackages.mrppath.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author ...
 */
public class MrpPass1 {
    // sara 23-09-2014 (deleted 2 lines, because of removing setEnviron)
    // private static int hashDepth;           // the hashdepth for the filename encoding
    // private static String dataPath;         // the path to the taxon "database"
    private static final Text IDtext = new Text("(=)");
    //
    // sara 23-09-2014 (changed 2 lines, because of removing setEnviron)
    private static Logger loggerP1 = Logger.getLogger(MrpPass1.class.getName());
    private static Logger debugLoggerP1 = Logger.getLogger("debugLogger");
    //
    // sara 23-09-2014 (changed 2 lines, because of removing setEnviron)
    private static FileSystem hadoopFS;     				// hadoop file system
    private static String hfsSeparator = Path.SEPARATOR;	// hadoop file separator => "/"
    
    /**
     *
     * @param _dataPath     the path to the taxon "database"
     * @param _hashDepth    the hashdepth for the filename encoding
     * @param _hadoopFS     the Hadoop FileSystem to use
     */
    public static void setEnviron(String _dataPath, int _hashDepth, 
                                                FileSystem _hadoopFS) {
	// sara 23-09-2014 (deleted all lines)
        // loggerP1        = Logger.getLogger(MrpPass1.class.getName());
        // debugLoggerP1   = Logger.getLogger("debugLogger");
        
        // dataPath        = _dataPath;
        // hashDepth       = _hashDepth;
        
        // hadoopFS        = _hadoopFS;
        // hfsSeparator    = Path.SEPARATOR;
    }

    /** 
     * class: Pass1Map
     * -------------------------------------------------------------------------
     * 
     * a Mapper class, an element of the Hadoop MapReduce framework
     * 
     */
    public static class Pass1Map extends Mapper<LongWritable, Text, Text, Text>
    {
        /**
         * method: map
         * ---------------------------------------------------------------------
         * 
         * Given a single taxon name as argument, this method reads in a file from a specified tree
	 * database. That file should contain one line: a "|"-separated list of nodes that describes
	 * the path, in pre-order indexed integers, from taxon to the root. The name of the file is 
	 * an encoded version of the taxon name, that contains no chracters that are not allowed in 
	 * a (Unix/Windows, etc.) file name.
         * 
         * Example:
         * Taxon        : parkia
         * Encoded      : 001888798bb50357c4ab8bea57ddfe81
         * File name    : /0/0/1/8/8/001888798bb50357c4ab8bea57ddfe81
         * Path         : 628:18|625:1|624:1|623:1|622:1|621:1|581:1|513:1|505:1| ..... |5:1|4:1|3:1|2:1|1:1
         * 
         * Each segment of that path is emitted as an (internal)node ID => taxon pair. 
         * For example, for imaginary tree
         * 
         *         (n1)                           A    B   C   D   E
         *         /  \	                           \  /   /   /   /
         *       (n2)  \                           (n4)  /   /   /
         *       /  \   \                            \  /   /   /
         *     (n3)  \   \                           (n3)  /   /
         *     /  \   \   \                            \  /   /
         *   (n4)  \   \   \                           (n2)  /
         *   /  \   \   \   \                            \  /
         *  A    B   C   D   E                           (n1)
         *
         * if the taxons specified for extraction are A and C and D, Map-1() emits the records:
         * (=)  A:Agoracea
	 * n4   A
         * n3   A
         * n2   A
         * n1   A
	 * (=)  C:Catonacea
         * n3   C
         * n2   C
         * n1   C
	 * (=)  D:Draconacea
         * n2   D
         * n1   D
	 *
         * Representing the tree
         * 
         *  A        C   D
         *   \      /   /
         *   (n4)  /   /
         *     \  /   /
         *     (n3)  /
         *       \  /
         *       (n2)
         *         \
         *         (n1)
	 *
	 * where:
	 * A,C and D are the labels or ID's of the taxons
	 * Agoracea, Catonacea and Draconacea their respective names
         * n1, n2, .., n4 are the internal nodes
	 *
	 * Before being presented to the Reduce-1() method, the emited records are sorted
         * if taxons were A and C and D, this results in the records:
	 * (=)  A:Agoracea
	 * (=)  C:Catonacea
	 * (=)  D:Draconacea
         * n1   A
         * n1   C
         * n1   D
         * n2   A
         * n2   C
         * n2   D
         * n3   A
         * n3   C
         * n4   A
         * 
         * Map is called once for eacht taxon name in the input file
         * The name is encoded to the file location of the path file
         * The path is read in and split up into it's constituent nodes
         * For eacht node a record is written out for the reduce step;
         * for the parkia example 
         *   taxon: parkia
         *   path:  628:18|625:1|624:1|623:1|622:1| ..... |5:1|4:1|3:1|2:1|1:1
         * the output would be:
         *   (=)       628:18:parkia
         *   625:1     628:18
         *   623:1     628:18
         *   ...       ...
         *   ...       ...
         *   3:1       628:18
         *   2:1       628:18
         *   1:1       628:18
         * 
         * @param key1      the offset into the file (not used)
         * @param taxon     the name of the taxon to proces; like: "parkia"
         * @param context   the Hadoop output context for writing the result
         * @throws java.io.IOException
         * @throws java.lang.InterruptedException
         */
        @Override
        public void map(LongWritable key1, Text taxon, Context context) throws IOException, InterruptedException
        {
            // sara 23-09-2014 (new: 4 lines)
            String dataPath = context.getConfiguration().get("my.taxondir");
            int hashDepth = context.getConfiguration().getInt("my.hashdepth", 0);
            loggerP1.info("dataPath is: " + dataPath);
            loggerP1.info("hashDepth is: " + hashDepth);
            String taxonName = tidyTaxonName(taxon.toString());
            loggerP1.info("input is taxon: " + taxonName);
            debugLoggerP1.debug("Map: " + taxonName);
            // decode the name to a file location in the taxon "database"
            String taxonFile = null;
            try {
                taxonFile = determineTaxonFile(taxonName, dataPath, hashDepth);
            } catch (NoSuchAlgorithmException ex) {
                loggerP1.fatal("NoSuchAlgorithmException");
                throw new IOException(ex);
            }
            debugLoggerP1.debug("Map: " + taxonName + "\tFile =\t" + taxonFile);
            // read the taxon path from the file
            String taxonPath = readTaxonPath(taxonFile, FileSystem.get(context.getConfiguration()));	// new (FS.get)
            debugLoggerP1.debug("Map: " + taxonName + "\tPath =\t" + taxonPath);
            // break up the path into a list of nodes
            List<PathNode> taxonNodes = getTaxonNodes(taxonPath);
            
            // the first node in the list is the tip (/leaf/taxon/..)
            PathNode tipNode = taxonNodes.get(0);
            // create a path node: e.g.
            // "628:18" where
            // 628 is the nodelabel
            // 18 is the brach length
            String tipString = tipNode.toString();
            // write the tip "record" e.g.
            // "(=)       628:18:parkia"
            tipNode.setName(taxonName);
            debugLoggerP1.debug("Map: " + taxonName + "\tTip =\t" + tipNode.toString());
            context.write(IDtext, new Text(tipNode.toString()));
            // write a node "record" for each of the internal nodes: e.g.
            //  625:1     628:18
            //  623:1     628:18
            //  ...       ...
            for ( int i = 1; i < taxonNodes.size(); i++ ) {
                String nodeString = taxonNodes.get(i).toString();
                debugLoggerP1.debug("Map: " + taxon + "\tOutput =\t" + nodeString + "\t:\t" + tipString);
                context.write(new Text(nodeString), new Text(tipString)); 
            // notice this inverts the key from taxon to internal node
            }
        }
    }
        
    /** 
     * method: tidyTaxonName
     * -------------------------------------------------------------------------
     *
     * Constructs a tidy version of the taxon name
     * trims whitespace, replaces "_" characters, etc
     * capitalizes first character
     * 
     * @param name  the "dirty" file name
     * @return      the "tidy" file name
     */
    private static String tidyTaxonName(String name) {
        String tidyName = name.trim();
        tidyName = tidyName.replace("_", " ");
        tidyName = StringUtils.capitalize(tidyName);
        return tidyName;
    }

    /** 
     * method: determineTaxonFile
     * -------------------------------------------------------------------------
     *
     * Constructs the location of the file that encodes the tip-to-root path 
     * for the focal tip given the focal tree
     * Earlier the same calculation has taken place in order to store the
     * taxon (path) files in the disk location (taxon "database") they now occupy.
     * 
     * For the parkia example it would encode "Parkia" into the safe
     * string: "001888798bb50357c4ab8bea57ddfe81" that is then formed
     * into the (relative) file path: /0/0/1/8/8/001888798bb50357c4ab8bea57ddfe81
     * 
     * @param taxon     the name of the taxon to determine the file path for
     * @return          the (relative) path to the taxon file
     * @throws java.security.NoSuchAlgorithmException
     */
    // sara 23-09-2014 (new: dataPath, hashDepth)
    private static String determineTaxonFile( String taxon, String dataPath, int hashDepth) 
            throws NoSuchAlgorithmException, IOException {
        //logger.debug("taxon value = " + taxon );
        // make the taxon part of the (file system) path
        // remove all characters that can not be in a file name
        String safeString = makeSafeString(taxon);
        //logger.debug("taxon encoded = " + safeString );
        // determine the path to the taxon file
        // take the dataPath and a number of subdirectories depending on 
        // the hashdepth; both are options found in config
        StringBuilder taxonPath = new StringBuilder(dataPath);
        for ( int i = 0; i <= hashDepth; i++ )
        {
            taxonPath.append(safeString.charAt(i)).append(hfsSeparator);
        }
        taxonPath.append(safeString);
        String taxonFilePath = taxonPath.toString();
        return taxonFilePath;
    } 
    
    /**
     * method: makeSafeString
     * -------------------------------------------------------------------------
     *
     * This is an opaque way of turning an arbitrary string into
     * "safe" strings that can be used as (part of) paths in a file system
     * 
     * @param   unsafeString    the string to be "made safe" for use in a file system
     * @return  safeString      the "safe" string
     * @throws  java.security.NoSuchAlgorithmException
     */
    private static String makeSafeString(String unsafeString) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(unsafeString.getBytes());
        byte byteData[] = md.digest();
        StringBuilder safeString = new StringBuilder();
        for (int i = 0; i < byteData.length; i++)
        {
            safeString.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return safeString.toString();
    }   

    /**
     * method: readTaxonPath
     * -------------------------------------------------------------------------
     * 
     * Reads a taxon's tip-to-root path from it's file in the taxon
     * "database" on the Hadoop file system
     * 
     * For the parkia example it would read the file:
     * ..... /0/0/1/8/8/001888798bb50357c4ab8bea57ddfe81
     * and return the string
     * 628:18|625:1|624:1|623:1|622:1|621:1|581:1|513:1|505:1| ..... |5:1|4:1|3:1|2:1|1:1
     * 
     * @param _filePath the path of the file
     * @return  the line read from the file
     * @throws java.io.IOException
     */
    private static String readTaxonPath(String _filePath, FileSystem hadoopFS) throws IOException	// new (filesystem)
    {
        String line = null;
        Path filePath = new Path(_filePath);
        try{
            FSDataInputStream inputStream = hadoopFS.open(filePath);
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
            line = inputReader.readLine();
        } catch(IOException e){
            throw e;
        }
        return line;
    }

    /**
     * method: getTaxonNodes
     * -------------------------------------------------------------------------
     * 
     * Splits the tip-to-root path as read from file into it's
     * constituent nodes
     * 
     * @param taxonPath the string representing the taxon tip-to-root path
     * @return a List<> of PathNode objects representing the tip-to-root path
     */
    private static List<PathNode> getTaxonNodes(String taxonPath)
    {
        List<PathNode> nodeList = new ArrayList<>();
        String[] parts = taxonPath.split("\\|");
        for (String part : parts) {
            nodeList.add(PathNode.parseNode(part));
        }
        return nodeList;
    }

    /** 
     * class: Pass1Reduce
     * -------------------------------------------------------------------------
     * 
     * a Reducer class, an element of the Hadoop MapReduce framework
     * 
     */
    public static class Pass1Reduce extends Reducer<Text, Text, Text, Text>
    {
        /** 
         * method: reduce
         * ---------------------------------------------------------------------
         * 
         * Given a node ID (as described for "map") as a key, and all the tips that have
         * that node on their respective paths to the root, this method combines these to
         * emit a concatenated list of all tips, then the node ID, then the tip count.
         * 
         * For example, for tree
         * 
         *         (n1)                           A        C   D
         *         /  \	                           \      /   /
         *       (n2)  \                           (n4)  /   /
         *       /  \   \                            \  /   /
         *     (n3)  \   \                           (n3)  /
         *     /  \   \   \                            \  /
         *   (n4)  \   \   \                           (n2)
         *   /  \   \   \   \                            \
         *  A    B   C   D   E                          (n1)
         *
         * if taxons are A and C and D, Reduce-1() receives the records:
         * (=)  {A:Agoracea,C:Catonacea,D:Draconacea}   // {} = iterable list
         * n1   {A,C,D}                                 // {} = iterable list
         * n2   {A,C,D}                                 // {} = iterable list
         * n3   {A,C}                                   // {} = iterable list
         * n4   {A}                                     // {} = iterable list
         *
         * after processing Reduce-1() emits the records:
         * (=)          A:Agoracea
         * (=)          C:Catonacea
         * (=)          D:Draconacea
         * A,C,D        n1,3
         * A,C,D        n2,3
         * A,C          n3,2
         * A            n4,1
         * 
         * @param node      the internal node
         * @param nodeTips  the external nodes with "node" in their path
         * @param context   the Hadoop output context for writing the results
         * @throws java.io.IOException
         * @throws java.lang.InterruptedException
         */
        @Override
        public void reduce(Text node, Iterable<Text> nodeTips, Context context) throws IOException, InterruptedException
        {
            String nodeString = node.toString();
            if (node.equals(IDtext)) {
                // it is a taxon name set like this:
                // "(=)	{A:Agoracea,C:Catonacea,D:Draconacea}
                // where (=) is the node
                // and A:Agoracea, C:Catonacea and D:Draconacea are the nodeTips
                // write out without further processing: e.g.
                // (=)          A:Agoracea
                // (=)          C:Catonacea
                // (=)          D:Draconacea
                for (Text taxonID : nodeTips) {
                    debugLoggerP1.debug("Reduce: input = " + "(=)" + "\t:\t" + taxonID.toString());
                    context.write(node, taxonID);
                    debugLoggerP1.debug("Reduce: output = " + "(=)" + "\t:\t" + taxonID.toString());
                }
            } else {
                // it is a set of taxon nodes like this:
                // "n1 {A,C,D}"
                // where n1 is the internal node
                // and A,C and D are the external nodeTips it opposes
                //
                // write a counted version of taxon node set like this:
                // A,C,D       n1,3
                // read the internal node's data
                PathNode internalNode = PathNode.parseNode(node.toString());
                // read the nodeTips into a sorted list of tipnodes
                // (i.e. into a pathNodeSet)
                PathNodeSet tipSet = new PathNodeSet();
                for (Text nodeTip : nodeTips) {
                    String tipText = nodeTip.toString();
                    tipSet.addNode(PathNode.parseNode(tipText));
                    debugLoggerP1.debug("Reduce: input = " + nodeString + "\t:\t" + tipText);
                }
                // create an internal node with a count
                PathNodeInternal countedInternalNode = new PathNodeInternal(
                        internalNode.getLabel(),internalNode.getLength(),tipSet.getSize());
                debugLoggerP1.debug("Reduce: output = " + nodeString + "\t:\t" + tipSet.toString() + " : " + countedInternalNode.toString());
                // write out the counted node set as one record.
                context.write(new Text(tipSet.toString()), new Text(countedInternalNode.toString()));
            }
        }        
    }
}
