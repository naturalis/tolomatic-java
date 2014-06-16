package org.phylotastic.SourcePackages;

import java.io.*;
import java.util.*;

import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.Tree;
import jebl.evolution.trees.Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.log4j.Logger;

//import javax.naming.Context;
//import org.w3c.dom.*;
//import jebl.evolution.trees.SimpleTree;
//import jebl.evolution.trees.RootedFromUnrooted;
//import org.apache.hadoop.mapred.*;
//import jebl.evolution.io.NewickExporter;

public class MapReducePruner {
	static Util util;
	static Logger logger = Logger.getLogger("org.phylotastic.SourcePackages.MapReducePruner");

    static String slash = File.separator;
//    logger.info("separator is " + slash);
	
	public static class Map extends Mapper<LongWritable, Text, Text, Text>
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
		 */
		@Override
		public void map(LongWritable key1, Text taxon, Context context) throws IOException, InterruptedException
        {

//            File outputMap = new File("/home/carla/IdeaProjects/tolomatic-java/MapOutput.txt");
//            outputMap.createNewFile();
//            FileWriter writer = new FileWriter(outputMap);

            File taxonDir = util.getTaxonDir(null, taxon.toString());
//            logger.info("taxondir is " + taxonDir);
			String taxonCode = util.encodeTaxon(taxon.toString());
//            logger.info("taxoncode is " + taxonCode);
            StringBuffer taxonPath = new StringBuffer(taxonDir.getPath()).append(slash).append(taxonCode);
//            logger.info("taxonBuffer is " + taxonPath);

            File taxonFile = new File(taxonPath.toString());
//            logger.info("taxonFile is " +taxonFile);
			StringBuffer sb = new StringBuffer(taxonDir.getAbsolutePath());
//            logger.info("sb is " + sb);
            List<TreeNode> nodes = util.readTaxonFile(taxonFile);
            TreeNode tip = nodes.get(0);
			for ( int i = 1; i < nodes.size(); i++ )
            {
				context.write(new Text(nodes.get(i).toString()), new Text(tip.toString())); // notice the key inversion here
            }

    	}
	}
	
//	@SuppressWarnings("deprecation")
	public static class Combine extends Reducer<Text, Text, Text, Text>
    {
		
		/**
		 * Given a node ID (as described for "map") as a key, and all the tips that have
		 * that node on their respective paths to the root, this method combines these to
		 * emit a concatenated list of all tips, then the node ID, then the tip count. E.g.
		 * if the node ID is n1, this will be passed:
		 * n1,[A,B]
		 * and will emit:
		 * A|B,n1,2
		 */
//		@Override
		public void reduce(Text node, Iterator<Text> tips, Context context) throws IOException, InterruptedException
        {
			
			// this will become a sorted list of tips
			TreeNodeSet tipSet = new TreeNodeSet();
			while (tips.hasNext()) {	
				tipSet.addTip(TreeNode.parseNode(tips.next().toString()));
		    }			
			
			TreeNode n = TreeNode.parseNode(node.toString());
			InternalTreeNode ancestor = new InternalTreeNode(n.getLabel(),n.getLength(),tipSet.getSize());			
		    context.write(new Text(tipSet.toString()), new Text(ancestor.toString()));
		}

	}
	
//	@SuppressWarnings("deprecation")
	public static class Reduce extends Reducer<Text, Text, Text, Text>
    {

		/**
		 * Given a concatenated list of tips, a node ID and the number of tips it subtends,
		 * this will filter out all nodes that subtend 1 tip. In cases where a concatenated
		 * list of tips has multiple values associated with it, this means that there are
		 * unbranched internal nodes on the path from those tips to the root. Of those
		 * unbranched internals we want the MRCA of the tips, which we obtain by sorting
		 * the node IDs: since these are applied in pre-order, the highest node ID in the
		 * list is the MRCA.
		 */
//		@Override
		public void reduce(Text concatTips, Iterator<Text> nodes, Context context) throws IOException, InterruptedException
        {
			double accumulatedBranchLengths = 0;
			int nearestNodeId = 0;
			int tipCount = 0;
			while( nodes.hasNext() )
            {
				InternalTreeNode ancestor = InternalTreeNode.parseNode(nodes.next());
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
				context.write(concatTips, new Text(mrca.toString()));
			}
		}
	}
	
	/**
	 * Usage: MapReducePruner <infile> <outfile> <config> (>>conf, in, out)
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception
    {
        util = new Util(new File(args[0]));
        File confFile = new File(args[0]);
        File outputFile = new File(args[1]);
        File inputFile = new File(args[2]);

        logger.info("config file location is "+ new File(args[0]));
        logger.info("var conf " + confFile);
        logger.info("output dir is "+new File (args[1]));
        logger.info("input dir "+new File (args[2]));

        //*
        Configuration conf = new Configuration();

        Job job = new Job(conf);
        job.setJobName("mapreduceprune");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapperClass(MapReducePruner.Map.class);
        job.setCombinerClass(MapReducePruner.Combine.class);
        job.setReducerClass(MapReducePruner.Reduce.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        TextInputFormat.setInputPaths(job, new Path(args[1]));
        logger.info("input dir is "+new Path(args[1]));
        TextOutputFormat.setOutputPath(job, util.getOutputPath());
        logger.info("output path is "+util.getOutputPath());
        job.setJarByClass(MapReducePruner.class);
        job.waitForCompletion(true);
        //*/

        //*
        String outFile = util.getOutputPath().toString() + "/part-r-00000";
        Tree tree = util.readOutFile(new File(outFile));
        RootedTree rooted = Utils.rootTheTree(tree);
//            logger.info("rooted: " + rooted);
//            logger.info("rooted tree: " + Utils.rootTheTree(tree));
        String newick = Utils.toNewick(rooted);

//            logger.info("newick" + newick);
        System.out.println(newick+";");
        //*/

//            logger.info("outFile: " + outFile);
//            logger.info("tree: " + tree);
    }
}
