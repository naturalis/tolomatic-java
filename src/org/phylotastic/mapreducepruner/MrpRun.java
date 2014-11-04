package org.phylotastic.mapreducepruner;
 
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.Tool;				// sara 23-09-2014
import org.apache.hadoop.conf.Configured;                       // sara 23-09-2014
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.log4j.Logger;

/**
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpRun extends Configured implements Tool {
    // configuration data like paths to files, etc.
    private static Logger logger;
    private final MrpConfig userConfig;     // MrpConfig object holding the configuration options
    private MrpResult mrpResult;            // MrpResult object to process the result
    private Configuration hadoopConfig;     // hadoop configuration
    private FileSystem hadoopFS;            // hadoop file system
    private String hfsSeparator;            // hadoop file separator => "/"
        
    /**
     *
     * @param _config   the MrpConfig object holding the configuration variables (options)
     */
    public MrpRun(MrpConfig _config) {
        super();
        userConfig    = _config;
        logger        = Logger.getLogger(MrpRun.class.getName());
    }
    
    /**     Method Run
     * -------------------------------------------------------------------------
     *     This proces extracts a subset from an existing tree and removes any superfluous
     *     internal nodes. For instance taking the subset A, C, D from the imaginary tree:
     *
     *      A    B   C   D   E
     *       \  /   /   /   /
     *       (n4)  /   /   /
     *         \  /   /   /
     *         (n3)  /   /
     *           \  /   /
     *           (n2)  /
     *             \  /
     *             (n1)
     *
     *     step 1 (map-1/reduce-1) results in the tree:
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
     *     with superfluous nodes like (n1) and (n4). In two further steps
     *     these nodes are removed, resulting in the tree:
     *
     *      A        C   D
     *       \      /   /
     *        \    /   /
     *         \  /   /
     *         (n3)  /
     *           \  /
     *           (n2)
     *
     *     Step 2 (map-2/reduce-2) removes any unbranched internal nodes like (n1)
     *     Step 3 (map-3/reduce-3) removes any remaining internal nodes that subtend
     *     only 1 tip like (n4).
     *
     *     The names of the extracted taxons are carried all the way to reduce-3,
     *     where they are re-attached to the concerning (external) node, giving:
     *
     *      A:Agoracea
     *       \
     *        \    C:Catonacea
     *         \  /
     *         (n3)  D:Draconacea
     *           \  /
     *           (n2)
     * 
     * -------------------------------------------------------------------------
     * @param args arguments for the run (not used)
     * @return zero when run finished normally
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.lang.ClassNotFoundException
     */
    @Override
    public int run(String[] args) throws IOException, InterruptedException, ClassNotFoundException {	// sara 23-09-2014
        // initialise run
        this.hadoopConfig   = new Configuration();              // a Hadoop configuration
        this.hadoopFS       = FileSystem.get(hadoopConfig);     // a Hadoop filesystem
        this.hfsSeparator   = Path.SEPARATOR;                   // the Hadoop path separator
        // remove the parent outputdirectory (tempDir) and all it's files and subDirs
        String runTemp      = this.userConfig.tempDir.getValue();
        if (!runTemp.endsWith(this.hfsSeparator)) 
            runTemp += this.hfsSeparator;
        Path pathTemp       = new Path(runTemp);                // Hadoop file path
        Boolean result      = hadoopFS.delete(pathTemp, true);
        
        // configure MapReduce job for pass 1
        // ---------------------------------------------------------------------
        logger.info("MrpRun: configuring Hadoop job: Pass 1");
        String path1In = this.userConfig.inputFile.getPath();
        Path path1Input = new Path(path1In);
        // check if input path exists
        if (!hadoopFS.exists(path1Input)) {
            logger.error("Input file not found: " + path1In);
            throw new FileNotFoundException("Input file not found: " + path1In);
        } else {
            // check if path is a file
            if (!hadoopFS.isFile(path1Input)) {
                logger.error("Input file is not a file: " + path1In);
                throw new FileNotFoundException("Input file is not a file: " + path1In);
            }
        }
        
        // check if path to "taxon database" looks ok
        String rootDir = this.userConfig.dataRootDir.getValue();
        if (!rootDir.endsWith(this.hfsSeparator))
            rootDir += this.hfsSeparator;
        String dataDir = this.userConfig.dataDir.getValue();
        String taxonDir = rootDir + dataDir;
        Path taxonPath = new Path(taxonDir);
        // check if data path exists
        if (!hadoopFS.exists(taxonPath)) {
            logger.error("Taxon tree directory not found: " + taxonDir);
            throw new FileNotFoundException("Taxon tree directory not found: " + taxonDir);
        } else {
            // check if path is a folder
            if (!hadoopFS.isDirectory(taxonPath)) {
                logger.error("Taxon tree directory is not a folder: " + taxonDir);
                throw new FileNotFoundException("Taxon tree directory is not a folder: " + taxonDir);
            }
        }
        if (!taxonDir.endsWith(this.hfsSeparator)) 
            taxonDir += this.hfsSeparator;
        this.userConfig.dataPath.setValue(taxonDir);

        hadoopConfig.set("my.taxondir", taxonDir);						// sara 23-09-2014
        hadoopConfig.setInt("my.hashdepth", this.userConfig.hashDepth.getIntValue());		// sara 23-09-2014					// new
        hadoopConfig.setInt("mapreduce.input.fileinputformat.split.maxsize", 2000);		// sara 23-09-2014
        
        String path1Out = runTemp + "pass1" + this.hfsSeparator;
        Path path1Output = new Path(path1Out);
        
        Job jobPass1 = Job.getInstance(hadoopConfig);
        TextInputFormat.setInputPaths(jobPass1, path1Input);
        TextOutputFormat.setOutputPath(jobPass1, path1Output);
        jobPass1.setJarByClass(MrpRun.class);
        jobPass1.setMapperClass(MrpPass1Mapper.class);
        jobPass1.setReducerClass(MrpPass1Reducer.class);
        jobPass1.setInputFormatClass(TextInputFormat.class);
        jobPass1.setOutputFormatClass(TextOutputFormat.class);
        jobPass1.setOutputKeyClass(Text.class);
        jobPass1.setNumReduceTasks(this.userConfig.numTasks.getIntValue());
        
        // configure MapReduce job for pass 2
        // ---------------------------------------------------------------------
        logger.info("MrpRun: configuring Hadoop job: Pass 2");
        String path2Out = runTemp + "pass2" + this.hfsSeparator;
        Path path2Output = new Path(path2Out);
        
        Job jobPass2 = Job.getInstance(hadoopConfig);
        TextInputFormat.setInputPaths(jobPass2, path1Output);
        TextOutputFormat.setOutputPath(jobPass2, path2Output);
        jobPass2.setJarByClass(MrpRun.class);
        jobPass2.setMapperClass(MrpPass2Mapper.class);
        jobPass2.setReducerClass(MrpPass2Reducer.class);
        jobPass2.setInputFormatClass(KeyValueTextInputFormat.class);
        jobPass2.setOutputFormatClass(TextOutputFormat.class);
        jobPass2.setOutputKeyClass(Text.class);
        jobPass2.setOutputValueClass(Text.class);
        jobPass2.setNumReduceTasks(this.userConfig.numTasks.getIntValue());
        
        // configure MapReduce job for pass 3
        // ---------------------------------------------------------------------
        logger.info("MrpRun: configuring Hadoop job: Pass 3");
        String path3Out = runTemp + "pass3" + this.hfsSeparator;
        Path path3Output = new Path(path3Out);
        
        Job jobPass3 = Job.getInstance(hadoopConfig);
        TextInputFormat.setInputPaths(jobPass3, path2Output);
        TextOutputFormat.setOutputPath(jobPass3, path3Output);
        jobPass3.setJarByClass(MrpRun.class);
        jobPass3.setMapperClass(MrpPass3Mapper.class);
        jobPass3.setReducerClass(MrpPass3Reducer.class);
        jobPass3.setInputFormatClass(KeyValueTextInputFormat.class);
        jobPass3.setOutputFormatClass(TextOutputFormat.class);
        jobPass3.setOutputKeyClass(Text.class);
        jobPass3.setOutputValueClass(Text.class);
        jobPass2.setNumReduceTasks(this.userConfig.numTasks.getIntValue());
        
        // configure local job to process the MapReduce result
        // ---------------------------------------------------------------------
        logger.info("MrpRun: configuring Hadoop job: Result");
        mrpResult     = new MrpResult();
        String newickOut = this.userConfig.outputFile.getPath();
        Path newickOutput = new Path(newickOut);
        mrpResult.setEnviron(hadoopFS);
        
        // run MapReduce job for pass 1
        // ---------------------------------------------------------------------
        logger.info("MrpRun: starting Hadoop job pass 1");
        try {
            jobPass1.waitForCompletion(true);
            logger.info("MrpRun: ending Hadoop job pass 1");
        } catch (IOException | ClassNotFoundException | InterruptedException exp) {
            logger.fatal( "MrpRun: Pass 1 exception: ", exp );
            throw exp;
        }
        
        // run MapReduce job for pass 2
        // ---------------------------------------------------------------------
        logger.info("MrpRun: starting Hadoop job pass 2");
        try {
            jobPass2.waitForCompletion(true);
            logger.info("MrpRun: ending Hadoop job pass 2");
        } catch (IOException | ClassNotFoundException | InterruptedException exp) {
            logger.fatal( "MrpRun: Pass 2 exception: ", exp );
            throw exp;
        }
        
        // run MapReduce job for pass 3
        // ---------------------------------------------------------------------
        logger.info("MrpRun: starting Hadoop job pass 3");
        try {
            jobPass3.waitForCompletion(true);
            logger.info("MrpRun: ending Hadoop job pass 3");
        } catch (IOException | ClassNotFoundException | InterruptedException exp) {
            logger.fatal( "MrpRun: Pass 3 exception: ", exp );
            throw exp;
        }
        
        // run job to process the combined MapReduce result
        // ---------------------------------------------------------------------
        logger.info("MRP: start processing mapreduce result");
        try {
            mrpResult.process(path3Output, newickOutput);
        } catch (IOException e) {
            throw e;
        }
        logger.info("MRP: mapreduce run finished");
        return 0;									// new
    }
}
