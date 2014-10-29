package org.phylotastic.mrpoption;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ...
 */
public class MrpIntOptionTest {
    
    public MrpIntOptionTest() {
    }

    /**
     * Test of set- and getValue methods, of class MrpIntOption.
     */
    @Test
    public void testValue() {
        System.out.println();
        System.out.println("* MrpIntOptionTest: testValue()");
        String _description = "hashDepth taxonfiles";
        String _shortOption = "h";
        String _longOption  = "hashDepth";
        String _argumentName = "int depth";
        String _iniSection = "data";
        String _iniName = "hashDepth";
        MrpIntOption instance = new MrpIntOption(
                _description, _shortOption, _longOption, _argumentName, _iniSection, _iniName);
        assertNotNull(instance);
        String _value = "";
        int _intValue = 0;
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        System.out.println("  -");
        System.out.println("  expResult = " + _intValue);
        System.out.println("  result    = " + instance.getIntValue());
        assertEquals(_intValue, instance.getIntValue());
        _value = "1";
        _intValue = 1;
        instance.setValue(_value);
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        System.out.println("  -");
        System.out.println("  expResult = " + _intValue);
        System.out.println("  result    = " + instance.getIntValue());
        assertEquals(_intValue, instance.getIntValue());
        _value = "2";
        _intValue = 2;
        instance.setValue(_value);
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        System.out.println("  -");
        System.out.println("  expResult = " + _intValue);
        System.out.println("  result    = " + instance.getIntValue());
        assertEquals(_intValue, instance.getIntValue());
    }

    /**
     * Test of checkValue method, of class MrpIntOption.
     */
    @Test
    public void testCheckValue1() {
        System.out.println();
        System.out.println("* MrpIntOptionTest: testCheckValue1()");
        MrpIntOption instance = new MrpIntOption(
                "description", "short", "long","argument", "section", "key");
        assertNotNull(instance);
        instance.setValue("1");
        instance.checkValue();
        System.out.println("  result    = OK");
    }

    /**
     * Test of checkValue method, of class MrpIOption.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckValue2() {
        System.out.println();
        System.out.println("* MrpIntOptionTest: testCheckValue2()");
        MrpIntOption instance = new MrpIntOption(
                "description", "short", "long","argument", "section", "key");
        assertNotNull(instance);
        instance.setValue("A");
        instance.checkValue();
        System.out.println("  result    = Not OK");
    }
    
}
