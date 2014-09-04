package org.phylotastic.SourcePackages.mrpconfig;

import java.io.*;
import static org.apache.commons.io.FileUtils.deleteDirectory;

public class MrpFolderOption extends MrpArgumentOption {
    /**
     * An implementation of the MrpArgumentOption class
     * specialised in folder name options. The constructor
     * requires to indicate if it is mandatory that the
     * specified folder exist and also íf a folder that exists
     * should be removed. The first mostly is the case for
     * input folders and the latter can be the case for
     * certain test and/or output folders.
     */
    protected File folder;                      // the java.io.File object for the file
    protected Boolean mustExist;                // the indicator if the file must exist
    protected Boolean mustRemove;               // the indicator: remove if exists
    
    /**
     * Constructor
     *
     * @param _mustExist
     * @param _mustRemove
     */
    public MrpFolderOption(Boolean _mustExist, Boolean _mustRemove){
        super();
        this.folder = null;
        this.mustExist = _mustExist;
        this.mustRemove = _mustRemove;
    }
    
    /**
     * This is the method that sets the actual value for
     * the specific folder but not after first carrying out
     * the necessary checks, like if it exists and if it
     * should be removed, etc.
     *
     * @param _name
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @throws IllegalArgumentException
     */
    @Override
    public void setValue(String _name) throws IllegalArgumentException, FileNotFoundException, IOException {
        MrpOption.debugLogger.debug("MrpFolderOption: setting folder: " + this.description + " = " + _name );
        // check if the specified file _name is empty
        if (_name.isEmpty())
            throw new IllegalArgumentException("Name of folder: " + this.description + "  has not been specified");
        else  {
            // create a java.io.File object for the folder
            this.folder = new File(_name);
            // check if the folder should be an existing folder
            if (this.mustExist == true) {
                // it should be; check if it exists
                if (!this.folder.exists())
                    throw new FileNotFoundException("Folder not found: " + this.folder.getPath() + "(" + this.description + ")");
                else {
                    // file exists; check if what was specified is a file (and not e.g. a file)
                    if (!this.folder.isDirectory())
                        throw new FileNotFoundException("Folder is not a folder: " + this.folder.getPath() + "(" + this.description + ")");
                    else
                        // this is a valid file
                        this.value = _name;
                }
            } else {
                // folder does not need to exist
                this.value = _name;
                if (this.mustRemove == true) {
                    // folder should not yet exist; check that
                    if (this.folder.exists())
                        // folder already exists; delete it.
                        deleteDirectory(this.folder);
                }
            }
        }
    }
    
    /** 
     * check if a correct folder name was specified in the options;
     * in that case this should have a value and a file object
    */
    public void checkFolder() {
        if (this.value.isEmpty() || this.folder == null)
            throw new IllegalArgumentException("No folder has been specified for: " + this.description);
        else {
        }
    }  
    
    /**
     * Return the (adress of the) file object for the folder
     *
     * @return
     */
    public File getFolder() {
        return this.folder;
    }
    
    /**
     * Return the full (canonical) path of the folder
     *
     * @return
     * @throws IOException
     */
    public String getPath() throws IOException {
        return this.folder.getCanonicalPath() + File.separator;
    }
}
