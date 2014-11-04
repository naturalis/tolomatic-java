/*
 * Example of how to use org.apache.commons.cli to parse
 * command line arguments
 *
 * Possible arguments:
 *  e, environmentvar       boolean option: use environment variables
 *  c, config filename      path to config file
 *  i, input fileName       path to taxon input file
 *  h, hashDepth            hasDepth for encoding taxon names to their
                            corresponding filenames in the "taxon database"
 *  r, dataroot dirname     path to root directory for "taxon database"
 *  u, treeurl url          path url to treedata
 *  d, datadir dirname      name of data directory for actual "taxon database"
 *  t, tempdir dirname      path to temp directory
 *  o, output fileName      path to newick output file
 *
 * Example:
 *  -n -config config.ini -r "c:\Users\Default\Mijn netbeans\Mrp\"
 */

package org.phylotastic.mapreducepruner;

import java.io.*;
import java.net.URL;
import org.apache.commons.cli.*;
import org.apache.log4j.*;
import org.apache.hadoop.conf.Configuration;		// sara 23-09-2014
import org.apache.hadoop.util.ToolRunner;		// sara 23-09-2014

/**
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MapReducePruner {

    /**
     *     default name for the configuration file
     */
    protected static String defaultConfigName = "config.ini";

    /**
     *      default number of tasks to split the mappers/reducers over
     */
    protected static int defaultNumTasks = 10;
    
    /**
     * environment variable that specifies the name of the config file
     */
    protected static String environmentVarConfig = "PHYLOTASTIC_MAPREDUCE_CONFIG";
    
    /**
     * environment variable that specifies the name of the tree url
     */
    protected static String environmentVarTree = "PHYLOTASTIC_MAPREDUCE_TREE";
    
    /**
     * default logger
     */
    protected static Logger logger;

    /**
     * @param args the command line arguments passed through to main()
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {

        // ---------------------------------------------------------------------
        // Initialization
        // ---------------------------------------------------------------------
        // create a logger for reporting purposes
        logger = Logger.getLogger(MapReducePruner.class.getName());
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource("log4j.properties");
        PropertyConfigurator.configure(resource);
        //PropertyConfigurator.configure("log4j.properties");
        
        // proces configuration options from command line and config.ini
        MrpConfig config = new MrpConfig( defaultConfigName,
                                defaultNumTasks,
                                environmentVarConfig,
                                environmentVarTree);
        
        // create a cli Options object
        Options options = new Options();
        // add the various command line options
        config.getOptions(options);
        // create cli command line parser
        CommandLineParser parser = new BasicParser();
        try {
            // parse the command line arguments
            CommandLine cmdLine = parser.parse( options, args );
            // process the command line and config.ini configuration options
            config.setOptions(cmdLine);
        }
        catch (Exception exp) {
            System.out.println( "MrpMain: Command line error: " + exp.getMessage() );
            throw exp;
        }
        
        // configuration options correctly processed
        // execute the mapreduce run
        ToolRunner.run(new Configuration(), new MrpRun(config), args);		// sara 23-09-2014
    }
}
