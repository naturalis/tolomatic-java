package org.phylotastic.SourcePackages.mrpconfig;

public class MrpIntOption extends MrpArgumentOption {
    /**
     * An implementation of the MrpArgumentOption class
     * for integer values.
     */
    
    /**
     * default constructor
     *
     */
    public MrpIntOption() {
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
    public MrpIntOption(String _description, String _shortOption, String _longOption,
                        String _argumentName,
                        Boolean _hasIniVersion, String _iniSection, String _iniName) {
        super(_description, _shortOption, _longOption, _argumentName,
                        _hasIniVersion, _iniSection, _iniName);
    }
    
    /**
     * This is the method that sets the actual attribute
     * value for a specific config value (option)
     *
     * @param _value
     * @throws IllegalArgumentException
     */
    @Override
    public void setValue(String _value) throws IllegalArgumentException {
    try { 
        Integer.parseInt(_value); 
    } catch(NumberFormatException exc) { 
            throw new IllegalArgumentException("Option: " + this.description + ": " + _value + "  is not an integer value");
    }
        this.value = _value;
    }
    
    /**
     * Return the integer value
     *
     * @return
     */
    public int getIntValue() {
        return Integer.parseInt(this.value);
    }
}
