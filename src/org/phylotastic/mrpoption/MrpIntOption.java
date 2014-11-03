package org.phylotastic.mrpoption;

/**
 *     Class MrpIntOption
 * 
 *     An implementation of the MrpArgumentOption class
 *     for integer values.
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpIntOption extends MrpArgumentOption {
    
    /**
     *     default constructor
     *
     */
    public MrpIntOption() {
        super();
    }
    
    /**
     *     full constructor
     *
     * @param _description      cli: option description
     * @param _shortOption      cli: short option (name)
     * @param _longOption       cli: long option (name)
     * @param _argumentName     cli: argument name
     * @param _iniSection       ini: the section name for the value in he ini file
     * @param _iniName          ini: the key for the value in the ini file
     */
    public MrpIntOption(String _description, String _shortOption, String _longOption,
                        String _argumentName, String _iniSection, String _iniName) {
        super(_description, _shortOption, _longOption, _argumentName,
                        _iniSection, _iniName);
    }
    
    /**
     *     This is the method that sets the actual attribute
     *     value for a specific config value (option)
     *
     * @param _value
     */
    @Override
    public void setValue(String _value) {
        this.value = _value;
    }
    
    /**
     *     This is the method that sets the actual attribute
     *     value for a specific config value (option)
     *
     * @param _value
     */
    public void setValue(int _value) {
        this.value = Integer.toString(_value);
    }
    
    /** 
     *     check if a correct integer value was specified for this.option;
     *     otherwise @throw an IllegalArgumentException
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
                Integer.parseInt(this.value); 
            } catch(NumberFormatException exc) { 
                    throw new IllegalArgumentException("Option: " + this.description + 
                            ": " + this.value + "  is not an integer value");
            }
        }
    }
    
    /**
     *     Return this.option value as integer
     *
     * @return  this.option value as an integer
     */
    public int getIntValue() {
        if (this.value.isEmpty())
            return (int)0;
        else
            return Integer.parseInt(this.value);
    }
}
