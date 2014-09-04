 /** 
 * -----------------------------------------------------------------------------
 * This proces extracts a subset from an existing tree and removes any superfluous
 * internal nodes. For instance taking the subset A, C, D from the imaginary tree:
 *
 *  A    B   C   D   E
 *   \  /   /   /   /
 *   (n4)  /   /   /
 *     \  /   /   /
 *     (n3)  /   /
 *       \  /   /
 *       (n2)  /
 *         \  /
 *         (n1)
 *
 * step 1 (map-1/reduce-1) results in the tree:
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
 * with superfluous nodes like (n1) and (n4). In two further steps 
 * these nodes are removed, resulting in the tree:
 *
 *  A        C   D
 *   \      /   /
 *    \    /   /	
 *     \  /   /
 *     (n3)  / 
 *       \  /
 *       (n2)
 *
 * Step 2 (map-2/reduce-2) removes any unbranched internal nodes like (n1)
 * Step 3 (map-3/reduce-3) removes any remaining internal nodes that subtend
 * only 1 tip like (n4).
 *
 * The names of the extracted taxons are carried all the way to reduce-3,
 * where they are re-attached to the concerning (external) node, giving:
 *
 *  A:Agoracea
 *   \
 *    \    C:Catonacea
 *     \  /
 *     (n3)  D:Draconacea	
 *       \  /
 *       (n2)
 * 
 * -----------------------------------------------------------------------------
 */	

package org.phylotastic.SourcePackages.mapreducepruner;

import java.io.File;
import java.io.IOException;
import org.phylotastic.SourcePackages.mrpconfig.MrpConfig;
import org.apache.log4j.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;

public class MrpRun {
    // configuration data like paths to files, etc.
    private static Logger logger;
    private static Logger debugger;
    private MrpConfig config;
    private MrpResult mrpResult;
        
    public MrpRun(MrpConfig _config) {
        super();
        config = _config;
        mrpResult = new MrpResult();
        logger = Logger.getLogger(MrpRun.class.getName());
        debugger = Logger.getLogger("debugLogger");
    }
    
    public void run() throws IOException, InterruptedException, ClassNotFoundException {
        // Pass 1        
        Path pass1Input = null;
        Path pass1Output = null;
        try {
            pass1Input = new Path(this.config.inputFile.getPath());
            pass1Output = new Path(this.config.tempDir.getPath() + 
                "pass1" + this.config.pathSeparator.getValue());
            this.Pass1(pass1Input, pass1Output);
        } catch (IOException | ClassNotFoundException | InterruptedException exp) {
            logger.fatal( "MrpRun: Pass 1 exception: ", exp );
            throw exp;
        }
        
        // Pass 2       
        Path pass2Input = pass1Output;
        Path pass2Output = null;
        try {
            pass2Output = new Path(this.config.tempDir.getPath() + 
                "pass2" + this.config.pathSeparator.getValue());
            this.Pass2(pass2Input, pass2Output);
        } catch (IOException | ClassNotFoundException | InterruptedException exp) {
            logger.fatal( "MrpRun: Pass 2 exception: ", exp );
            throw exp;
        }
        
        // Pass 3        
        Path pass3Input = pass2Output;
        String pass3Out = this.config.tempDir.getPath() + 
                "pass3" + this.config.pathSeparator.getValue();
        Path pass3Output = null;
        try {
            pass3Output = new Path(pass3Out);
            this.Pass3(pass3Input, pass3Output);
        } catch (IOException | ClassNotFoundException | InterruptedException exp) {
            logger.fatal( "MrpRun: Pass 3 exception: ", exp );
            throw exp;
        }
        
        // Result
        logger.info("MRP: start processing hadoop output");
        File pass3Result = new File(pass3Out);        
        File newickResult = new File(this.config.tempDir.getPath() + "tree.nwk");
        try {
            mrpResult.process(pass3Result, newickResult);
        } catch (IOException e) {
            throw e;
        }
        logger.info("MRP: mapreduce run finished");
    }
    
    private void Pass1(Path passInput, Path passOutput) throws IOException, InterruptedException, ClassNotFoundException {
        logger.info("Pass 1: configuring Hadoop job");
        MrpPass1.setEnviron(this.config);
        Configuration pass1Config = new Configuration();
        Job jobPass1 = new Job(pass1Config);
        jobPass1.setJarByClass(MrpPass1.class);
        jobPass1.setMapperClass(MrpPass1.Pass1Map.class);
        jobPass1.setReducerClass(MrpPass1.Pass1Reduce.class);
        jobPass1.setInputFormatClass(TextInputFormat.class);
        jobPass1.setOutputFormatClass(TextOutputFormat.class);
        jobPass1.setOutputKeyClass(Text.class);
        jobPass1.setOutputValueClass(Text.class);

        TextInputFormat.setInputPaths(jobPass1, passInput);
        TextOutputFormat.setOutputPath(jobPass1, passOutput);
        logger.info("Pass 1: start of Hadoop job");
        try {
            jobPass1.waitForCompletion(true);
            logger.info("Pass 1: end of Hadoop job");
        } catch ( IOException | InterruptedException | ClassNotFoundException e ) {
            throw e;
        } finally {
        }
    }
    private void Pass2(Path passInput, Path passOutput) throws IOException, InterruptedException, ClassNotFoundException {
        logger.info("Pass 2: configuring Hadoop job");
        MrpPass2.setEnviron(this.config);
        Configuration pass2Config = new Configuration();
        Job jobPass2 = new Job(pass2Config);
        jobPass2.setJarByClass(MrpPass2.class);
        jobPass2.setMapperClass(MrpPass2.Pass2Map.class);
        jobPass2.setReducerClass(MrpPass2.Pass2Reduce.class);
        jobPass2.setInputFormatClass(KeyValueTextInputFormat.class);
        jobPass2.setOutputFormatClass(TextOutputFormat.class);
        jobPass2.setOutputKeyClass(Text.class);
        jobPass2.setOutputValueClass(Text.class);

        TextInputFormat.setInputPaths(jobPass2, passInput);
        TextOutputFormat.setOutputPath(jobPass2, passOutput);
        logger.info("Pass 2: start of Hadoop job");
        try {
            jobPass2.waitForCompletion(true);
            logger.info("Pass 2: end of Hadoop job");
        } catch ( IOException | InterruptedException | ClassNotFoundException e ) {
            throw e;
        } finally {
        }
    }
    
    private void Pass3(Path passInput, Path passOutput) 
            throws IOException, InterruptedException, ClassNotFoundException {
        logger.info("Pass 3: configuring Hadoop job");
        MrpPass3.setEnviron(this.config);
        Configuration pass3Config = new Configuration();
        Job jobPass3 = new Job(pass3Config);
        jobPass3.setJarByClass(MrpPass3.class);
        jobPass3.setMapperClass(MrpPass3.Pass3Map.class);
        jobPass3.setReducerClass(MrpPass3.Pass3Reduce.class);
        jobPass3.setInputFormatClass(KeyValueTextInputFormat.class);
        jobPass3.setOutputFormatClass(TextOutputFormat.class);
        jobPass3.setOutputKeyClass(Text.class);
        jobPass3.setOutputValueClass(Text.class);

        TextInputFormat.setInputPaths(jobPass3, passInput);
        TextOutputFormat.setOutputPath(jobPass3, passOutput);
        logger.info("Pass 3: start of Hadoop job");
        try {
            jobPass3.waitForCompletion(true);
            logger.info("Pass 3: end of Hadoop job");
        } catch ( IOException | InterruptedException | ClassNotFoundException e ) {
            throw e;
        } finally {
        }
    }
}
