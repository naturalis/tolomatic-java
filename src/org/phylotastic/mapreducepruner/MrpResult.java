package org.phylotastic.mapreducepruner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.fs.*;
import org.apache.log4j.*;

import org.phylotastic.mrppath.*;
import org.phylotastic.mrptree.*;

/**
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpResult {
    private static Logger logger;
    private FileSystem hadoopFS;                    // hadoop file system
    private String hfsSeparator;                    // hadoop file separator => "/"
    
    /** 
     * constructor
     */
    public MrpResult() {
        super();
        logger = Logger.getLogger(MrpResult.class.getName());
    }
    
    /** method: setEnviron
     *
     * @param _hadoopFS     the Hadoop file system to use
     */
    public void setEnviron(FileSystem _hadoopFS) {
        this.hadoopFS        = _hadoopFS;
        this.hfsSeparator    = Path.SEPARATOR;
    }
    
    /**     method: process
     * 
     *     Process the mapreduce results into a newick tree
     *     and write that to a diskfile.
     *
     * @param inputDir      the Hadoop Path for directory with the result of the MapReduce process
     * @param outputFile    the Hadoop Path for the file where the resulting Newick tree is to be written to
     * @throws IOException
     */
    public void process(Path inputDir, Path outputFile) throws IOException 
    {
        // The mapreduce result can be spread over one or more result files
        // Create an empty tree and add each of the files to it
        // There can be other items in the inputDir than only result files
        Tree tree = new Tree();
        // get a list of the available result files
        // these are all files with no extension, that start with: "part-r-0"
        List<Path> pathList = this.getFilePaths(inputDir, "part-r-0*");
        int count = 0;
        logger.info("Start processing result (part) files");
        for (Path file : pathList) {
            String fileName = file.getName();
            count++;
            logger.info("File " + count + " = " + fileName);
            this.addFile(tree, file);
        }
        // Roots the tree on the node with the lowest label value
        tree.rootTheTree();
        // Create the newick string
        String newickTree = tree.toNewick();
        // Write the Newick string to the file
        this.writeResult(newickTree, outputFile);
        logger.info("Done processing result");
        logger.info("Newick Tree written to file: " + outputFile.toString());      
    }
    
    /** method: addFile
     * Add the taxons in a given MR result file to the given tree
     *
     * @param tree      the mrptree.tree to add the taxons to
     * @param file      the file to add the taxons from (Hadoop)
     * @throws IOException
     */
    public void addFile(Tree tree, Path file) throws IOException {
        // add the taxons in the file to the tree
        try{
            // open the file and read the first line
            FSDataInputStream inputStream = hadoopFS.open(file);
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = inputReader.readLine();
            // iterate over the lines (taxons) in the file
            while( line != null ) {
                // split line into tip and ancestorlist on tab character
                String[] tuple = line.split("\t");
                PathNode tipNode = PathNode.fromString(tuple[0]);
                PathNodeSet ancestors = PathNodeSet.fromString(tuple[1]);
                // create node for focal tip
                TreeNode child = tree.addNode(tipNode);
                // iterate over ancestors (are sorted from young to old)
                for ( PathNode ancestor : ancestors.getSet() )
                {
                    int ancestorID = ancestor.getLabel();
                    // already seen this (ancestor) node along the path of a previous taxon?
                    if (tree.hasNode(ancestorID)) {
                        // yes already seen this node
                        // set this.node as a child of that ancestor
                        TreeNode parent = tree.getNode(ancestorID);
                        tree.setChild(parent, child);
                        // don't continue farther, processing youngest first, 
                        // so thís parents' ancestors should already have been done.
                        break;
                    }
                    else {
                        // not yet seen this ancestor;
                        // instantiate new ancestor node
                        // and add it to the tree
                        TreeNode parent = tree.addNode(ancestor);
                        tree.setChild(parent, child);
                        // continue processing this (younger) ancestors' possible own (older) ancestor
                        // so this ancestor becomes the current child and the loop continues
                        child = parent;
                    }
                }
                line = inputReader.readLine();
            }
            inputReader.close();
            inputStream.close();
        } catch ( IOException e ) {
            throw e;
        }   
    }
    
    /**
     *     Writes the given newick string to a specified disk file
     *
     * @param newick        the Newick string to be written
     * @param outputFile    the file to write the string to
     * @throws IOException
     */
    public void writeResult(String newick, Path outputFile) throws IOException {
        // Create a file name(path) for the newick string
//        Path outputDir = outputFile.getParent();
        try {
//            if (!hadoopFS.exists(outputDir))
//                // folder is not there, make a new one
//                hadoopFS.mkdirs(outputDir);
            OutputStreamWriter stream = new OutputStreamWriter(hadoopFS.create(outputFile,true));
            BufferedWriter writer = new BufferedWriter(stream);
            writer.write(newick);
            writer.close();
            stream.close();
        } catch (IOException exc) {
            //do stuff with exception
            throw exc;
        }
    }
    
    /**
     *     Returns a list of File's in a given directory
     *     taking into account a filename filter
     *
     * @param directory     the directory to list the file from
     * @param filter        the filter to apply
     * @return              the List of Hadoop paths for the files found
     * @throws java.io.IOException
     */
    public List<Path> getFilePaths(Path directory, String filter) throws IOException {
        List<Path> filePaths = new ArrayList<>();
        Path filterPath = new Path(directory.toString()+ this.hfsSeparator + filter);
        FileStatus[] listStatus = hadoopFS.globStatus(filterPath);
        for (FileStatus fStatus : listStatus) {
            if(fStatus.isFile())
                filePaths.add(fStatus.getPath());
        }
        return filePaths;
    }
}
