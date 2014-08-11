/*
 * Example of how to use org.apache.commons.cli to parse
 * command line arguments
 *
 * Possible arguments:
 *  l, log level            log level: off, trace, debug, info, warn, error, fatal
 *  n, environmentvar       boolean option: don't use environment variables yes/no
 *  c, config filename      path to config file
 *  i, input fileName       path to taxon input file
 *  t, tempdir dirname      path to temp directory
 *  r, dataroot dirname     path to dataroot directory
 *  u, treeurl url          path url to treedata
 *  d, datadir dirname      name of data directory
 *
 * Voorbeeld:
 *  -b -config config.ini -r "c:\Users\Jan\Mijn netbeans\Mrp\"
 */

package mapreducepruner;
/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.Tree;
import jebl.evolution.trees.Utils;
import org.apache.commons.cli.*;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.log4j.Logger;

public class MapReducePruner {
    private static MrpConfig config;
    private static MrpTree treeUtil;
    private static MrpTrace traceUtil;
    private static final String defaultConfigName = "Config.ini";
    private static final String environmentVarConfig = "PHYLOTASTIC_MAPREDUCE_CONFIG";
    private static final String environmentVarTree = "PHYLOTASTIC_MAPREDUCE_TREE";
    //
    private static final Logger logger = Logger.getLogger("mapreducepruner.MapReducePruner");

    // class: Map
    // ------------------------------------------------------------------------
    /**
     * Map class, as element of the MapReducePruner class
     */
    public static class TaxonMap extends Mapper<LongWritable, Text, Text, Text>
    {
        /**
         * Given a single taxon name as argument, this method reads in a file whose name is
         * an encoded version of the taxon name. That file should contain one line: a tab-separate
         * list that describes the path, in pre-order indexed integers, from taxon to the root.
         * Each segment of that path is emitted as node ID => taxon. For example, for tree
         * (((A,B)n3,C)n2,D)n1
         *  A    B
         *   \  /
         *   (n3)  C
         *     \  /
         *     (n2)  D
         *       \  /
         *       (n1)
         *
         * if taxon is A, this emits:
         * n3,A
         * n2,A
         * n1,A
         * 
         * @param key1
         * @param taxon
         * @param context
         * @throws java.io.IOException
         * @throws java.lang.InterruptedException
         */
        @Override
        public void map(LongWritable key1, Text taxon, Context context) throws IOException, InterruptedException
        {
            String taxonName = taxon.toString();
            traceUtil.recordTaxonName(taxonName);
            File taxonFile = null;
            try {
                taxonFile = config.determineTaxonFile(taxonName);
            } catch (NoSuchAlgorithmException ex) {
                logger.fatal(null, ex);
            }
            traceUtil.recordTaxonFile(taxonName, taxonFile);
            String taxonPath = treeUtil.readTaxonPath(taxonFile);
            traceUtil.recordTaxonPath(taxonName, taxonPath);
            List<TreeNode> taxonNodes = treeUtil.getTaxonNodes(taxonPath);
            TreeNode taxonTip = taxonNodes.get(0);
            for ( int i = 1; i < taxonNodes.size(); i++ ) {
                traceUtil.recordMapOutput(taxonName, taxonNodes.get(i).toString(), taxonTip.toString());
                context.write(new Text(taxonNodes.get(i).toString()), new Text(taxonTip.toString())); // notice the key inversion here
            }
        }
    }

    // class: Combine
    // ------------------------------------------------------------------------
    /**
     * Combine class, as element of the MapReducePruner class
     */
    //
    public static class NodesCombine extends Reducer<Text, Text, Text, Text>
    {
        /**
         * Given a node ID (as described for "map") as a key, and all the tips that have
         * that node on their respective paths to the root, this method combines these to
         * emit a concatenated list of all tips, then the node ID, then the tip count. E.g.
         * if the node ID is n1, this will be passed:
         * n1,[A,B]
         * and will emit:
         * A|B,n1,2
         * 
         * @param node
         * @param nodeTips
         * @param context
         * @throws java.io.IOException
         * @throws java.lang.InterruptedException
         */
        
        @Override
        public void reduce(Text node, Iterable<Text> nodeTips, Context context) throws IOException, InterruptedException
        {
            String nodeString = node.toString();
//            traceUtil.recordCombineInput(nodeString, "dummy");
            // this will become a sorted list of tips
            TreeNodeSet tipSet = new TreeNodeSet();
            for (Text nodeTip : nodeTips) {
                String tipText = nodeTip.toString();
                tipSet.addTip(TreeNode.parseNode(tipText));
                traceUtil.recordCombineInput(nodeString, tipText);
            }
            TreeNode n = TreeNode.parseNode(node.toString());
            InternalTreeNode ancestor = new InternalTreeNode(n.getLabel(),n.getLength(),tipSet.getSize());
            traceUtil.recordCombineOutput(nodeString, tipSet.toString(), ancestor.toString());
            context.write(new Text(tipSet.toString()), new Text(ancestor.toString()));
        }
    }

    // class: reduce
    // ------------------------------------------------------------------------
    /**
     * Reduce class, as element of the MapReducePruner class
     */
    //
    public static class NodesReduce extends Reducer<Text, Text, Text, Text>
    {

        /**
         * Given a concatenated list of tips, a node ID and the number of tips it subtends,
         * ( like: A|B,n1,2)
         * this will filter out all nodes that subtend 1 tip. In cases where a concatenated
         * list of tips has multiple values associated with it, this means that there are
         * unbranched internal nodes on the path from those tips to the root. Of those
         * unbranched internals we want the MRCA of the tips, which we obtain by sorting
         * the node IDs: since these are applied in pre-order, the highest node ID in the
         * list is the MRCA.
         * (MRCA = Most recent common ancestor)
         * @param concatTips
         * @param nodes
         * @param context
         * @throws java.io.IOException
         * @throws java.lang.InterruptedException
         */

        @Override
        public void reduce(Text concatTips, Iterable<Text> nodes, Context context) throws IOException, InterruptedException
        {
            String tipText = concatTips.toString();
            double accumulatedBranchLengths = 0;
            int nearestNodeId = 0;
            int tipCount = 0;
            int nodeCount = 0;
            for(Text node : nodes)
            {
                nodeCount++;
                String nodeText = node.toString();
                traceUtil.recordReduceInput(tipText, nodeText);
                InternalTreeNode ancestor = InternalTreeNode.parseNode(nodeText);
                accumulatedBranchLengths += ancestor.getLength();
                int myTipCount = ancestor.getTipCount();

                // not interested in unbranched parents of tips
                if ( myTipCount > 1 ) {

                    // the higher the ID, the more recent
                    if ( ancestor.getLabel() > nearestNodeId ) {
                        nearestNodeId = ancestor.getLabel();
                        tipCount = myTipCount;
                    }
                }
            }
            if ( nearestNodeId > 0 ) {
                InternalTreeNode mrca = new InternalTreeNode(nearestNodeId,accumulatedBranchLengths,tipCount);
                traceUtil.recordReduceOutput(tipText, mrca.toString());
                context.write(concatTips, new Text(mrca.toString()));
            }
            System.out.println("seen "+nodeCount+" nodes for tip key "+concatTips);
        }
    }

    // method: Main
    // ------------------------------------------------------------------------
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, 
                                InterruptedException, ClassNotFoundException {
        
        // proces configuration options from command line and config.ini
        config = new MrpConfig( defaultConfigName,
                                environmentVarConfig,
                                environmentVarTree);
        treeUtil = new MrpTree();
        traceUtil = new MrpTrace();
        
        // comand line options
        // create an Ini4J Options object
        Options options = new Options();
        // add various command line options
        
        // log level option: with level as option argument
        OptionBuilder.withDescription("logging level");
        OptionBuilder.withArgName("level");
        OptionBuilder.hasArg();
        OptionBuilder.withLongOpt("log");
        options.addOption(OptionBuilder.create("l"));
        config.getOptions(options);

        // create command line parser
        CommandLineParser parser = new BasicParser();
        try {
            // parse the command line arguments
            CommandLine cmdLine = parser.parse( options, args );

            // validate that option has been set
            if(cmdLine.hasOption("log")) {
                System.out.println("Debug MrpMain: debug = true");
                config.setLogLevel(cmdLine.getOptionValue("log"));
            } 
            else {
                config.setLogLevel("OFF");
            }
            logger.setLevel(config.getLoggingLevel());
            // process the command line and config.ini configuration options
            config.setOptions(cmdLine);
            File tempDir = config.getTempDir();
            if (tempDir.exists())
                deleteDirectory(tempDir);
        }
        catch( ParseException exp ) {
            System.out.println( "MrpMain: Command line error: " + exp.getMessage() );
        }
        catch( java.io.FileNotFoundException exp ) {
            System.out.println( "MrpMain: File not found error: " + exp.getMessage() );
        }
        catch( java.io.IOException exp ) {
            System.out.println( "MrpMain: IO exception: " + exp.getMessage() );
        }

        //*
        logger.info("MRP: configuring Hadoop job");
        Configuration conf = new Configuration();

        Job job = new Job(conf);
        job.setJarByClass(MapReducePruner.class);
        job.setMapperClass(TaxonMap.class);
        job.setCombinerClass(NodesCombine.class);
        job.setReducerClass(NodesReduce.class);
        job.setNumReduceTasks(1);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        TextInputFormat.setInputPaths(job, new Path(config.getPathInputFile()));
        TextOutputFormat.setOutputPath(job, new Path(config.getPathTempDir()));
        job.setJarByClass(MapReducePruner.class);
        logger.info("MRP: start of Hadoop job");
        try {
            job.waitForCompletion(true);
            logger.info("MRP: end of Hadoop job");
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            //*/
            traceUtil.printMapResults();
            traceUtil.printCombineResults();
            traceUtil.printReduceResults();
            //*
        }
        logger.info("MRP: start processing hadoop output");
        String resultFileName = config.getPathTempDir() + "part-r-00000";
        File resultFile = new File(resultFileName);
        // let op, zit ook een Map<> class in Hadoop!!!
        java.util.Map<Integer, MrpTip> resultMap = treeUtil.readMrpFile(resultFile);
        traceUtil.printTipList(resultMap);
        Tree jebleTree = treeUtil.getJebleTree(resultMap);
        RootedTree rootedJebleTree = Utils.rootTheTree(jebleTree);
        // logger.info("MRP: start creating newick result");
        String newickTree = Utils.toNewick(rootedJebleTree);
        // Tree tree = treeUtil.readOutFile(new File(outFileName));
        // RootedTree rooted = Utils.rootTheTree(tree);
        // String newick = Utils.toNewick(rooted);
        // logger.info("MRP: run completed; result = " + newickTree + ";");
        System.out.println(newickTree + ";");
        //*/
    }    
}
