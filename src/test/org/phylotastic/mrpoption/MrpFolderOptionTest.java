package org.phylotastic.mrpoption;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ...
 */
public class MrpFolderOptionTest {
    
    public MrpFolderOptionTest() {
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
    
    private boolean createFolder(String _path) throws IOException {
        File dir = new File(_path);
        if (dir.exists())
            if (dir.isDirectory()) return true;
            else throw new IOException("Dir: " +_path+" is not a directory");
        else return dir.mkdir();
    }
    
    private boolean removeFolder(String _path) throws IOException {
        File dir = new File(_path);
        if (dir.exists())
            if (dir.isDirectory()) return dir.delete();
            else throw new IOException("Dir: " +_path+" is not a directory");
        else return true;
    }
    
    private boolean existsFolder(String _path) throws IOException {
        File dir = new File(_path);
        if (dir.exists())
            if (dir.isDirectory()) return true;
            else throw new IOException("Dir: " +_path+" is not a directory");
        else return false;
    }

    /**
     * Test of set- and getValue method, of class MrpFolderOption.
     */
    @Test
    public void testValue() {
        System.out.println();
        System.out.println("* MrpFolderOptionTest: testValue()");
        String _description = "mapreduce temp dir";
        String _shortOption = "t";
        String _longOption  = "temp";
        String _argumentName = "folder path";
        String _iniSection = "main";
        String _iniName = "temp";
        Boolean mustExist = false;
        Boolean mustRemove = true;
        MrpFolderOption instance = new MrpFolderOption(mustExist, mustRemove);
        assertNotNull(instance);
        instance.setProperties(_description, _shortOption, _longOption, _argumentName, _iniSection, _iniName);
        String _string = "Option: -t, -temp / [main]temp: folder path; mapreduce temp dir";
        String _value = "";
        System.out.println("  expResult = " + _string);
        System.out.println("  result    = " + instance.toString());
        assertEquals(_string, instance.toString());
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        String _testFolder = "unitTest"+File.separator+"temp";
        instance.setValue(_testFolder);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFolder);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFolder, instance.getValue());
    }

    /**
     * Test of checkFile method, of class MrpFolderOption.
     * @throws java.lang.Exception
     */
    @Test
    public void testCheckFolder1() throws Exception {
        System.out.println();
        System.out.println("* MrpFolderOptionTest: testCheckFolder1()");
        String _description = "mapreduce temp dir";
        String _shortOption = "t";
        String _longOption  = "temp";
        String _argumentName = "folder path";
        String _iniSection = "main";
        String _iniName = "temp";
        Boolean mustExist = false;
        Boolean mustRemove = true;
        MrpFolderOption instance = new MrpFolderOption(mustExist, mustRemove);
        assertNotNull(instance);
        instance.setProperties(_description, _shortOption, _longOption, _argumentName, _iniSection, _iniName);
        String _string = "Option: -t, -temp / [main]temp: folder path; mapreduce temp dir";
        String _value = "";
        System.out.println("  expResult = " + _string);
        System.out.println("  result    = " + instance.toString());
        assertEquals(_string, instance.toString());
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        String _testFolder = "unitTest"+File.separator+"temp";
        instance.setValue(_testFolder);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFolder);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFolder, instance.getValue());
        if (createFolder(_testFolder)) {
            instance.checkFolder();
            if (existsFolder(_testFolder))
                fail("Folder" + _testFolder + " should have been deleted");
            else
                System.out.println("  result    = OK");
        }
        else
            fail("Can't create test folder" + _testFolder);
    }

    /**
     * Test of checkFile method, of class MrpFolderOption.
     * @throws java.lang.Exception
     */
    @Test (expected = IllegalArgumentException.class)
    public void testCheckFolder2() throws Exception {
        System.out.println();
        System.out.println("* MrpFolderOptionTest: testCheckFolder2()");
        String _description = "mapreduce temp dir";
        String _shortOption = "t";
        String _longOption  = "temp";
        String _argumentName = "folder path";
        String _iniSection = "main";
        String _iniName = "temp";
        Boolean mustExist = false;
        Boolean mustRemove = true;
        MrpFolderOption instance = new MrpFolderOption(mustExist, mustRemove);
        assertNotNull(instance);
        instance.setProperties(_description, _shortOption, _longOption, _argumentName, _iniSection, _iniName);
        String _string = "Option: -t, -temp / [main]temp: folder path; mapreduce temp dir";
        String _value = "";
        System.out.println("  expResult = " + _string);
        System.out.println("  result    = " + instance.toString());
        assertEquals(_string, instance.toString());
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        instance.checkFolder();
        System.out.println("  result    = Not OK");
    }

    /**
     * Test of checkFile method, of class MrpFolderOption.
     * @throws java.lang.Exception
     */
    @Test (expected = IllegalArgumentException.class)
    public void testCheckFolder3() throws Exception {
        System.out.println();
        System.out.println("* MrpFolderOptionTest: testCheckFolder3()");
        String _description = "mapreduce temp dir";
        String _shortOption = "t";
        String _longOption  = "temp";
        String _argumentName = "folder path";
        String _iniSection = "main";
        String _iniName = "temp";
        Boolean mustExist = false;
        Boolean mustRemove = true;
        MrpFolderOption instance = new MrpFolderOption(mustExist, mustRemove);
        assertNotNull(instance);
        instance.setProperties(_description, _shortOption, _longOption, _argumentName, _iniSection, _iniName);
        String _string = "Option: -t, -temp / [main]temp: folder path; mapreduce temp dir";
        String _value = "";
        System.out.println("  expResult = " + _string);
        System.out.println("  result    = " + instance.toString());
        assertEquals(_string, instance.toString());
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        String _testFolder = "unitTest"+File.separator+"temp";
        instance.setValue(_testFolder);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFolder);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFolder, instance.getValue());
        if (createFile(_testFolder)) {
            instance.checkFolder();
            System.out.println("  result    = Not OK");
        } else
            fail("Can't create test file" + _testFolder);
    }

    /**
     * Test of set- and getFile method, of class MrpFileOption.
     */
    @Test
    public void testGetFolder() {
        System.out.println();
        System.out.println("* MrpFileOptionTest: testGetFolder()");
        String _description = "mapreduce temp dir";
        String _shortOption = "t";
        String _longOption  = "temp";
        String _argumentName = "folder path";
        String _iniSection = "main";
        String _iniName = "temp";
        Boolean mustExist = false;
        Boolean mustRemove = true;
        MrpFolderOption instance = new MrpFolderOption(mustExist, mustRemove);
        assertNotNull(instance);
        instance.setProperties(_description, _shortOption, _longOption, _argumentName, _iniSection, _iniName);
        String _string = "Option: -t, -temp / [main]temp: folder path; mapreduce temp dir";
        String _value = "";
        System.out.println("  expResult = " + _string);
        System.out.println("  result    = " + instance.toString());
        assertEquals(_string, instance.toString());
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        String _testFolder = "unitTest"+File.separator+"temp";
        instance.setValue(_testFolder);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFolder);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFolder, instance.getValue());
        File _folder = new File(_testFolder);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFolder);
        System.out.println("  result    = " + instance.getFolder());
        assertEquals(_folder, instance.getFolder());
    }

    /**
     * Test of getPath method, of class MrpFolderOption.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetPath() throws Exception {
        System.out.println();
        System.out.println("* MrpFolderOptionTest: testGetPath()");
        String _description = "mapreduce temp dir";
        String _shortOption = "t";
        String _longOption  = "temp";
        String _argumentName = "folder path";
        String _iniSection = "main";
        String _iniName = "temp";
        Boolean mustExist = false;
        Boolean mustRemove = true;
        MrpFolderOption instance = new MrpFolderOption(mustExist, mustRemove);
        assertNotNull(instance);
        instance.setProperties(_description, _shortOption, _longOption, _argumentName, _iniSection, _iniName);
        String _string = "Option: -t, -temp / [main]temp: folder path; mapreduce temp dir";
        String _value = "";
        System.out.println("  expResult = " + _string);
        System.out.println("  result    = " + instance.toString());
        assertEquals(_string, instance.toString());
        System.out.println("  -");
        System.out.println("  expResult = " + _value);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_value, instance.getValue());
        String _testFolder = "unitTest"+File.separator+"temp";
        instance.setValue(_testFolder);
        System.out.println("  -");
        System.out.println("  expResult = " + _testFolder);
        System.out.println("  result    = " + instance.getValue());
        assertEquals(_testFolder, instance.getValue());
        File _folder = new File(_testFolder);     
        String _path = _folder.getAbsolutePath() + File.separator;
        System.out.println("  -");
        System.out.println("  expResult = " + _path);
        System.out.println("  result    = " + instance.getPath());
        assertEquals(_path, instance.getPath());
    }
    
}
