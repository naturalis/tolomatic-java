package org.phylotastic.SourcePackages.mrpconfig;

import java.io.*;

import org.apache.commons.cli.*;
import org.apache.commons.cli.Options; // brcause ini4j also has getOptions method
import org.apache.log4j.*;
import org.ini4j.*;

public class MrpConfig {
    /**
     * This class forms the central repository for all the 
     * configuration variables for the MapReducePruner proces.
     * Within that framework it also is responsible for the
     * specification and acceptance of the command line options
     * as well as the config.ini options.
     * 
     * Options specified on the command line take precedence
     * over any values that might be specified in the 
     * environment variables or in the config file. So if both
     * command line and config file specify a value for the
     * input file, like:
     *   cli: -input inputDir\input2.tree
     *   ini: input = inputDir\input1.tree
     * the one from the commend line will be used. So in this
     * case: inputDir\input2.tree
     * 
     * For most command line options two versions are available,
     * with either a long or a short option name. E.g. for the
     * input file both:
     *  -i path-to-input-file 
     * and
     *  -input path-to-input-file
     * are valid options.
     * 
     */
    protected final String defaultNameConfig;                     // default name for config file
    protected final String environmentVarConfig;                  // name environment var for config file
    protected final String environmentVarTree;                    // name environment var for tree url
    
    protected Ini configIni;                                                // ini4j Ini file object
    private static Logger logger;                                           // log4j logger object
    private static Logger debugLogger;
    
    public MrpOption environmentVars = new MrpOption();                     // use environment variables
    public MrpOption pathSeparator = new MrpOption();                       // file system path separator
    public MrpIntOption hashDepth = new MrpIntOption();                     // value of hashDepth option
    public MrpUrlOption treeUrl = new MrpUrlOption();                       // tree URL as a string
    public MrpFileOption configFile = new MrpFileOption(true, false);       // path to config file
    public MrpFileOption inputFile = new MrpFileOption(true, false);        // path to input file
    public MrpFolderOption workDir = new MrpFolderOption(true, false);      // path to work dir
    public MrpFolderOption tempDir = new MrpFolderOption(false, true);      // path to temp dir
    public MrpFolderOption dataRootDir = new MrpFolderOption(false, false);  // path to data root dir
    public MrpArgumentOption dataDir = new MrpArgumentOption();             // name of data dir
    public MrpFolderOption dataPath = new MrpFolderOption(true, false);     // path to temp dir

    // constructor
    // ------------------------------------------------------------------------
    /** Construct a config object
     * 
     * The constructor recieves three parameters:
     * - the default name for the configuration file
     *   (normally something like: config.ini)
     *   to be used in conjunction with the current workdir
     *   when no other configuration file has been specified
     * - the name of the environment variable that holds the
     *   the name of the configuration file to be used.
     *   Only when applicable; i.e. when running on a server
     * - the name of the environment variable that holds the
     *   the URL phylogenetic tree to be used. This URL forms
     *   a section name in the config file and defines what
     *   data folder is to be used.
     *   Only when applicable; i.e. when running on a server
     * 
     * It also creates the MrpOption objects for the
     * following config values: (cli = command line,
     * ini = config file; the curly brackets are not
     * part of the option value;just used to separate
     * option and value)
     * 
     * - Use environment vars
     *   cli: -n
     *   Indicates that no environment variables should
     *   be used. Mostly the case when testing locally
     *   (i.e. not on a server)
     * 
     * - The path to the configuration file
     *   cli: -c {path to config file}
     *        -config {path to config file}
     * 
     * - The hasdepth value to be used for the encoding 
     *   of the taxon name.
     *   cli: -h {integer value}
     *        -hashdepth {integer value}
     *   ini: [Main] hashDepth = {integer value}
     * 
     * - The url for the tree to be used
     *   cli: -u {url}
     *   ini: [Tree] url = {url}
     * 
     * - The path to the taxon input file
     *   cli: -i {path to input file}
     *        -input {path to input file}
     *   ini: [Main] input = {path to input file}
     * 
     * - The path to the directory to be used as the default directory
     *   cli: -w {path to work directory}
     *        -work {path to work directory}
     *   ini: [Main] work = {path to work directory}
     * 
     * - The path to the dataroot directory (for the phylog. tree)
     *   cli: -r {path to root directory}
     *        -root {path to root directory}
     *   ini: [Main] root = {path to root directory}
     * 
     * - The path to the data directory (for the phylog. tree)
     *   cli: -d {path to data directory}
     *        -data {path to root directory}
     *   ini: [{url}] data = {path to data directory}
     * 
     * - The path to the directory to be used as the temp or output directory
     *   cli: -t {path to temp directory}
     *        -temp {path to temp directory}
     *   ini: [Main] temp = {path to temp directory}
     * 
     * @param nameConfig
     * @param envConfig
     * @param envTree
     * @throws java.io.IOException
    */
    public MrpConfig(String nameConfig, String envConfig, String envTree) throws IOException, Exception {
        this.defaultNameConfig = nameConfig;
        this.environmentVarConfig = envConfig;
        this.environmentVarTree = envTree;
        logger = Logger.getLogger(MrpConfig.class.getName());
        debugLogger = Logger.getLogger("debugLogger");
        
        this.pathSeparator.setValue(File.separator);
        this.workDir.setValue(System.getProperty("user.dir"));
        
        this.environmentVars.setProperties("do nót use environment variables", 
                "n", "environment");                                            // use of environment variables
        this.configFile.setProperties("path to config.ini", 
                "c", "config", "file path");                                    // path to config file
        this.hashDepth.setProperties("hashdepth for decoding taxonname", 
                "h", "hashdepth" , "integer", "Main", "hashDepth");             // value of hashDepth option
        this.treeUrl.setProperties("treeURL", 
                "u", "url", "url", "Tree", "url");                              // tree URL as a string
        this.inputFile.setProperties("path to input file", 
                "i", "input", "file path", "Main", "input");                    // path to input file
        this.workDir.setProperties("path to work directory", 
                "w", "workdir", "folder path", "Main", "workDir");              // path to work dir
        this.dataRootDir.setProperties("path to dataroot folder", 
                "r", "root", "folder path", "Main", "root");                    // path to data root dir
        this.dataDir.setProperties("name of data folder", 
                "d", "datadir", "folder name", "Tree", "dataDir");              // path to data dir
        this.dataPath.setProperties("path to data folder", 
                "", "", "folder path");                                         // path to data dir
        this.tempDir.setProperties("path to temp folder", 
                "t", "temp", "folder path", "Main", "tempDir");                 // path to temp dir
    }
    
    // getOptions
    // ------------------------------------------------------------------------
    /** 
    * Adds the options for the various configuration values to the
    * given options object for an ini4j command line parser. 
    * These will be used by that command line parser to interprete
    * the information the user entered on the command line.
    * 
    * @param  options  a ini4j Options object
    */
    public void getOptions(Options options) {
        logger.info("MrpConfig: Setting ini4j command line options");
        // environment variable option: if present then don't use environment 
        // variables:
        options.addOption(this.environmentVars.getOption());
        options.addOption(this.configFile.getOption());
        options.addOption(this.hashDepth.getOption());
        options.addOption(this.inputFile.getOption());
        options.addOption(this.treeUrl.getOption());
        options.addOption(this.dataRootDir.getOption());
        options.addOption(this.dataDir.getOption());
        options.addOption(this.tempDir.getOption());
    }
    
    // setOptions
    // ------------------------------------------------------------------------
    /** 
     * Processes the config file and command line options.
     * First it is determined whether or not environment variables
     * should be used. Based on that is determined which condiguration
     * file to use whereupon the options in that file are checked and read
     * into the various MrpOption objects for the config values.
     * At the same time the command line input is parsed and any option
     * values found there are also read into the MrpOption objects concerned.
     * If for the same option, values are found boh in the config file and
     * on the command line, than the config values are overwritten with those 
     * read from the command line.
     * 
     * @param  cmdLine  a ini4j CommandLine object
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void setOptions(CommandLine cmdLine) throws FileNotFoundException, IOException, Exception {
        boolean environment = true;
        // check the environment variables option
        this.environmentVars.setOption(cmdLine);
        if (this.environmentVars.hasValue()) {
            // do not use environment variable for config file or tree
            environment = false;
            logger.info("MrpConfig: not using environment variables = true");
        }
        logger.info("MrpConfig: Config file: determining cofiguration file");
        // See if the command line specifies a configuration file
        this.configFile.setOption(cmdLine);
        if(!this.configFile.hasValue()) {
            logger.info("MrpConfig: Config file: was not specified in command line options");
            // no config file specified on the command line
            if (true == environment) {
                // see if there is a valid config file specified in the environment variables
                String name = System.getenv(this.environmentVarConfig);
                if (name != null) {
                    logger.info("MrpConfig: Config file: using environment variable: " + this.environmentVarConfig);
                    this.configFile.setValue(name);
                } else {
                    logger.fatal("MrpConfig: Can not obtain environment variable: " + this.environmentVarConfig);
                    throw new IllegalArgumentException("MrpConfig: Environment variable '" +
                            this.environmentVarConfig + "' has not been specified");
                }
            } else {
                // env.variables not to be used; try with the default config file name
                // (setValue will throw exception if none existing file)
                logger.info("MrpConfig: Config file: using default name and location");
                this.configFile.setValue(defaultNameConfig);
            }
        } else {
            // a config file was specified that overrides all others
            logger.info("MrpConfig: Config file: was specified in command line options");
        }
        logger.info("MrpConfig: Config file: using " + this.configFile.getPath());
        // get an ini4j inifile object for the ini file
        this.configIni = new Ini(this.configFile.getFile());
        // read the various options from ini file and command line
        this.hashDepth.setOption(configIni, cmdLine);
        this.inputFile.setOption(configIni, cmdLine);
        this.dataRootDir.setOption(configIni, cmdLine);
        // if no dataroot was entered, use the default dir
        if (!this.dataRootDir.hasValue())
            this.dataRootDir.setValue(this.workDir.getPath());
        this.tempDir.setOption(configIni, cmdLine);
        this.treeUrl.setOption(configIni, cmdLine);
        
        // The value for the datadir in the ini file depends on the
        // url; it's value can not be read as long as the treeURL 
        // option has not been processed.
        // store the command line value; íf any is specified.
        this.dataDir.setOption(cmdLine);
        logger.info("MrpConfig: Tree url: determining url for data folder");
        // check if for the treeUrl a valid value was specified on the command line
        if (!this.treeUrl.hasValue()) {
            logger.info("MrpConfig: Tree url: was not specified in command line options");
            // no url on the command line; see if there is one in the environment vars.
            if (environment == true) {
                // environment vars can be used
                String name = System.getenv(this.environmentVarTree);
                if (name != null) {
                    // yes, found one
                    logger.info("MrpConfig: Tree url: using environment variable: " + this.environmentVarTree);
                    this.treeUrl.setValue(name);
                } else {
                    // ah, pitty
                    logger.fatal("MrpConfig: Can not obtain environment variable: " + this.environmentVarTree);
                    throw new IllegalArgumentException("MrpConfig: Environment variable '" + 
                            this.environmentVarTree + "' has not been specified");
                }
            } else {
                // environment vars are not to be used
                // use the default values from the config.ini file
                logger.info("MrpConfig: Tree url: using default url from config file");
                this.treeUrl.setProperties("Tree", "default");
                this.treeUrl.setOption(configIni);
                // load the default datadir name just in case
                // will be overwritten below if a value is specified
                this.dataDir.setProperties("Tree", "dataDir"); 
            }
        } else {
            // valid treeUrl specified on the commend line
            logger.info("MrpConfig: Tree url: was specified in command line options");
        }
        // set the ini properties for the datadir; the ini section
        // to be used depends on the specified treeUrl
        this.dataDir.setProperties(this.treeUrl.getValue(), "dataDir"); 
        // get the option values from the ini file
        this.dataDir.setOption(configIni);
        // combine dataroot and datadir to 1 complete path
        this.dataPath.setValue(this.dataRootDir.getPath() + this.dataDir.getValue());
        logger.info("MrpConfig: Tree url: using url " + this.treeUrl.getValue());
        logger.info("MrpConfig: Tree url: using data dir " + this.dataPath.getPath());

        logger.info(" ");
        logger.info("MrpConfig: Config object ");
        logger.info("MrpConfig: -----------------------------------");
        logger.info("MrpConfig: Work dir   = " + this.workDir.getPath());
        logger.info("MrpConfig: Input file = " + this.inputFile.getPath());
        logger.info("MrpConfig: Temp path  = " + this.tempDir.getPath());
        logger.info("MrpConfig: Hashdepth  = " + this.hashDepth.getValue());
        logger.info("MrpConfig: Tree url   = " + this.treeUrl.getValue());
        logger.info("MrpConfig: Data root  = " + this.dataRootDir.getPath());
        logger.info("MrpConfig: Data dir   = " + this.dataDir.getValue());
        logger.info("MrpConfig: Data path  = " + this.dataPath.getPath());
        logger.info("MrpConfig: -----------------------------------");
    }
}
