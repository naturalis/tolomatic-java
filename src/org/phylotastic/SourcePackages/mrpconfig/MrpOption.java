package org.phylotastic.SourcePackages.mrpconfig;

import org.apache.commons.cli.*;
import org.apache.log4j.*;

public class MrpOption {
    /**
     * This class contains methods and fields
     * to maintain the data for an optional
     * configuration value.
     * This includes the data needed to implement
     * the value as a command line option and/or
     * as an ini.file option
     * 
     * The class mainly uses a default constructor
     * and one or more specfic setProperties methods
     * This in oorder to not have to duplicate too
     * many constructors in each of the dercendant classes
     * 
     */
    protected String description;               // a short string description
    protected String shortOption;               // the short version of the cli option
    protected String longOption;                // the long version of the cli option
    
    protected String value = "";                // the current value of the option
    protected static Logger logger;
    protected static Logger debugLogger;
    
    /**
     * default constructor
     *
     */
    public MrpOption() {
        super();
        this.description = "";
        this.shortOption = "";
        this.longOption = "";
        logger = Logger.getLogger(MrpOption.class.getName());
        debugLogger = Logger.getLogger("debugLogger");
    }
    
    /**
     * full constructor
     *
     * @param _description
     * @param _shortOption
     * @param _longOption
     */
    public MrpOption(String _description, String _shortOption, String _longOption) {
        super();
        this.description = _description;
        this.shortOption = _shortOption;
        this.longOption = _longOption;
        this.value = "";
        logger = Logger.getLogger(MrpOption.class.getName());
        debugLogger = Logger.getLogger("debugLogger");
    }
    
    /**
     * set the properties for a command line option without argument
     * use with default constructor
     *
     * @param _description
     * @param _shortOption
     * @param _longOption
     */
    public void setProperties(String _description, String _shortOption, String _longOption) {
        this.description = _description;
        this.shortOption = _shortOption;
        this.longOption = _longOption;
    }
    
    /**
     * create and return an org.apache.commons.cli Option object
     * for this option
     *
     * @return
     */
    public Option getOption() {
        OptionBuilder.withDescription(this.description);
        OptionBuilder.withLongOpt(this.longOption);
        return OptionBuilder.create(this.shortOption);
    }
    
    /**
     *
     * @param _cmdLine
     * @throws Exception
     */
    public void setOption(CommandLine _cmdLine) throws Exception {
        if(_cmdLine.hasOption(this.shortOption))
            this.setValue(this.shortOption);
    }
    
    /**
     *
     * @param _value
     * @throws Exception
     */
    public void setValue(String _value) throws Exception {
        this.value = _value;
    }
    
    /**
     *
     * @return
     */
    public String getValue() {
        return this.value;
    }
    
    /**
     *
     * @return
     */
    public Boolean hasValue() {
        return (!this.value.isEmpty());
    }
}
