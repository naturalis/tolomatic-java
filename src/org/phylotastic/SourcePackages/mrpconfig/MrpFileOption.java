package org.phylotastic.SourcePackages.mrpconfig;

import java.io.*;

public class MrpFileOption extends MrpArgumentOption {
    /**
     * An implementation of the MrpArgumentOption class
     * specialised in file name options. The constructor
     * requires to indicate if it is mandatory that the
     * specified file exist and also íf a file that exists
     * should be removed. The first mostly is the case for
     * input files and the latter can be the case for
     * certain test and/or output files.
     */
    protected File file;                        // the java.io.File object for the file
    protected Boolean mustExist;                // the indicator if the file must exist
    protected Boolean mustRemove;               // the indicator: remove if exists
    
    /**
     * Constructor
     *
     * @param _mustExist
     * @param _mustRemove
     */
    public MrpFileOption(Boolean _mustExist, Boolean _mustRemove){
        super();
        this.file = null;
        this.mustExist = _mustExist;
        this.mustRemove = _mustRemove;
    }
    
    /**
     * This is the method that sets the actual value for
     * the specific file but not after first carrying out
     * the necessary checks, like if it exists and if it
     * should be removed, etc.
     *
     * @param _name
     * @throws java.io.FileNotFoundException
     * @throws IllegalArgumentException
     */
    @Override
    public void setValue(String _name) throws FileNotFoundException, IllegalArgumentException {
        MrpOption.debugLogger.debug("MrpFileOption: setting file: " + this.description + " = " + _name );
        // check if the specified file _name is empty
        if (_name.isEmpty())
            throw new IllegalArgumentException("Name of file: " + this.description + "  has not been specified");
        else  {
            // create a java.io.File object for the file
            this.file = new File(_name);
            // check if the file should be an existing file
            if (this.mustExist == true) {
                // it should be; check if it exists
                if (!this.file.exists())
                    throw new FileNotFoundException("File not found: " + this.file.getPath() + "(" + this.description + ")");
                else {
                    // file exists; check if what was specified is a file (and not e.g. a folder)
                    if (!this.file.isFile())
                        throw new FileNotFoundException("File is not a file: " + this.file.getPath() + "(" + this.description + ")");
                    else
                        // this is a valid file
                        this.value = _name;
                }
            } else {
                // file does not need to exist
                this.value = _name;
                if (this.mustRemove == true) {
                    // file should not yet exist; check that
                    if (this.file.exists())
                        // file already exists; delete it.
                        this.file.delete();
                }
            }
        }
    }
    
    /** 
     * check if a correct file name was specified in the options;
     * in that case this should have a value and a file object
    */
    public void checkFile() {
        if (this.value.isEmpty() || this.file == null)
            throw new IllegalArgumentException("No correct file has been specified for: " + this.description);
        else {
        }
    }  
    
    /**
     * Return the (adress of the) file object
     *
     * @return
     */
    public File getFile() {
        return this.file;
    }
    
    /**
     * Return the full (canonical) path of the file
     *
     * @return
     * @throws IOException
     */
    public String getPath() throws IOException {
        return this.file.getCanonicalPath();
    }
}
