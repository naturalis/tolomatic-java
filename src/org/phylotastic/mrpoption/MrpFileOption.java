package org.phylotastic.mrpoption;

import java.io.*;

/**
 *     Class MrpFileOption
 * 
 *     An implementation of the MrpArgumentOption class
 *     specialised in file name options. The constructor
 *     requires to indicate if it is mandatory that the
 *     specified file exist and also íf a file that exists
 *     should be removed. The first mostly is the case for
 *     input files and the latter can be the case for
 *     certain test and/or output files.
 * 
 *     The class can only be used for local file system locations,
 *     not for Hadoop filesystem paths
 * 
 *     The class addds 2 boolean values to the constructor:
 *     mustExist and mustRemove. MustExist indicates that the
 *     file must exist; mustRemove indicates that the file
 *     should be deleted might it exists.
 * 
 *     The class itself  does not make any assumptions as to where
 *     the folder might exist. If the user does not specify the
 *     full path for the folder, the class depends on java.io and
 *     the local file system to complete the folder path to it's full
 *     length. This allows the user to specify folder names/paths that
 *     are somehow relative to the current work directory (user.dir)
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpFileOption extends MrpArgumentOption {
    
    /**
     *     indicator if the file must exist
     */
    protected Boolean mustExist;

    /**
     *     indicator if file must be removed if it exists
     */
    protected Boolean mustRemove;
    
    /**
     *     Constructor
     *
     * @param _mustExist   indication if the folder must exist (true)
     * @param _mustRemove  indication if the folder should be deleted (true)
     */
    public MrpFileOption(Boolean _mustExist, Boolean _mustRemove){
        super();
        this.mustExist = _mustExist;
        this.mustRemove = _mustRemove;
    }
    
    /**
     *     This is the method that sets the actual value for
     *     the specific file.
     *
     * @param _name     the file path or name
     */
    @Override
    public void setValue(String _name) {
        //MrpOption.debugLogger.debug("MrpFileOption: setting file: " + this.description + " = " + _name );
        this.value = _name;
    }
    
    /** 
     *     check if a correct file name was specified in the options;
     *     in that case this should have a value and a file object
     * 
     * @throws java.io.FileNotFoundException
     * @throws IllegalArgumentException
    */
    public void checkFile() throws IllegalArgumentException, FileNotFoundException {
        // check if the specified file _name is empty
        if (this.value.isEmpty())
            throw new IllegalArgumentException("Name of file: " + this.description + "  has not been specified");
        else  {
            // create a java.io.File object for the file
            File file = new File(this.value);
            // check if the file should be an existing file
            if (this.mustExist == true) {
                // it should be; check if it exists
                if (!file.exists())
                    throw new FileNotFoundException("File not found: " + file.getPath() + "(" + this.description + ")");
                else {
                    // file exists; check if what was specified is a file (and not e.g. a folder)
                    if (!file.isFile())
                        throw new FileNotFoundException("File is not a file: " + file.getPath() + "(" + this.description + ")");
                    else {
                    }
                }
            } else {
                // file does not need to exist
                if (this.mustRemove == true) {
                    // file should not yet exist; check that
                    if (file.exists())
                        // file already exists; delete it.
                        file.delete();
                }
            }
        }
    }  
    
    /**
     *     Return the (adress of the) file object
     *
     * @return      a (local) java.io.File object for this.file
     */
    public File getFile() {
        return new File(this.value);
    }
    
    /**
     *     Return the full (absolute) path of the file
     *
     * @return  the full (absolute) path of the file
     */
    public String getPath() {
        File file = new File(this.value);
        return file.getAbsolutePath();
    }
}
