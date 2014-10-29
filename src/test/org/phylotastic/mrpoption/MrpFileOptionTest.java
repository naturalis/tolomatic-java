package org.phylotastic.mrpoption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ...
 */
public class MrpFileOptionTest {
    
    public MrpFileOptionTest() {
    }
    
    // helper methods
    private boolean createFile(String _path) throws IOException {
        File file = new File(_path);
        if (file.exists()) {
            //System.out.println("- File: " + file.getAbsolutePath() + " exists");
            if (file.isFile()) {
                //System.out.println("- File: " + file.getAbsolutePath() + " is a file");
                return true;
            } else { 
                //System.out.println("- File: " + file.getAbsolutePath() + " is not a file");
                throw new IOException("File: " +_path+" is not a file");
            }
        } else {
            //System.out.println("- File: " + file.getAbsolutePath() + " does not exist");
            return file.createNewFile();
        }
    }
    
    private boolean removeFile(String _path) throws IOException {
        File file = new File(_path);
        if (file.exists())
            if (file.isFile()) return file.delete();
            else throw new IOException("File: " +_path+" is not a file");
        else return true;
    }
    
    private boolean createDir(String _path) throws IOException {
        File dir = new File(_path);
        if (dir.exists())
            if (dir.isDirectory()) return true;
            else throw new IOException("Dir: " +_path+" is not a directory");
        else return dir.mkdir();
    }
    
    private boolean removeDir(String _path) throws IOException {
        File dir = new File(_path);
        if (dir.exists())
            if (dir.isDirectory()) return dir.delete();
            else throw new IOException("Dir: " +_path+" is not a directory");
        else return true;
    }

    /**
     * Test of set- and getValue method, of class MrpFileOption.
     */
    @Test
    public void testValue() {
        System.out.println();
        System.out.println("* MrpFileOptionTest: testValue()");
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        String _iniSection = "main";
        String _iniName = "input";
        Boolean mustExist = true;
        Boolean mustRemove = false;
        MrpFileOption instance = new MrpFileOption(mustExist, mustRemove);
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
        String _testFile = "unitTest"+File.separator+"input.txt";
        instance.setValue(_testFile);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFile);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFile, instance.getValue());
    }

    /**
     * Test of checkFile method, of class MrpFileOption.
     * @throws java.lang.Exception
     */
    @Test
    public void testCheckFile1() throws Exception {
        System.out.println();
        System.out.println("* MrpFileOptionTest: testCheckFile1()");
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        String _iniSection = "main";
        String _iniName = "input";
        Boolean mustExist = true;
        Boolean mustRemove = false;
        MrpFileOption instance = new MrpFileOption(mustExist, mustRemove);
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
        String _testFile = "unitTest"+File.separator+"test.txt";
        instance.setValue(_testFile);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFile);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFile, instance.getValue());
        if (createFile(_testFile)) {
            instance.checkFile();
            System.out.println("  result    = OK");
        } else
            fail("Can't create test file" + _testFile);
    }

    /**
     * Test of checkFile method, of class MrpFileOption.
     * @throws java.lang.Exception
     */
    @Test (expected = IllegalArgumentException.class)
    public void testCheckFile2() throws Exception {
        System.out.println();
        System.out.println("* MrpFileOptionTest: testCheckFile2()");
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        String _iniSection = "main";
        String _iniName = "input";
        Boolean mustExist = true;
        Boolean mustRemove = false;
        MrpFileOption instance = new MrpFileOption(mustExist, mustRemove);
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
        instance.checkFile();
        System.out.println("  result    = Not OK");
    }

    /**
     * Test of checkFile method, of class MrpFileOption.
     * @throws java.lang.Exception
     */
    @Test (expected = FileNotFoundException.class)
    public void testCheckFile3() throws Exception {
        System.out.println();
        System.out.println("* MrpFileOptionTest: testCheckFile3()");
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        String _iniSection = "main";
        String _iniName = "input";
        Boolean mustExist = true;
        Boolean mustRemove = false;
        MrpFileOption instance = new MrpFileOption(mustExist, mustRemove);
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
        String _testFile = "unitTest"+File.separator+"test.txt";
        instance.setValue(_testFile);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFile);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFile, instance.getValue());
        if (removeFile(_testFile)) {
            instance.checkFile();
            System.out.println("  result    = Not OK");
        } else
            fail("Can't delete test file" + _testFile);
    }

    /**
     * Test of checkFile method, of class MrpFileOption.
     * @throws java.lang.Exception
     */
    @Test (expected = FileNotFoundException.class)
    public void testCheckFile4() throws Exception {
        System.out.println();
        System.out.println("* MrpFileOptionTest: testCheckFile4()");
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        String _iniSection = "main";
        String _iniName = "input";
        Boolean mustExist = true;
        Boolean mustRemove = false;
        MrpFileOption instance = new MrpFileOption(mustExist, mustRemove);
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
        String _testFile = "unitTest"+File.separator+"test.dir";
        instance.setValue(_testFile);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFile);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFile, instance.getValue());
        if (createDir(_testFile)) {
            instance.checkFile();
            System.out.println("  result    = Not OK");
        } else
            fail("Can't create test directory" + _testFile);
    }

    /**
     * Test of set- and getFile method, of class MrpFileOption.
     */
    @Test
    public void testGetFile() {
        System.out.println();
        System.out.println("* MrpFileOptionTest: testGetFile()");
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        String _iniSection = "main";
        String _iniName = "input";
        Boolean mustExist = true;
        Boolean mustRemove = false;
        MrpFileOption instance = new MrpFileOption(mustExist, mustRemove);
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
        String _testFile = "unitTest"+File.separator+"input.txt";
        instance.setValue(_testFile);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFile);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFile, instance.getValue());
        File file = new File(_testFile);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFile);
        System.out.println("  result    = " + instance.getFile());
        assertEquals(file, instance.getFile());
    }

    /**
     * Test of set- and getPath method, of class MrpFileOption.
     */
    @Test
    public void testGetPath() {
        System.out.println();
        System.out.println("* MrpFileOptionTest: testGetPath()");
        String _description = "taxon input file";
        String _shortOption = "i";
        String _longOption  = "input";
        String _argumentName = "file path";
        String _iniSection = "main";
        String _iniName = "input";
        Boolean mustExist = true;
        Boolean mustRemove = false;
        MrpFileOption instance = new MrpFileOption(mustExist, mustRemove);
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
        String _testFile = "unitTest"+File.separator+"input.txt";
        instance.setValue(_testFile);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFile);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFile, instance.getValue());
        File _file = new File(_testFile);
        String _path = _file.getAbsolutePath();
        System.out.println("  -");
        System.out.println("  expResult = " + _path);
        System.out.println("  result    = " + instance.getPath());
        assertEquals(_path, instance.getPath());
    }
    
}
