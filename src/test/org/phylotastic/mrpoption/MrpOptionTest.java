package org.phylotastic.mrpoption;

import org.apache.commons.cli.*;
import org.apache.commons.cli.Options; // because ini4j also has getOptions method
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ...
 */
public class MrpOptionTest {
    
    public MrpOptionTest() {
    }

    /**
     * Test of constructor, of class MrpOption.
     */
    @Test
    public void testConstructor1() {
        System.out.println();
        System.out.println("* MrpOptionTest: testConstructor1()");
        MrpOption instance = new MrpOption();
        assertNotNull(instance);
        String result = instance.toString();
        String expResult = "Option: " + "; not specified";
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of constructor, of class MrpOption.
     */
    @Test
    public void testConstructor2() {
        System.out.println();
        System.out.println("* MrpOptionTest: testConstructor2()");
        MrpOption instance = new MrpOption("description", "short", "longOption");
        assertNotNull(instance);
        String result = instance.toString();
        String expResult = "Option: -short, -longOption; description";
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of setProperties method, of class MrpOption.
     */
    @Test
    public void testSetProperties() {
        System.out.println();
        System.out.println("* MrpOptionTest: testSetProperties()");
        MrpOption instance = new MrpOption();
        assertNotNull(instance);
        String result = instance.toString();
        String expResult = "Option: " + "; not specified";
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        instance.setProperties("description", "short", "longOption");
        result = instance.toString();
        expResult = "Option: -short, -longOption; description";
        System.out.println("  -");
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getOption method, of class MrpOption.
     */
    @Test
    public void testGetOption() {
        System.out.println();
        System.out.println("* MrpOptionTest: testGetOption()");
        String description = "don't use environment variables";
        String shortOption = "n";
        String longOption = "noEnv";
        
        OptionBuilder.withDescription(description);
        OptionBuilder.withLongOpt(longOption);
        Option cliOption = OptionBuilder.create(shortOption);
        
        MrpOption instance = new MrpOption(description, shortOption, longOption);
        assertNotNull(instance);
        String result = instance.toString();
        String expResult = "Option: -n, -noEnv; don't use environment variables";
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        Option mrpOption = instance.getOption();
        assertNotNull(mrpOption);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + cliOption.equals(mrpOption));
        assertEquals(cliOption, mrpOption);
    }

    /**
     * Test of setOption method, of class MrpOption.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetOption() throws Exception {
        System.out.println();
        System.out.println("* MrpOptionTest: testSetOption()");
        // create an mrp option
        String description = "don't use environment variables";
        String shortOption = "n";
        String longOption = "noEnv";
        MrpOption instance = new MrpOption(description, shortOption, longOption);
        // create a cli Options object
        Options options = new Options();
        options.addOption(instance.getOption());
        String expResult = "";
        
        String[] args = new String[1];
        args[0] = "-n";
        // create cli command line parser
        CommandLineParser parser = new BasicParser();
        assertNotNull(parser);
        try {
            // parse the command line arguments
            CommandLine cmdLine = parser.parse( options, args );
            // process the command line
            if(cmdLine.hasOption(shortOption))
                expResult = shortOption;
            instance.setOption(cmdLine);
            String mrpResult = instance.getValue();
            System.out.println("  expResult = " + true);
            System.out.println("  result    = " + expResult.equals(mrpResult));
            assertEquals(expResult, mrpResult);
        }
        catch (Exception exp) {
            System.out.println( "MrpOptionTest: Command line error: " + exp.getMessage() );
            throw exp;
        }
    }

    /**
     * Test of setValue method, of class MrpOption.
     */
    @Test
    public void testSetValue() {
        System.out.println();
        System.out.println("* MrpOptionTest: testSetValue()");
        // create an mrp option
        String description = "don't use environment variables";
        String shortOption = "n";
        String longOption = "noEnv";
        MrpOption instance = new MrpOption(description, shortOption, longOption);
        instance.setValue(shortOption);
        String result = instance.getValue();
        String expResult = shortOption;
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of checkValue method, of class MrpOption.
     */
    @Test
    public void testCheckValue1() {
        System.out.println();
        System.out.println("* MrpOptionTest: testCheckValue1()");
        // create an mrp option
        String description = "don't use environment variables";
        String shortOption = "n";
        String longOption = "noEnv";
        MrpOption instance = new MrpOption(description, shortOption, longOption);
        instance.setValue("n");
        assertEquals("n", instance.getValue());
        instance.checkValue();
        System.out.println("  result    = OK");
    }

    /**
     * Test of checkValue method, of class MrpOption.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckValue2() {
        System.out.println();
        System.out.println("* MrpOptionTest: testCheckValue2()");
        // create an mrp option
        String description = "don't use environment variables";
        String shortOption = "n";
        String longOption = "noEnv";
        MrpOption instance = new MrpOption(description, shortOption, longOption);
        instance.checkValue();
        System.out.println("  result    = not OK!!");
    }

    /**
     * Test of getValue method, of class MrpOption.
     */
    @Test
    public void testGetValue() {
        System.out.println();
        System.out.println("* MrpOptionTest: testGetValue()");
        // create an mrp option
        String description = "don't use environment variables";
        String shortOption = "n";
        String longOption = "noEnv";
        MrpOption instance = new MrpOption(description, shortOption, longOption);
        instance.setValue(shortOption);
        String result = instance.getValue();
        String expResult = shortOption;
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        instance.setValue("");
        result = instance.getValue();
        expResult = "";
        System.out.println("  -");
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of hasValue method, of class MrpOption.
     */
    @Test
    public void testHasValue() {
        System.out.println();
        System.out.println("* MrpOptionTest: testHasValue()");
        // create an mrp option
        String description = "don't use environment variables";
        String shortOption = "n";
        String longOption = "noEnv";
        MrpOption instance = new MrpOption(description, shortOption, longOption);
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.hasValue());
        assertFalse(instance.hasValue());
        instance.setValue("n");
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.hasValue());
        assertTrue(instance.hasValue());
        instance.setValue("");
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.hasValue());
        assertFalse(instance.hasValue());
    }

    /**
     * Test of toString method, of class MrpOption.
     */
    @Test
    public void testToString() {
        System.out.println();
        System.out.println("* MrpOptionTest: testSetProperties()");
        MrpOption instance = new MrpOption();
        assertNotNull(instance);
        String result = instance.toString();
        String expResult = "Option: " + "; not specified";
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        instance.setProperties("don't use environment variables", "n", "noEnv");
        result = instance.toString();
        expResult = "Option: -n, -noEnv; don't use environment variables";
        System.out.println("  -");
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }
    
}
