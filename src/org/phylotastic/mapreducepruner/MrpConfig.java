package org.phylotastic.mapreducepruner;

import java.io.*;

import org.apache.commons.cli.*;
import org.apache.commons.cli.Options; // because ini4j also has getOptions method
import org.apache.log4j.*;
import org.ini4j.*;

import org.phylotastic.mrpoption.*;

/**
 *     Class MrpConfig
 *  
 *     This class forms the central repository for all the
 *     configuration variables for the MapReducePruner proces.
 *     Within that framework it also is responsible for the
 *     specification and acceptance of the command line options
 *     as well as the config.ini options.
 * 
 *     Options specified on the command line take precedence
 *     over any values that might be specified in the
 *     environment variables or in the config file. So if both
 *     command line and config file specify a value for the
 *     input file, like:
 *       cli: -input inputDir\input2.tree
 *       ini: input = inputDir\input1.tree
 *     the one from the commend line will be used. So in this
 *     case: inputDir\input2.tree
 * 
 *     For most command line options two versions are available,
 *     with either a long or a short option name. E.g. for the
 *     input file both:
 *      -i path-to-input-file
 *     and
 *      -input path-to-input-file
 *     are valid options.
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpConfig {

    /**
     * default name for config file
     */
    protected final String defaultNameConfig;

    /**
     * default number of tasks
     */
    protected final int defaultNumTasks;

    /**
     * name of the environment variable for the config file
     */
    protected final String environmentVarConfig;

    /**
     * name of the environment variable for the tree url
     */
    protected final String environmentVarTree;
    
    /**
     * the ini4j object for the Ini file
     */
    protected Ini configIni;
    
    /**
     * log4j logger object
     */
    private static Logger logger;

    /**
     * program value: file system path separator
     */
    public MrpOption pathSeparator = new MrpOption();
    
    /**
     * program option: use environment variables
     */
    public MrpOption environmentVars = new MrpOption();

    /**
     * program option: number of tasks to split the processes over
     */
    public MrpIntOption numTasks = new MrpIntOption();

    /**
     * program option: value of the hashDepth for the "taxon database"
     */
    public MrpIntOption hashDepth = new MrpIntOption();

    /**
     * program option: the url for the "taxon database"
     */
    public MrpUrlOption treeUrl = new MrpUrlOption();

    /**
     * program option: path to config file
     */
    public MrpFileOption configFile = new MrpFileOption(true, false);

    /**
     * program option: path to work dir (user.dir)
     */
    public MrpFolderOption workDir = new MrpFolderOption(false, false);

    /**
     * program option: path to input file
     */
    public MrpPathOption inputFile = new MrpPathOption();

    /**
     * program option: path to output file
     */
    public MrpPathOption outputFile = new MrpPathOption();

    /**
     * program option: path to temp dir
     */
    public MrpArgumentOption tempDir = new MrpArgumentOption();

    /**
     * program option: path to data root dir
     */
    public MrpArgumentOption dataRootDir = new MrpArgumentOption();

    /**
     * program option: name of data dir
     */
    public MrpArgumentOption dataDir = new MrpArgumentOption();

    /**
     * program option: path to data root/data dir
     */
    public MrpArgumentOption dataPath = new MrpArgumentOption();

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
     * following config values: (cli = command line option,
     * ini = config.ini file option; the curly brackets are not
     * part of the option value; just used to separate
     * option and value)
     * 
     * - Use environment vars
     *   cli: -n
     *        -noenv
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
     * - The the number of hadoop tasks to use for the processes
     *   cli: -n {integer value}
     *        -numTasks {integer value}
     *   ini: [Main] numTasks = {integer value}
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
     * - The path to the newick tree output file
     *   cli: -o {path to output file}
     *        -output {path to output file}
     *   ini: [Main] output = {path to output file}
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
     * @param _nameConfig    the default name for thre configuration file
     * @param _numTasks      the default number of tasks for the mr procsses
     * @param _envConfig     the name of the environment variable that holds the name of the config file
     * @param _envTree       the name of the environment variable that holds the url of the tree to be used
     * @throws java.io.IOException
    */
    public MrpConfig(String _nameConfig, int _numTasks, 
            String _envConfig, String _envTree) throws IOException, Exception {
        this.defaultNameConfig = _nameConfig;
        this.defaultNumTasks = _numTasks;
        this.environmentVarConfig = _envConfig;
        this.environmentVarTree = _envTree;
        logger = Logger.getLogger(MrpConfig.class.getName());
        
        this.pathSeparator.setValue(File.separator);
        
        // set default values
        // will be overwritten if there are any values in the
        // ini file or on the command line
        this.workDir.setValue(System.getProperty("user.dir"));
        this.numTasks.setValue(this.defaultNumTasks);
        this.inputFile.setValue("input.txt");
        this.outputFile.setValue("output.txt");
        
        /** Set the properties for the MrpOptions to be used
         * These properties also serve as the cli and ini4j
         * option properties
         */
        this.environmentVars.setProperties("use environment variables", 
                "e", "env");                                                    // use of environment variables
        this.configFile.setProperties("path to config.ini", 
                "c", "config", "file path");                                    // path to config file                               // path to config file
        this.numTasks.setProperties("number of concurrent tasks", 
                "n", "numtasks" , "integer", "Main", "numTasks");               // value of numTasks option
        this.hashDepth.setProperties("hashdepth for decoding taxonname", 
                "h", "hashdepth" , "integer", "Main", "hashDepth");             // value of hashDepth option
        this.treeUrl.setProperties("treeURL", 
                "u", "url", "url", "Tree", "url");                              // tree URL as a string
        this.inputFile.setProperties("path to input file", 
                "i", "input", "file path", "Main", "input");                    // path to input file
        this.tempDir.setProperties("path to temp folder", 
                "t", "temp", "folder path", "Main", "tempDir");                 // path to temp dir
        this.outputFile.setProperties("path to output file", 
                "o", "output", "file path", "Main", "output");                    // path to input file
        this.dataRootDir.setProperties("path to dataroot folder", 
                "r", "root", "folder path", "Main", "root");                    // path to data root dir
        this.dataDir.setProperties("name of data folder", 
                "d", "datadir", "folder name", "Tree", "dataDir");              // path to data dir
        this.dataPath.setProperties("path to data folder", 
                "", "", "folder path");                                         // path to data dir
    }
    
    // getOptions
    // ------------------------------------------------------------------------
    /** 
    * Adds the options for the various configuration values to the
    * given Options object for a cli command line parser. These will 
    * be used by that command line parser to interprete the information 
    * that the user entered on the command line.
    * 
    * @param options  a cli Options object
    */
    public void getOptions(Options options) {
        logger.info("MrpConfig: Setting ini4j command line options");
        // environment variable option: if present then don't use environment 
        // variables:
        options.addOption(this.environmentVars.getOption());
        options.addOption(this.configFile.getOption());
        options.addOption(this.numTasks.getOption());
        options.addOption(this.hashDepth.getOption());
        options.addOption(this.inputFile.getOption());
        options.addOption(this.tempDir.getOption());
        options.addOption(this.outputFile.getOption());
        options.addOption(this.treeUrl.getOption());
        options.addOption(this.dataRootDir.getOption());
        options.addOption(this.dataDir.getOption());
    }
    
    // setOptions
    // ------------------------------------------------------------------------
    /** 
     * Processes the command line and config file options.
     * First it determines whether or not environment variables
     * should be used. Based on that, it is determined which configuration
     * file to use, whereupon the options in that file are checked and read
     * into the various MrpOption objects for the config values.
     * At the same time the command line input is parsed and any option
     * values found there are also read into the MrpOption objects concerned.
     * If for the same option, values are found boh in the config file and
     * on the command line, than the config values are overwritten with those 
     * read from the command line.
     * 
     * @param  cmdLine  a cli CommandLine object
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @throws java.lang.Exception
     */
    public void setOptions(CommandLine cmdLine) throws FileNotFoundException, IOException, Exception {
        boolean environment = false;
        // check the environment variables option
        this.environmentVars.setOption(cmdLine);
        if (this.environmentVars.hasValue()) {
            // then use environment variables for config file or tree
            environment = true;
            logger.info("MrpConfig: using environment variables = true");
        }
        logger.info("MrpConfig: Config file: determining cofiguration file");
        // See if the command line specifies a configuration file
        this.configFile.setOption(cmdLine);
        if(!this.configFile.hasValue()) {
            logger.info("MrpConfig: Config file: was not specified in command line options");
            // no config file specified on the command line
            if (true == environment) {
                // then environment variables should be used
                // see if there is a valid config file specified in a 
                // <this.environmentVarConfig> environment variable
                String name = System.getenv(this.environmentVarConfig);
                if (name != null) {
                    // then a config file name was found in the env. vars
                    logger.info("MrpConfig: Config file: using environment variable: " + this.environmentVarConfig);
                    this.configFile.setValue(name);
                } else {
                    // no config file name was found in the env. vars
                    logger.fatal("MrpConfig: Can not obtain environment variable: " + this.environmentVarConfig);
                    throw new IllegalArgumentException("MrpConfig: Environment variable '" + 
                            this.environmentVarConfig + "' has not been specified");
                }
            } else {
                // env.variables are not to be used; try with the default config file name
                logger.info("MrpConfig: Config file: using default name and location");
                this.configFile.setValue(defaultNameConfig);
            }
        } else {
            // a config file was specified on the command line that overrides all others
            logger.info("MrpConfig: Config file: was specified in command line options");
        }
        logger.info("MrpConfig: Config file: using " + this.configFile.getPath());
        // check the file name for correctness, existance, etc.
        // throws exeption if not usable
        this.configFile.checkFile();
        // get an ini4j inifile object for the ini file
        this.configIni = new Ini(this.configFile.getFile());
        // read the various options from ini file and command line
        // overwriting ini.values with command line values for
        // the same option when both are specfied
        this.numTasks.setOption(configIni, cmdLine);
        this.hashDepth.setOption(configIni, cmdLine);
        this.inputFile.setOption(configIni, cmdLine);
        this.tempDir.setOption(configIni, cmdLine);
        this.outputFile.setOption(configIni, cmdLine);
        this.dataRootDir.setOption(configIni, cmdLine);
        // if no dataroot was entered, use the default dir
        if (!this.dataRootDir.hasValue())
            this.dataRootDir.setValue(this.workDir.getPath());
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
                // then environment vars can be used
                // see if there is a valid tree url specified in a 
                // <this.environmentVarTree> environment variable
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
        // check the url for wellformedness, existance, etc.
        // throws exeption if not usable
        this.treeUrl.checkValue();
        // set the ini properties for the datadir; the ini section
        // to be used depends on the specified treeUrl
        this.dataDir.setProperties(this.treeUrl.getValue(), "dataDir"); 
        // get the option values from the ini file
        this.dataDir.setOption(configIni);
        // combine dataroot and datadir to 1 complete path
        this.dataPath.setValue(this.dataRootDir.getValue() + "/" + this.dataDir.getValue());
        logger.info("MrpConfig: Tree url: using url " + this.treeUrl.getValue());
        logger.info("MrpConfig: Tree url: using data dir " + this.dataPath.getValue());

        logger.info(" ");
        logger.info("MrpConfig: Config object ");
        logger.info("MrpConfig: -----------------------------------");
        logger.info("MrpConfig: Work dir    = " + this.workDir.getPath());
        logger.info("MrpConfig: Num tasks   = " + this.numTasks.getValue());
        logger.info("MrpConfig: Input file  = " + this.inputFile.getPath());
        logger.info("MrpConfig: Hashdepth   = " + this.hashDepth.getValue());
        logger.info("MrpConfig: Tree url    = " + this.treeUrl.getValue());
        logger.info("MrpConfig: Data root   = " + this.dataRootDir.getValue());
        logger.info("MrpConfig: Data dir    = " + this.dataDir.getValue());
        logger.info("MrpConfig: Data path   = " + this.dataPath.getValue());
        logger.info("MrpConfig: Temp dir    = " + this.tempDir.getValue());
        logger.info("MrpConfig: Output file = " + this.outputFile.getPath());
        logger.info("MrpConfig: -----------------------------------");
        
        // check the various options for correctness, etc.
        // throw exeption if not usable
        this.numTasks.checkValue();
        this.hashDepth.checkValue();
        this.inputFile.checkValue();
        this.tempDir.checkValue();
        this.outputFile.checkValue();
        this.dataRootDir.checkValue();
        this.treeUrl.checkValue();
        this.dataDir.checkValue();
    }
}
