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
 *  -b -config config.ini -r "c:\user\carla\IdeaProjects\tolomatic-java\"
 */

package org.phylotastic.SourcePackages.mapreducepruner;

import java.io.*;

import org.apache.commons.cli.*;
import org.apache.log4j.*;

import org.phylotastic.SourcePackages.mrpconfig.MrpConfig;

public class MapReducePruner {
    //private static MrpConfig config;
    private static Logger logger;
    private static Logger debugger;
    private static final String defaultConfigName = "Config.ini";
    private static final String environmentVarConfig = "PHYLOTASTIC_MAPREDUCE_CONFIG";
    private static final String environmentVarTree = "PHYLOTASTIC_MAPREDUCE_TREE";
    //

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        
        // ---------------------------------------------------------------------
        //
        // Initialization
        //
        // ---------------------------------------------------------------------
        
        logger = Logger.getLogger(MapReducePruner.class.getName());
        debugger = Logger.getLogger("debugLogger");
        PropertyConfigurator.configure("log4j.properties");
        // process configuration options from command line and config.ini
        MrpConfig config = new MrpConfig( defaultConfigName,
                                environmentVarConfig,
                                environmentVarTree);
        
        // command line options
        // create a cli Options object
        Options options = new Options();
        // add various command line options
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
        
        MrpRun mapReduce = new MrpRun(config);
        mapReduce.run();
    }
}
