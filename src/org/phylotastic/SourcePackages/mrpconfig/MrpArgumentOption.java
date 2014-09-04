package org.phylotastic.SourcePackages.mrpconfig;


import org.ini4j.*;
import org.apache.commons.cli.*;

public class MrpArgumentOption extends MrpOption {
    /**
     * This class extends the MrpOption class
     * with the posibilitie to implement cli options
     * with arguments like the path of an inputfile:
     * "-input inputdir\input.txt"
     * Within that scope it also extends that class
     * with the possibility to implement ini file
     * options; since those by default are
     * argument options like: 
     * "[Main]"
     * "input = inputdir\input.txt"
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
     * @param _description
     * @param _shortOption
     * @param _longOption
     * @param _argumentName
     * @param _hasIniVersion
     * @param _iniSection
     * @param _iniName
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
     * @param _description
     * @param _shortOption
     * @param _longOption
     * @param _argumentName
     * @param _iniSection
     * @param _iniName
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
     * @param _description
     * @param _shortOption
     * @param _longOption
     * @param _argumentName
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
     * @param _iniSection
     * @param _iniName
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
     * @return
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
     * reading it from an ini file
     *
     * @param configIni
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
     * @param _cmdLine
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
     * @param configIni
     * @param _cmdLine
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
     * @param _value
     * @throws java.lang.Exception
     */
    @Override
    public void setValue(String _value) throws Exception {
        this.value = _value;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String getValue() {
        return this.value;
    }
}
