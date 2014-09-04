package org.phylotastic.SourcePackages.mapreducepruner;

import org.phylotastic.SourcePackages.mrppath.*;
import org.phylotastic.SourcePackages.mrptree.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;

import org.apache.log4j.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class MrpResult {
    private static Logger loggerRs;
    private static Logger debuggerRs;
    
    public MrpResult() {
        super();
        loggerRs = Logger.getLogger(MrpResult.class.getName());
        debuggerRs = Logger.getLogger("debugLogger");
    }
    
    /**
     * Process the mapreduce results into a newich tree
     * and write that to a diskfile.
     *
     * @param resultDir
     * @param resultFile
     * @throws IOException
     */
    public void process(File resultDir, File resultFile) throws IOException {
        // The mapreduce result can be spread over one or more files
        // create a tree and add each of the files to it
        // There can be more items in the resultDir than only result files
        Tree tree = new Tree();
        File[] list = getFileList(resultDir, "part-r-0*");
        int count = 0;
        loggerRs.info("Start processing result (part) files");
        for (File file : list) {
            String fileName = file.getName();
            if (file.isDirectory() || 
                    !FilenameUtils.getExtension(fileName).isEmpty()) {
            } else {
                count++;
                loggerRs.info("File " + count + " = " + fileName);
                this.addFile(tree, file);
            }
        }
        // Roots the tree on the node with the lowest label value
        tree.rootTheTree();
        // Create the newick string
        String newickTree = tree.toNewick();
        this.writeResult(newickTree, resultFile);
        loggerRs.info("Done processing result");
        loggerRs.info("Newick Tree written to file: " + resultFile.getCanonicalPath());      
    }
    
    /**
     * Add the taxons in a given result file to the given tree
     *
     * @param tree
     * @param file
     * @throws IOException
     */
    public void addFile(Tree tree, File file) throws IOException {
        // add the taxons in the file to the tree
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            // iterate over lines
            while( line != null ) {
                // split line into tip and ancestorlist
                String[] tuple = line.split("\t");
                PathNode tipNode = PathNode.parseNode(tuple[0]);
                PathNodeSet ancestors = PathNodeSet.parsePathNodeSet(tuple[1]);
                // create node for focal tip
                TreeNode child = tree.addNode(tipNode);
                // iterate over ancestors, sorted from young to old
                //Collections.sort(ancestors.);
                for ( PathNode ancestor : ancestors.getSet() )
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
                        TreeNode parent = tree.addNode(ancestor);
                        tree.setChild(parent, child);
                        // continue processing this (younger) ancestors' possible own (older) ancestor
                        // so this ancestor becomes the current child and the loop continues
                        child = parent;
                    }
                }
                line = reader.readLine();
            }
            reader.close();
        } catch ( IOException e ) {
            throw e;
        }
    }
    
    /**
     * Writes the newick string to a specified disk file
     *
     * @param newick
     * @param resultFile
     * @throws IOException
     */
    public void writeResult(String newick, File resultFile) throws IOException {
        // Create a file name(path) for the newick string
        File resultDir = resultFile.getParentFile();
        try {
            if (!resultDir.exists())
                // folder is not there, make a new one
                resultDir.mkdir();
            FileWriter fw = new FileWriter(resultFile);
            // Write the newick string
            fw.write(newick);
            fw.close();
        } catch (IOException exc) {
            //do stuff with exception
            throw exc;
        }
    }
    
    /**
     * Returns a list of File's in a given directory
     * taking into acount a filename filter
     *
     * @param directory
     * @param filter
     * @return
     */
    public File[] getFileList(File directory, String filter) {
        FileFilter fileFilter = new WildcardFileFilter(filter);
        File[] files = directory.listFiles(fileFilter);
        return files;
    }    
}
