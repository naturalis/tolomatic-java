package org.phylotastic.mrpoption;
//package org.phylotastic.SourcePackages.mrpoption;

/**
 *
 * @author ...
 */
import java.net.URL;
import java.net.MalformedURLException;

public class MrpUrlOption extends MrpArgumentOption {
    /**
     * An implementation of the MrpArgumentOption class
     * focused on URL values.
     */
    
    /**
     * default constructor
     *
     */
    public MrpUrlOption() {
        super();
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
    public MrpUrlOption(String _description, String _shortOption, String _longOption,
                        String _argumentName,
                        Boolean _hasIniVersion, String _iniSection, String _iniName) {
        super(_description, _shortOption, _longOption, _argumentName,
                        _hasIniVersion, _iniSection, _iniName);
    }
    
    /**
     * This is the method that sets the actual value for
     * the specific url 
     * 
     * @param _value    the URL string to set as this.option's value
     */
    @Override
    public void setValue(String _value) {
        MrpOption.debugLogger.debug("MrpUrlOption: setting url: " + this.description + " = " + _value );
        this.value = _value;             
    }
    
    /** 
     * check if a correct URL value was specified for this.option;
     * otherwise @throw an IllegalArgumentException
     * 
     * @throws IllegalArgumentException
    */
    @Override
    public void checkValue() throws IllegalArgumentException {
        if (this.value.isEmpty())
            throw new IllegalArgumentException("No value has been specified for: " 
                    + this.description);
        else {
            try {
                URL test = new URL(this.value);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Option: " + this.description + 
                        ": " + this.value + "  is not a well formed URL");
            }
        }
    }
}
