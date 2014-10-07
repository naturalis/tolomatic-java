/*
 * Example of how to use org.apache.commons.cli to parse
 * command line arguments
 *
 * Possible arguments:
 *  n, environmentvar       boolean option: don't use environment variables yes/no
 *  c, config filename      path to config file
 *  i, input fileName       path to taxon input file
 *  t, tempdir dirname      path to temp directory
 *  o, output fileName      path to newick output file
 *  r, dataroot dirname     path to dataroot directory
 *  u, treeurl url          path url to treedata
 *  d, datadir dirname      name of data directory
 *
 * Voorbeeld:
 *  -n -config config.ini -r "c:\Users\Default\Mijn netbeans\Mrp\"
 */

package org.phylotastic.mapreducepruner;
//package org.phylotastic.SourcePackages.mapreducepruner;

import java.io.*;
import java.net.URL;
import org.apache.commons.cli.*;
import org.apache.log4j.*;
import org.apache.hadoop.conf.Configuration;		// sara 23-09-2014
import org.apache.hadoop.util.ToolRunner;		// sara 23-09-2014

/**
 *
 * @author ...
 */
public class MapReducePruner {
    //private static MrpConfig config;
    private static Logger logger;
    private static Logger debugger;
    // default name for the configuration file
    private static final String defaultConfigName = "config.ini";
    // environment variable that specifies the name of the config file
    private static final String environmentVarConfig = "PHYLOTASTIC_MAPREDUCE_CONFIG";
    // environment variable that specifies the name of the tree url
    private static final String environmentVarTree = "PHYLOTASTIC_MAPREDUCE_TREE";

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
        debugger = Logger.getLogger("debugLogger");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource("log4j.properties");
        PropertyConfigurator.configure(resource);
        //PropertyConfigurator.configure("log4j.properties");
        
        // proces configuration options from command line and config.ini
        MrpConfig config = new MrpConfig( defaultConfigName,
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
        // MrpRun mapReduce = new MrpRun(config);
        // mapReduce.run();
        // sara 23-09-2014 (replaced 2 lines)
        ToolRunner.run(new Configuration(), new MrpRun(config), args);		// sara 23-09-2014
    }
}
