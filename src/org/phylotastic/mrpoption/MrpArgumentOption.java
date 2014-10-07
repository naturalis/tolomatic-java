package org.phylotastic.mrpoption;
//package org.phylotastic.SourcePackages.mrpoption;

import org.ini4j.*;
import org.apache.commons.cli.*;

/**
 *
 * @author ...
 */
public class MrpArgumentOption extends MrpOption {
    /**
     * This class extends the MrpOption class
     * with the posibilitie to implement cli options
     * with arguments like the path of an inputfile:
     * "-input inputdir\input.txt"
     * 
     * Within that scope it also extends that class
     * with the possibility to implement ini file
     * options; since those by default are
     * argument options like: 
     * "[Main]"
     * "input = inputdir\input.txt"
     * 
     * Not all cli options need to have counterparts
     * in the ini file
     * 
     */
    protected String argumentName;              // the cli name for the argument
    protected Boolean hasIniVersion;            // has a config.ini implementation?
    protected String iniSection;                // the options section name in the ini file
    protected String iniName;                   // the options ini name of the argument
    
    /**
     * default constructor
     *
     */
    public MrpArgumentOption() {
        super();
        this.argumentName = "";
        this.hasIniVersion = false;
        this.iniSection = "";
        this.iniName = "";
    }
    
    /**
     * full constructor
     *
     * @param _description      cli: option description
     * @param _shortOption      cli: short option (name)
     * @param _longOption       cli: long option (name)
     * @param _argumentName     cli: argument name
     * @param _hasIniVersion    indicator if option value can be specified in config.ini file
     * @param _iniSection       ini: the section name for the value in he ini file
     * @param _iniName          ini: the key for the value in the ini file
     */
    public MrpArgumentOption(String _description, String _shortOption, String _longOption,
                        String _argumentName,
                        Boolean _hasIniVersion, String _iniSection, String _iniName) {
        super(_description, _shortOption, _longOption);
        this.argumentName = _argumentName;
        this.hasIniVersion = _hasIniVersion;
        this.iniSection = _iniSection;
        this.iniName = _iniName;
    }
    
    /**
     * set properties for a command line option with argument
     * use with default constructor
     *
     * @param _description      cli: option description
     * @param _shortOption      cli: short option (name)
     * @param _longOption       cli: long option (name)
     * @param _argumentName     cli: argument name
     * @param _iniSection       ini: the section name for the value in he ini file
     * @param _iniName          ini: the key for the value in the ini file
     */
    public void setProperties(String _description, String _shortOption, String _longOption,
                                String _argumentName,
                                String _iniSection, String _iniName) {
        super.setProperties(_description, _shortOption, _longOption);
        this.argumentName = _argumentName;
        this.hasIniVersion = true;
        this.iniSection = _iniSection;
        this.iniName = _iniName;
    }
    
    /**
     * set properties for a command line only option with argument
     * use with default constructor
     *
     * @param _description      cli: option description
     * @param _shortOption      cli: short option (name)
     * @param _longOption       cli: long option (name)
     * @param _argumentName     cli: argument name
     */
    public void setProperties(String _description, String _shortOption, String _longOption,
                                String _argumentName) {
        super.setProperties(_description, _shortOption, _longOption);
        this.argumentName = _argumentName;
    }
    
    /**
     * set properties for an ini only -option
     * use with default constructor
     *
     * @param _iniSection       ini: the section name for the value in he ini file
     * @param _iniName          ini: the key for the value in the ini file
     */
    public void setProperties(String _iniSection, String _iniName) {
        this.hasIniVersion = true;
        this.iniSection = _iniSection;
        this.iniName = _iniName;
    }
    
    /**
     * create and return an org.apache.commons.cli Option object
     * for this config value
     *
     * @returns     a org.apache.commons.cli Option object for this.option
     */
    @Override
    public Option getOption() {
        OptionBuilder.withDescription(this.description);
        OptionBuilder.hasArg();
        OptionBuilder.withArgName(this.argumentName);
        OptionBuilder.withLongOpt(this.longOption);
        return OptionBuilder.create(this.shortOption);
    }
    
    /**
     * Set the value ot this config value by
     * reading it from a specified ini file
     *
     * @param configIni     ini: the path to the (config.)ini file
     * @throws java.lang.Exception
     */
    public void setOption(Ini configIni) throws Exception {
        String item = configIni.get(this.iniSection, this.iniName);
        if (item == null || item.isEmpty()) {
        } else
        //if (item != null)
            this.setValue(item);
    }
    
    /**
     * Set the value ot this config value by
     * reading it from a cli command line parser
     *
     * @param _cmdLine  org.apache.commons.cli CommandLine object
     * @throws Exception
     */
    @Override
    public void setOption(CommandLine _cmdLine) throws Exception {
        if(_cmdLine.hasOption(this.shortOption))
            this.setValue(_cmdLine.getOptionValue(this.shortOption));
    }
    
    /**
     * Set the value ot this config value by
     * reading it both from
     * a specified ini file and
     * a specified cli command line parser
     * 
     * If both ini file and command line specify
     * a value for the same option, the cli
     * value overwrites the ini file value
     *
     * @param configIni     ini: the path to the (config.)ini file
     * @param _cmdLine      org.apache.commons.cli CommandLine object
     * @throws java.lang.Exception
     */
    public void setOption(Ini configIni, CommandLine _cmdLine) throws Exception {
        this.setOption(configIni);
        this.setOption(_cmdLine);
    }
    
    /**
     * This is the method that sets the actual attribute
     * value for a specific config value (option)
     * Also this is the method that should be overridden
     * in more specialised subclasses of MrpArgumentOption
     * and nót the setOption method(s)
     *
     * @param _value    this.option's attribute value
     */
    @Override
    public void setValue(String _value) {
        this.value = _value;
    }
    
    /** 
     * check if a correct value was specified for this.option;
     * otherwise @throws an IllegalArgumentException
     * 
     * @throws IllegalArgumentException
    */
    @Override
    public void checkValue() throws IllegalArgumentException {
        if (this.value.isEmpty())
            throw new IllegalArgumentException("No value has been specified for: " 
                    + this.description);
    }
    
    /**
     *
     * @return      the attribute value of this.option
     */
    @Override
    public String getValue() {
        return this.value;
    }
}
