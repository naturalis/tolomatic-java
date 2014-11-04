package org.phylotastic.mrpoption;

import java.io.*;
import static org.apache.commons.io.FileUtils.deleteDirectory;

/**
 *     Class MrpFolderOption
 * 
 *     An implementation of the MrpArgumentOption class
 *     specialised in folder name options. The constructor
 *     requires to indicate if it is mandatory that the
 *     specified folder exist and also íf a folder that exists
 *     should be removed. The first mostly is the case for
 *     input folders and the latter can be the case for
 *     certain test and/or output folders.
 * 
 *     The class can only be used for local file system locations,
 *     not for Hadoop filesystem paths
 * 
 *     The class addds 2 boolean values to the constructor:
 *     mustExist and mustRemove. MustExist indicates that the
 *     folder must exist; mustRemove indicates that the folder
 *     should be deleted if it exists.
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
public class MrpFolderOption extends MrpArgumentOption {

    /**
     *     indicator if the folder must exist
     */
    protected Boolean mustExist;

    /**
     *     indicator if folder must be removed if it exists
     */
    protected Boolean mustRemove;
    
    /**
     *     Constructor
     *
     * @param _mustExist   indication if the folder must exist (true)
     * @param _mustRemove  indication if the folder should be deleted (true)
     */
    public MrpFolderOption(Boolean _mustExist, Boolean _mustRemove){
        super();
        this.mustExist = _mustExist;
        this.mustRemove = _mustRemove;
    }
    
    /**
     *     This is the method that sets the actual value for
     *     the specific folder.
     *
     * @param _name     the folder path/name
     * @throws IllegalArgumentException
     */
    @Override
    public void setValue(String _name) {
        //MrpOption.debugLogger.debug("MrpFolderOption: setting folder: " + this.description + " = " + _name );
        this.value = _name;
    }
    
    /**
     *     check if a correct folder name was specified for this.options;
     *     in that case this should have a value and a file object
     * 
     * @throws java.io.FileNotFoundException
     * @throws IllegalArgumentException
     */
    public void checkFolder() throws IllegalArgumentException, FileNotFoundException, IOException {
        // check if the specified folder _name is empty
        if (this.value.isEmpty())
            throw new IllegalArgumentException("Name of folder: " + this.description + "  has not been specified");
        else  {
            // create a java.io.File object for the folder
            File folder = new File(this.value);
            // check if the folder should be an existing folder
            if (this.mustExist == true) {
                // it should be; check if it exists
                if (!folder.exists())
                    throw new FileNotFoundException("Folder not found: " + folder.getPath() + "(" + this.description + ")");
                else {
                    // file exists; check if what was specified is a folder (and not e.g. a file)
                    if (!folder.isDirectory())
                        throw new FileNotFoundException("Folder is not a folder: " + folder.getPath() + "(" + this.description + ")");
                    else {
                        // this is a valid file
                    }
                }
            } else {
                // folder does not need to exist
                if (this.mustRemove == true) {
                    // folder should not yet exist; check that
                    if (folder.exists())
                        // folder already exists; delete it.
                        deleteDirectory(folder);
                }
            }
        }
    }
    
    /**
     *     Return the (adress of the) file object for the folder
     *
     * @return      a (local) java.io.File object for this.folder
     */
    public File getFolder() {
        return new File(this.value);
    }
    
    /**
     *     Return the full (absolute) path of the folder
     *
     * @return  the full (absolute) path of the folder
     * @throws IOException
     */
    public String getPath() throws IOException {
        File folder = new File(this.value);
        return folder.getAbsolutePath() + File.separator;
    }
}
