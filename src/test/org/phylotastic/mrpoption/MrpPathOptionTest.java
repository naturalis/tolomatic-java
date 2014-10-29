package org.phylotastic.mrpoption;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ...
 */
public class MrpPathOptionTest {
    
    public MrpPathOptionTest() {
    }

    /**
     * Test of set- and getValue methods, of class MrpPathOption.
     */
    @Test
    public void testValue() {
        System.out.println();
        System.out.println("* MrpPathOptionTest: testValue()");
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        String _iniSection = "main";
        String _iniName = "input";
        MrpPathOption instance = new MrpPathOption();
        assertNotNull(instance);
        instance.setProperties(_description, _shortOption, _longOption, _argumentName, _iniSection, _iniName);
        String _string = "Option: -i, -input / [main]input: file path; taxon input file";
        String _value = "";
        System.out.println("  expResult = " + _string);
        System.out.println("  result    = " + instance.toString());
        assertEquals(_string, instance.toString());
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        _value = "input.txt";
        instance.setValue(_value);
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
    }

    /**
     * Test of getPath method, of class MrpPathOption.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetPath() throws Exception {
        System.out.println();
        System.out.println("* MrpPathOptionTest: testGetPath()");
        String userDir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");
        String serverPath = separator+"user"+separator+"userName"+separator+"case6"+separator+"input.txt";
        String localPath = "case6"+separator+"input.txt";
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        MrpPathOption instance = new MrpPathOption();
        assertNotNull(instance);
        instance.setProperties(_description, _shortOption, _longOption, _argumentName);
        String _string = "Option: -i, -input: file path; taxon input file";
        String _value = "";
        System.out.println("  expResult = " + _string);
        System.out.println("  result    = " + instance.toString());
        assertEquals(_string, instance.toString());
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        instance.setValue(serverPath);
        System.out.println("  -");
        System.out.println("  expResult = " + serverPath);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(serverPath, instance.getValue());
        System.out.println("  -");
        System.out.println("  expResult = " + serverPath);
        System.out.println("  result    = " + instance.getPath());
        assertEquals(serverPath, instance.getPath());
        instance.setValue(localPath);
        System.out.println("  -");
        System.out.println("  expResult = " + localPath);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(localPath, instance.getValue());
        String expResult = userDir+separator+localPath;
        System.out.println("  -");
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + instance.getPath());
        assertEquals(expResult, instance.getPath());
    }

    /**
     * Test of isLocal method, of class MrpPathOption.
     */
    @Test
    public void testIsLocal() {
        System.out.println();
        System.out.println("* MrpPathOptionTest: testIsLocal()");
        String userDir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");
        String serverPath = separator+"user"+separator+"userName"+separator+"case6"+separator+"input.txt";
        String localPath = "case6"+separator+"input.txt";
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        MrpPathOption instance = new MrpPathOption();
        assertNotNull(instance);
        instance.setProperties(_description, _shortOption, _longOption, _argumentName);
        String _string = "Option: -i, -input: file path; taxon input file";
        String _value = "";
        System.out.println("  expResult = " + _string);
        System.out.println("  result    = " + instance.toString());
        assertEquals(_string, instance.toString());
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        instance.setValue(serverPath);
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.isLocal());
        assertFalse(instance.isLocal());
        instance.setValue(localPath);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.isLocal());
        assertTrue(instance.isLocal());
    }
    
}
