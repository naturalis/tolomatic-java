package org.phylotastic.mrpoption;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ...
 */
public class MrpUrlOptionTest {
    
    public MrpUrlOptionTest() {
    }

    /**
     * Test of set- and getValue methods, of class MrpUrlOption.
     */
    @Test
    public void testValue() {
        System.out.println();
        System.out.println("* MrpUrlOptionTest: testValue()");
        String _description = "url for tree data";
        String _shortOption = "u";
        String _longOption  = "url";
        String _argumentName = "url tree";
        String _iniSection = "data";
        String _iniName = "tree";
        MrpUrlOption instance = new MrpUrlOption(
                _description, _shortOption, _longOption, _argumentName, _iniSection, _iniName);
        assertNotNull(instance);
        String _value = "";
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        _value = "http://www.evoio.org/wg/evoio/images/9/9c/Phylomatictree.nex";
        instance.setValue(_value);
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
    }

    /**
     * Test of checkValue method, of class MrpUrlOption.
     */
    @Test
    public void testCheckValue1() {
        System.out.println();
        System.out.println("* MrpUrlOptionTest: testCheckValue1()");
        MrpUrlOption instance = new MrpUrlOption(
                "description", "short", "long","argument", "section", "key");
        assertNotNull(instance);
        String _value = "http://www.evoio.org/wg/evoio/images/9/9c/Phylomatictree.nex";
        instance.setValue(_value);
        instance.checkValue();
        System.out.println("  result    = OK");
    }

    /**
     * Test of checkValue method, of class MrpUrlOption.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckValue2() {
        System.out.println();
        System.out.println("* MrpUrlOptionTest: testCheckValue2()");
        MrpUrlOption instance = new MrpUrlOption(
                "description", "short", "long","argument", "section", "key");
        assertNotNull(instance);
        instance.setValue("A");
        instance.checkValue();
        System.out.println("  result    = Not OK");
    }
    
}
