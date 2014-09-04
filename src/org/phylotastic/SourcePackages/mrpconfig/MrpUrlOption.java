package org.phylotastic.SourcePackages.mrpconfig;


import java.net.URL;
import java.net.MalformedURLException;

public class MrpUrlOption extends MrpArgumentOption {
    /**
     * An implementation of the MrpArgumentOption class
     * meant for URL values.
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
     * @param _description
     * @param _shortOption
     * @param _longOption
     * @param _argumentName
     * @param _hasIniVersion
     * @param _iniSection
     * @param _iniName
     */
    public MrpUrlOption(String _description, String _shortOption, String _longOption,
                        String _argumentName,
                        Boolean _hasIniVersion, String _iniSection, String _iniName) {
        super(_description, _shortOption, _longOption, _argumentName,
                        _hasIniVersion, _iniSection, _iniName);
    }
    
    /**
     * This is the method that sets the actual value for
     * the specific url but not after first checking
     * wether it is a valid and well formed URL
     * 
     * @param _value
     * @throws java.lang.Exception
     */
    @Override
    public void setValue(String _value) throws Exception {
        MrpOption.debugLogger.debug("MrpUrlOption: setting url: " + this.description + " = " + _value );
        // test url for correctness
        try {
            URL test = new URL(_value);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Option: " + this.description + ": " + _value + "  is not a well formed URL");
        }
        // is correctly formed url
        this.value = _value;             
    }
    
    /** 
     * check if a correct file name was specified in the options;
     * in that case this should have a value and a file object
    */
    public void checkUrl() {
        if (this.value.isEmpty())
            throw new IllegalArgumentException("No correct URL has been specified for: " + this.description);
        else {
        }
    }
}
