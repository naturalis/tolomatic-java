/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.phylotastic;
/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Map;
import java.util.HashMap;
import org.apache.commons.cli.*;
import org.apache.commons.cli.Options;                  // brcause ini4j also has getOptions method
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.*;
import org.apache.log4j.Logger;
import org.ini4j.*;

public class MrpConfig {
    private final String pathSeparator = File.separator;  // file system path separator
    private final File defaultDir = new File("dummy.txt");    // path to default dir (user dir)
    
    private final String defaultNameConfig;             // default name for config file
    private final String environmentVarConfig;          // name environment var for config file
    private final String environmentVarTree;            // name environment var for tree url
    //
    private String logLevel;                            // log4j log level
    private final Map<String, Level> logLevels;
    private final Logger logger = Logger.getLogger("org.phylotastic.MrpConfig");
    
    private Ini configIni;                              // ini4j Ini file object
    private File configFile;                            // path to config file
    private File inputFile;                             // path to input file
    private String inputFilePath;                       // path to input file
    private File dataRoot;                              // dataroot file object
    private String dataRootPath;                        // dataroot path
    private File dataDir;                               // dataRoot/dataDir/
    private String dataDirPath;                         // dataDir path
    private String dataDirName;                         // name of data directory
    private File tempDir;                               // temp directory file object
    private String tempDirPath;                         // tempdir path
    private int hashDepth;                              // value of hashDepth option
    private String treeUrl;                             // tree URL as a string
    private File taxonFile;                             // taxon file File object
    private String taxonFilePath;                       // path to taxon file

    // constructor
    // ------------------------------------------------------------------------
    /** Construct a config object
     * @param nameConfig
     * @param envConfig
     * @param envTree
    */
    public MrpConfig(String nameConfig, String envConfig, String envTree) {
        this.defaultNameConfig = nameConfig;
        this.environmentVarConfig = envConfig;
        this.environmentVarTree = envTree;
        
        this.logLevels = new HashMap<>();
        this.logLevels.put("OFF", Level.OFF);
        this.logLevels.put("TRACE", Level.TRACE);
        this.logLevels.put("DEBUG", Level.DEBUG);
        this.logLevels.put("INFO", Level.INFO);
        this.logLevels.put("WARN", Level.WARN);
        this.logLevels.put("ERROR", Level.ERROR);
        this.logLevels.put("FATAL", Level.FATAL);
        
        this.logLevel = "OFF";
        this.logger.setLevel(this.logLevels.get(this.logLevel));
    }
    
    // getOptions
    // ------------------------------------------------------------------------
    /** add options to the command line arguments
    * @param  options  a ini4j Options object
    */
    public void getOptions(Options options) {
        // environment variable option: if present then don't use environment 
        // variables:
        options.addOption(new Option( "n", "environment", false, "do nót use environment variables" ));
        
        // config file option: with file path and/or name as option argument
        OptionBuilder.withDescription("config file");
        OptionBuilder.withArgName("path");
        OptionBuilder.hasArg();
        OptionBuilder.withLongOpt("config");
        options.addOption(OptionBuilder.create("c"));
        
        // input file option: with file path and/or name as option argument
        OptionBuilder.withDescription("input file");
        OptionBuilder.withArgName("path");
        OptionBuilder.hasArg();
        OptionBuilder.withLongOpt("input");
        options.addOption(OptionBuilder.create("i"));
        
        // dataroot option: with dir. path as option argument
        OptionBuilder.withDescription("dataroot directory");
        OptionBuilder.withArgName("path");
        OptionBuilder.hasArg();
        OptionBuilder.withLongOpt("dataroot");
        options.addOption(OptionBuilder.create( "r" ));
        
        // datadir option: with dir name as option argument
        OptionBuilder.withDescription("data directory name");
        OptionBuilder.withArgName("dir name");
        OptionBuilder.hasArg();
        OptionBuilder.withLongOpt("datadir");
        options.addOption(OptionBuilder.create("d"));
        
        // output dir option: with dir name as option argument
        OptionBuilder.withDescription( "temp directory");
        OptionBuilder.withArgName("path");
        OptionBuilder.hasArg();
        OptionBuilder.withLongOpt("temp");
        options.addOption(OptionBuilder.create("t"));
        
        // tree url option: with url as option argument
        OptionBuilder.withDescription( "tree url");
        OptionBuilder.withArgName("url");
        OptionBuilder.hasArg();
        OptionBuilder.withLongOpt("treeurl");
        options.addOption(OptionBuilder.create("u"));
    }
    
    // setOptions
    // ------------------------------------------------------------------------
    /** process the config and command line options
    * @param  cmdLine  a ini4j CommandLine object
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
    */
    public void setOptions(CommandLine cmdLine) throws FileNotFoundException, IOException {
        boolean environment = true;
        if (cmdLine.hasOption("environment")) {
            // do not use environment variable for config file or tree
            environment = false;
            logger.info("MrpConfig: not using environment variables = true");
        }
        if(cmdLine.hasOption("config"))
            // override default config file
            this.setConfigFile(cmdLine.getOptionValue("config"));
        else {
            if (true == environment) {
                String name = System.getenv(this.environmentVarConfig);
                if (name != null) {
                    this.setConfigFile(name);
                }
                else {
                    logger.fatal("MrpConfig: Can not obtain environment variable: " + this.environmentVarConfig);
                    throw new IllegalArgumentException("MrpConfig: Environment variable '" + 
                            this.environmentVarConfig + "' has not been specified");
                }
            }
            else
                this.setConfigFile(defaultNameConfig);
        }
        this.readConfigFile();
        if(cmdLine.hasOption("input"))
            // override the inputFile value
            this.setInputFile(cmdLine.getOptionValue("input"));
        if(cmdLine.hasOption("dataroot"))
            // override the dataroot value
            this.setDataRoot(cmdLine.getOptionValue("dataroot"));
        if(cmdLine.hasOption("datadir"))
            // override the dataDir value
            this.setDataDir(cmdLine.getOptionValue("datadir"));
        if(cmdLine.hasOption("tempdir"))
            // override the tempDir value
            this.setTempDir(cmdLine.getOptionValue("tempdir"));
        if(cmdLine.hasOption("treeurl"))
            // override tree url value
            this.setTreeUrl(cmdLine.getOptionValue("treeurl"));
        else {
            if (true == environment) {
                String name = System.getenv(this.environmentVarTree);
                if (name != null)
                    this.setTreeUrl(name);
                else {
                    logger.fatal("MrpConfig: Can not obtain environment variable: " + this.environmentVarTree);
                    throw new IllegalArgumentException("MrpConfig: Environment variable '" + 
                            this.environmentVarTree + "' has not been specified");
                }
            }
        }
        this.setDataDir(this.configIni.get(this.getTreeUrl(), "dataDir"));    
        
        logger.info("MrpConfig: Config object ");
        logger.info("MrpConfig: -----------------------------------");
        logger.info("MrpConfig: Input file = " + this.getInputFile());
        logger.info("MrpConfig: Data root  = " + this.getDataRoot());
        logger.info("MrpConfig: Root path  = " + this.getPathDataRoot());
        logger.info("MrpConfig: Temp dir   = " + this.getTempDir());
        logger.info("MrpConfig: Temp path  = " + this.getPathTempDir());
        logger.info("MrpConfig: Hashdepth  = " + this.getHashDepth());
        logger.info("MrpConfig: Tree url   = " + this.getTreeUrl());
        logger.info("MrpConfig: Data dir   = " + this.getDataDir());
        logger.info("MrpConfig: Data path  = " + this.getPathDataDir());
        logger.info("MrpConfig: -----------------------------------");
        }
    
    // configFile
    // ------------------------------------------------------------------------
    /** set het config File object
    * @param  name  de naam van de config file
    * @throws FileNotFoundException
    */
    private void setConfigFile(String name) throws FileNotFoundException {
        logger.info("MrpConfig: setting config file: " + name );
        if (name.equals(""))
            throw new IllegalArgumentException("Name config file has not been specified");
        else  {
            this.configFile = new File(name);
            if (!this.configFile.exists())
		throw new FileNotFoundException("Config file not found: " + this.configFile.getPath());
            else {
		if (!this.configFile.isFile())
                    throw new FileNotFoundException("Config file is not a file: " + this.configFile.getPath());
		}
        }
    }
    
    /** Get het config File object
    * @return the File object for the config file
    */
    public File getConfigFile() {
        return this.configFile;
    }
    
    /** set het config File object
    * @param  name  de naam van de config file
    * @throws FileNotFoundException
    */
    private void readConfigFile() throws IOException {
        this.configIni = new Ini(this.configFile);
        this.setDataRoot(this.configIni.get("Main", "dataRoot"));
//        this.setInputFile(this.configIni.get("Main", "input"));
        this.setTempDir(this.configIni.get("Main", "tempDir"));
        this.setHashDepth(this.configIni.get("Main", "hashDepth"));
        this.setTreeUrl(this.configIni.get("Tree", "default"));
        this.setDataDir(this.configIni.get("Tree", "dataDir"));
    }
    
    // logLevel
    // ------------------------------------------------------------------------
    /** set the logging level
    * @param  level  the log level
    */
    public void setLogLevel(String level) {
        logger.info("MrpConfig: logLevel = " + level);
        level = level.toUpperCase();
        if (this.logLevels.containsKey(level) == true) {
                this.logLevel = level;
                logger.setLevel(this.logLevels.get(level));
        }
        else
            throw new IllegalArgumentException("Unknown logging level specified = " + level);
    }
    
    /** get the logging level as string
    * @return logLevel
    */
    public String getLogLevel() {
        return this.logLevel;
    }
    
    /** get the logging level as string
    * @return logLevel
    */
    public Level getLoggingLevel() {
        return (this.logLevels.get(this.logLevel));
    }
    
    // defaultDir
    // ------------------------------------------------------------------------
    /** Get het defaultdir File object
    * @return the defaultdirectory File object
    */
    public File getDefaultDir() {
        return this.defaultDir;
    }
    
    // method: getPathSeparator
    // ------------------------------------------------------------------------
    /** Get the file system PathSeparator
    * @return the separator
    */
    public String getPathSeparator() {
        return this.pathSeparator;
    }
    
    // configFile
    // ------------------------------------------------------------------------
    /** set het config File object
    * @param  name  de naam van de config file
    * @throws FileNotFoundException
    */
    private void setInputFile(String name) throws FileNotFoundException, IOException {
        logger.info("MrpConfig: setting input file: " + name );
        if (name.equals(""))
            throw new IllegalArgumentException("Name input file has not been specified");
        else  {
            this.inputFile = new File(name);
            if (!this.inputFile.exists())
		throw new FileNotFoundException("Input file not found: " + this.inputFile.getPath());
            else {
		if (!this.inputFile.isFile())
                    throw new FileNotFoundException("Input file is not a file: " + this.inputFile.getPath());
                else
                    this.inputFilePath = this.inputFile.getCanonicalPath();
            }
        }
    }    
    
    /** Get het config File object
    * @return the File object for the config file
    */
    public File getInputFile() {
        return this.inputFile;
    }
    
    /** the input file path
    * @return the input file path
    */
    public String getPathInputFile() {
        return this.inputFilePath;
    }
    
    // dataRoot directory
    // ------------------------------------------------------------------------
    /** set the dataRoot directory value
    * @param  dir  the value of the dataRoot dir
     * @throws java.io.IOException
    */
    public void setDataRoot(String dir) throws IOException {
        logger.info("MrpConfig: dataRoot = " + dir );
        this.dataRoot = new File(dir);
        this.dataRootPath = this.dataRoot.getCanonicalPath() + this.getPathSeparator();
    }
    
    /** the dataRoot directory file object
    * @return the dataRoot directory value
    */
    public File getDataRoot() {
        return this.dataRoot;
    }
    
    /** the dataRoot directory path
    * @return the dataRoot directory path
    */
    public String getPathDataRoot() {
        return this.dataRootPath;
    }
    
    // tempDir
    // ------------------------------------------------------------------------
    /** set the temp directory value
    * @param  dir  the name of the tempDir
     * @throws java.io.IOException
    */
    public void setTempDir(String dir) throws IOException {
        logger.info("MrpConfig: tempDir = " + dir );
        this.tempDir = new File(dir);
        this.tempDirPath = this.tempDir.getCanonicalPath() + this.getPathSeparator();
    }
    
    /** the temp directory value
    * @return the temp directory value
    */
    public File getTempDir() {
        return this.tempDir;
    }
    
    /** the temp directory path
    * @return the temp directory path
    */
    public String getPathTempDir() {
        return this.tempDirPath;
    }

    // hashDepth
    // ------------------------------------------------------------------------
    /** set the hashDepth value
    * @param  depth  the value of the hashDepth option as an int
    */
    public void setHashDepth(int depth) {
        logger.info("MrpConfig: hashDepth = " + depth );
        this.hashDepth = depth;
    }
    
    /** set the hashDepth value
    * @param  depth  the value of the hashDepth option as a string
    */
    public void setHashDepth(String depth) {
        logger.info("MrpConfig: hashDepth = " + depth );
        this.hashDepth = Integer.parseInt(depth);
    }
    
    /** get the hashDepth value
    * @return the hashDepth value
    */
    public int getHashDepth() {
        return this.hashDepth;
    }
    
    // treeUrl
    // ------------------------------------------------------------------------
    /** set the tree url value
    * @param  url  the name of the tempDir
     * @throws java.net.MalformedURLException
    */
    public void setTreeUrl(String url) throws MalformedURLException {
        logger.info("MrpConfig: treeUrl = " + url );
        try {
            URL test = new URL(url);        // test url for correctness
            this.treeUrl = url;             // is correctly formed url
        } catch (MalformedURLException e) {
            throw e;                        // is not correctly formed url
        }
    }
    
    /** the tree url value
    * @return the tree url value
    */
    public String getTreeUrl() {
        return this.treeUrl;
    }
    
    // dataDir
    // ------------------------------------------------------------------------
    /** set the data directory value
    * @param  dir  the value of the outputDir
     * @throws java.io.IOException
    */
    public void setDataDir(String dir) throws IOException {
    logger.info("MrpConfig: dataDir = " + dir );
        this.dataDirName = dir;
        this.dataDir = new File(this.getPathDataRoot() + this.dataDirName 
                                + this.getPathSeparator());
        this.dataDirPath = this.dataDir.getCanonicalPath() + this.getPathSeparator();
    }
    
    /** the data directory option value
    * @return the data directory option value
    */
    public String getNameDataDir() {
        return this.dataDirName;
    }
    
    /** the output directory value
    * @return the output directory value
    */
    public File getDataDir() {
        return this.dataDir;
    }
    
    /** the dataPath (dataroot/datadir)
    * @return the data path
    */
    public String getPathDataDir() {
        return this.dataDirPath;
    }
    
    // method: makeSafeString
    // ------------------------------------------------------------------------
    /**
     * This is an opaque way of turning arbitrary string into
     * "safe" strings that can be turned into paths in
     * the file system
     * @param unsafeString
     * @return safeString
     * @throws java.security.NoSuchAlgorithmException
     */
    public String makeSafeString(String unsafeString) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(unsafeString.getBytes());
        byte byteData[] = md.digest();
        StringBuilder safeString = new StringBuilder();
        for (int i = 0; i < byteData.length; i++)
        {
            safeString.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return safeString.toString();
    }

    // method: determineTaxonFile
    // ------------------------------------------------------------------------
    /**
     * Constructs the location of the file that encodes the tip-to-root path for the
     * focal tip given the focal tree
     * @param taxon
     * @return taxon part of filepath
     * @throws java.security.NoSuchAlgorithmException
     */
    public File determineTaxonFile( String taxon) throws NoSuchAlgorithmException {

        logger.info("MrpConfig: taxon value = " + taxon );
        // make the taxon part of the path
        String safeString = StringUtils.capitalize(taxon);
        safeString = this.makeSafeString(safeString);
        //safeString = StringUtils.capitalize(safeString);
        logger.info("MrpConfig: taxon encoded = " + safeString );
        StringBuilder taxonPath = new StringBuilder(this.getPathDataDir());
        for ( int i = 0; i <= this.hashDepth; i++ )
        {
            taxonPath.append(safeString.charAt(i)).append(this.pathSeparator);
        }
        taxonPath.append(safeString);
        this.taxonFilePath = taxonPath.toString();
        this.taxonFile = new File(taxonFilePath);
        logger.info("MrpConfig: taxon file = " + this.taxonFilePath );
        return this.taxonFile;
    }
}
