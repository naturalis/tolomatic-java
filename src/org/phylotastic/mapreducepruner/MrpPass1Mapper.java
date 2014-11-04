package org.phylotastic.mapreducepruner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import org.phylotastic.mrppath.PathNode;

/** 
 * class: Pass1Mapper
 * -------------------------------------------------------------------------
 * 
 * a Mapper class, an element of the Hadoop MapReduce framework
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 * 
 */
public class MrpPass1Mapper extends Mapper<LongWritable, Text, Text, Text> 
{
    /**
     * core mapper methods for pass1Mapper
     */
    protected final Core core               = new Core();       // core mapper methods
    
    private final Text IDtext               = new Text("(=)");
    private final String hfsSeparator       = Path.SEPARATOR;   // hadoop file separator => "/"
    private final static Logger logger      = Logger.getLogger(MrpPass1Mapper.class.getName());
    
    private FileSystem hadoopFS;     	  // hadoop file system for the job
    private int hashDepth;                // the hashdepth for the filename encoding
    private String dataPath;              // the path to the taxon "database"
    private Configuration jobConf;        // the hadoop job configuration
    
    
    /**
     *     method: setup
     * 
     *     This method is called once for each mapper task. So if 10 mappers
     *     were spawned for a job, then for each of those mappers it will be
     *     called once
     *     General guideline is to add any task that is required to be done
     *     only once, like getting the path of the distributed cache,
     *     passing and getting parameters to mappers, etc.
     *
     * @param context   a Hadoop context, giving access to data related to the pass1 job
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void setup(Context context) throws IOException, InterruptedException
    {
        super.setup(context);        
        this.jobConf = context.getConfiguration();
        this.dataPath = jobConf.get("my.taxondir");
        this.hashDepth = jobConf.getInt("my.hashdepth", 0);
//        logger.info("Map: dataPath is: " + dataPath);
//        logger.info("Map: hashDepth is: " + hashDepth);
        this.hadoopFS = FileSystem.get(jobConf);
        this.core.setup(this.dataPath, this.hashDepth, this.hadoopFS, this.hfsSeparator);
    }
    
    /**
     *     method: map
     *     ---------------------------------------------------------------------
     * 
     *     Given a single taxon name as argument, this method reads in a file from a specified tree
     *     database. That file should contain one line: a "|"-separated list of nodes that describes
     *     the path, in pre-order indexed integers, from taxon to the root. The name of the file is
     *     an encoded version of the taxon name, that contains no chracters that are not allowed in
     *     a (Unix/Windows, etc.) file name.
     * 
     *     Example:
     *     Taxon        : parkia
     *     Encoded      : 001888798bb50357c4ab8bea57ddfe81
     *     File name    : /0/0/1/8/8/001888798bb50357c4ab8bea57ddfe81
     *     Path         : 628:18|625:1|624:1|623:1|622:1|621:1|581:1|513:1|505:1| ..... |5:1|4:1|3:1|2:1|1:1
     * 
     *     Each segment of that path is emitted as an (internal)node ID => taxon pair.
     *     For example, for imaginary tree
     * 
     *             (n1)                           A    B   C   D   E
     *             /  \                            \  /   /   /   /
     *           (n2)  \                           (n4)  /   /   /
     *           /  \   \                            \  /   /   /
     *         (n3)  \   \                           (n3)  /   /
     *         /  \   \   \                            \  /   /
     *       (n4)  \   \   \                           (n2)  /
     *       /  \   \   \   \                            \  /
     *      A    B   C   D   E                           (n1)
     *
     *     if the taxons specified for extraction are A and C and D, Map-1() emits the records:
     *     (=)  A:Agoracea
     *     n4   A
     *     n3   A
     *     n2   A
     *     n1   A
     *     (=)  C:Catonacea
     *     n3   C
     *     n2   C
     *     n1   C
     *     (=)  D:Draconacea
     *     n2   D
     *     n1   D
     *
     *     Representing the tree
     * 
     *      A        C   D
     *       \      /   /
     *       (n4)  /   /
     *         \  /   /
     *         (n3)  /
     *           \  /
     *           (n2)
     *             \
     *             (n1)
     *
     *     where:
     *     A,C and D are the labels or ID's of the taxons
     *     Agoracea, Catonacea and Draconacea their respective names
     *     n1, n2, .., n4 are the internal nodes
     *
     *     Before being presented to the Reduce-1() method, the emited records are sorted
     *     if taxons were A and C and D, this results in the records:
     *     (=)  A:Agoracea
     *     (=)  C:Catonacea
     *     (=)  D:Draconacea
     *     n1   A
     *     n1   C
     *     n1   D
     *     n2   A
     *     n2   C
     *     n2   D
     *     n3   A
     *     n3   C
     *     n4   A
     * 
     *     Map is called once for eacht taxon name in the input file
     *     The name is encoded to the file location of the path file
     *     The path is read in and split up into it's constituent nodes
     *     For eacht node a record is written out for the reduce step;
     *     for the parkia example
     *       taxon: parkia
     *       path:  628:18|625:1|624:1|623:1|622:1| ..... |5:1|4:1|3:1|2:1|1:1
     *     the output would be:
     *       (=)       628:18:parkia
     *       625:1     628:18
     *       623:1     628:18
     *       ...       ...
     *       ...       ...
     *       3:1       628:18
     *       2:1       628:18
     *       1:1       628:18
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
        String taxonName = this.core.tidyTaxonName(taxon.toString());
        logger.info("input is taxon: " + taxonName);
        logger.info("Map: " + taxonName);
        /* decode the name to a file location in the taxon "database" */
        String taxonFile = null;
        try {
            taxonFile = this.core.determineTaxonFile(taxonName);
        } catch (NoSuchAlgorithmException ex) {
            logger.fatal("NoSuchAlgorithmException");
            throw new IOException(ex);
        }
        logger.info("Map: " + taxonName + "\tFile =\t" + taxonFile);
        /* read the taxon path from the file */
        String taxonPath = this.core.readTaxonPath(taxonFile);
        logger.info("Map: " + taxonName + "\tPath =\t" + taxonPath);
        /* break up the path into a list of nodes */
        List<PathNode> taxonNodes = this.core.getTaxonNodes(taxonPath);

        /* the first node in the list is the tip (/leaf/taxon/..) */
        PathNode tipNode = taxonNodes.get(0);
        /* create a path node: like
         * "628:18" where:
         * 628 is the nodelabel
         * 18 is the brach length   */
        String tipString = tipNode.toString();
        /* write the tip "record" e.g.
         * "(=)       628:18:parkia" */
        tipNode.setName(taxonName);
        logger.info("Map: " + taxonName + "\tTip =\t" + tipNode.toString());
        context.write(IDtext, new Text(tipNode.toString()));
        /* write a node "record" for each of the internal nodes: e.g.
         *  625:1     628:18
         *  623:1     628:18
         *  ...       ...   */
        for ( int i = 1; i < taxonNodes.size(); i++ ) {
            String nodeString = taxonNodes.get(i).toString();
            logger.info("Map: " + taxon + "\tOutput =\t" + nodeString + "\t:\t" + tipString);
            context.write(new Text(nodeString), new Text(tipString)); 
        /* notice this inverts the key from taxon (tip) to internal node */
        }
    }
    
    /**
     *     (static) class: Core
     *     -------------------------------------------------------------------------
     * 
     *     This class contains core methods for the MrpPass1Mapper class
     *     It has been created for the sole purpose of making it possible
     *     to unit test the methods without having to create an instance
     *     of the MrpPass1Mapper class. Something that would require the
     *     use of special unit test packages like MrUnit. At his moment
     *     however MrUnit won't support but version 2.0.0 of Hadoop while
     *     MRP is already based on hadoop 2.5.0.
     * 
     *     Collecting them in a separate inner static class makes testing
     *     them independant of Hadoop, MrUnit, etc. The static indicator
     *     makes it possible to create an instance of the class without
     *     needing an instance of the surrounding Mapper class.
     *     Also the "protected" status supports unit testing
     * 
     * @author ...
     *
     */
    protected static class Core
    {

        /**
         * hadoopFS file separator => "/"
         */
        protected String hfsSeparator;

        /**
         * hadoop FileSystem for the current map/reduce job
         */
        protected FileSystem hadoopFS;

        /**
         * hashdepth for the filename encoding
         */
        protected int hashDepth;

        /**
         * path to the taxon "database"
         */
        protected String dataPath;
        
        /**
         * Constructor
         * 
         * (initialisation moved to method: setup)
         */
        protected Core() {
            super();
        }
        
        /**
         * Method: setup
         * ---------------------------------------------------------------------
         * 
         * @param _dataPath         path to the taxon "database" or "tree"
         * @param _hashDepth        hashdepth for filename encoding
         * @param _hadoopFs         hadoop filesystem
         * @param _hfsSeparator     path separator => "/"
         *
         * @throws IOException
         */
        protected void setup(String _dataPath, 
                int _hashDepth, FileSystem _hadoopFs, 
                String _hfsSeparator) throws IOException {
        this.dataPath = _dataPath;
        this.hashDepth = _hashDepth;
        this.hadoopFS = _hadoopFs;
        this.hfsSeparator = _hfsSeparator;
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
        protected String tidyTaxonName(String name) {
            String tidyName = name.trim();
            tidyName = tidyName.replace("_", " ");
            tidyName = tidyName.toLowerCase();
            tidyName = StringUtils.capitalize(tidyName);
            return tidyName;
        }

        /** 
         *     method: determineTaxonFile
         *     -------------------------------------------------------------------------
         *
         *     Constructs the location of the file that encodes the tip-to-root path
         *     for the focal tip given the focal tree
         *     Earlier the same calculation has taken place in order to store the
         *     taxon (path) files in the disk location (taxon "database") they now occupy.
         * 
         *     For the parkia example it would encode "Parkia" into the safe
         *     string: "001888798bb50357c4ab8bea57ddfe81" that is then formed
         *     into the (relative) file path: /0/0/1/8/8/001888798bb50357c4ab8bea57ddfe81
         *     The number of subdirectories used is determined by the variable HashDepth
         * 
         * @param taxon     the name of the taxon to determine the file path for
         * @return          the (relative) path to the taxon file
         * @throws  java.security.NoSuchAlgorithmException
         * @throws  java.io.IOException
         */
        // sara 23-09-2014 (new: dataPath, hashDepth)
        protected String determineTaxonFile(String taxon) 
                throws NoSuchAlgorithmException, IOException {
//            logger.info("taxon value = " + taxon );
            /* make the taxon part of the (file system) path
             * remove all characters that can not be in a file name */
            String safeString = makeSafeString(taxon);
//            logger.info("taxon encoded = " + safeString );
            /* determine the path to the taxon file
             * take the dataPath and a number of subdirectories depending on 
             * the hashdepth; both are options found in config */
            StringBuilder taxonFilePath = new StringBuilder(this.dataPath);
            for ( int i = 0; i < this.hashDepth; i++ )
            {
                taxonFilePath.append(safeString.charAt(i)).append(this.hfsSeparator);
            }
            taxonFilePath.append(safeString);
            return taxonFilePath.toString();
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
        protected String makeSafeString(String unsafeString) throws NoSuchAlgorithmException
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
        protected String readTaxonPath(String _filePath) throws IOException
        {
            String line = null;
            Path filePath = new Path(_filePath);
            try{
                FSDataInputStream inputStream = this.hadoopFS.open(filePath);
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
         * constituent nodes. So for the parkia:
         * 
         * 628:18|625:1|624:1|623:1|622:1|621:1|581:1|513:1|505:1| ..... |5:1|4:1|3:1|2:1|1:1
         * 
         * it would return the ArrayList&<PathNode&>
         * {628:18, 625:1, 624:1, 623:1, ....., 2:1, 1:1}
         * 
         * 
         * @param taxonPath the string representing the taxon tip-to-root path
         * @return a List&<&> of PathNode objects representing the tip-to-root path
         */
        protected List<PathNode> getTaxonNodes(String taxonPath)
        {
            List<PathNode> nodeList = new ArrayList<>();
            String[] parts = taxonPath.split("\\|");
            for (String part : parts) {
                nodeList.add(PathNode.fromString(part));
            }
            return nodeList;
        }
    }
    
}
