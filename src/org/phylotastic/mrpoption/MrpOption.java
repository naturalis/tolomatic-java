package org.phylotastic.mrpoption;

import org.apache.commons.cli.*;
import org.apache.log4j.*;

/**
 *     Class MrpOption
 *   
 *     This class contains methods and fields
 *     to maintain the data for an optional
 *     configuration value.
 *     This includes the data needed to implement
 *     the value as a command line option and/or
 *     as an ini.file option
 * 
 *     The class mainly uses a default constructor
 *     and one or more specfic setProperties methods
 *     This in order to not have to duplicate too
 *     many constructors in each of the decendant classes
 * 
 *     The class uses commons.cli. Most of it's properties
 *     exist of cli option properties, like:
 * 
 *      description:    cli: option description
 *      shortOption:    cli: short option (name), e.d. -c for configfile
 *      longOption:     cli: long option (name), e.d. -config for configfile
 * 
 *     The class can only be used for boolean type options;
 *     i.e. options without arguments values. So only for:
 *      -n              don't use environment values
 *      not for
 *      -c config.ini   path of config file to be used
 * 
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpOption {

    /**
     *     a short string description as used in the cli option
     */
    public String description;

    /**
     *     the short version of the cli option (name)
     */
    public String shortOption;

    /**
     *     the long version of the cli option (name)
     */
    public String longOption;
    
    /**
     *     the value the user specified for the option
     */
    protected String value = "";

    /**
     *     a default logger for the option class
     */
    protected static Logger logger;
    
    /**
     *     default constructor
     *
     */
    public MrpOption() {
        super();
        this.description = "";
        this.shortOption = "";
        this.longOption = "";
        logger = Logger.getLogger(MrpOption.class.getName());
    }
    
    /**
     *     full constructor
     *
     * @param _description      cli: option description
     * @param _shortOption      cli: short option (name)
     * @param _longOption       cli: long option (name)
     */
    public MrpOption(String _description, String _shortOption, String _longOption) {
        super();
        this.description = _description;
        this.shortOption = _shortOption;
        this.longOption = _longOption;
        this.value = "";
        logger = Logger.getLogger(MrpOption.class.getName());
    }
    
    /**
     *     Method: setProperties
     * 
     *     set the properties for a command line option without argument
     *     use with default constructor
     *
     * @param _description      cli: option description
     * @param _shortOption      cli: short option (name)
     * @param _longOption       cli: long option (name)
     */
    public void setProperties(String _description, String _shortOption, String _longOption) {
        this.description = _description;
        this.shortOption = _shortOption;
        this.longOption = _longOption;
    }
    
    /**
     *     create and return an org.apache.commons.cli Option object
     *     for this.option
     *
     * @return  the org.apache.commons.cli Option object
     */
    public Option getOption() {
        OptionBuilder.withDescription(this.description);
        OptionBuilder.withLongOpt(this.longOption);
        return OptionBuilder.create(this.shortOption);
    }
    
    /**
     *
     * @param _cmdLine  org.apache.commons.cli CommandLine object
     * @throws Exception
     */
    public void setOption(CommandLine _cmdLine) throws Exception {
        if(_cmdLine.hasOption(this.shortOption))
            this.setValue(this.shortOption);
    }
    
    /**
     *
     * @param _value    the value to be set for the option
     */
    public void setValue(String _value) {
        this.value = _value;
    }
    
    /** 
     *     check if a correct value was specified for this.option;
     *     otherwise @throws an IllegalArgumentException
     * 
     * @throws IllegalArgumentException
    */
    public void checkValue() throws IllegalArgumentException {
        if (this.value.isEmpty())
            throw new IllegalArgumentException("No value has been specified for: " 
                    + this.description);
    }
    
    /**
     *
     * @return      the value of this.option
     */
    public String getValue() {
        return this.value;
    }
    
    /**
     *
     * @return      true if this.option has a value sppecified for it
     */
    public Boolean hasValue() {
        return (!this.value.isEmpty());
    }

    /**
     *     Returns the string representation of this pathnode
     * 
     * @return      this.Option as a string, like: 
     *      "Option: -n, -noenv: do nót use environment variables"
     */
    @Override
    public String toString () {
        String result = "Option: ";
        String separator = "";
        if (!this.shortOption.isEmpty()) {
            result += "-" + shortOption;
            separator = ", -";
        }
        if (!this.longOption.isEmpty())
            result += separator + longOption;
        if (!this.description.isEmpty())
            result += "; " + this.description;
        else
            result += "; not specified";
        return result;
    }
}
