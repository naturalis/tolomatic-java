package org.phylotastic.mrpoption;

import java.io.File;
import java.io.IOException;

/**
 *     Class MrpPathOption
 * 
 *     An implementation of the MrpArgumentOption class
 *     specialised in file and foldername options of items
 *     that can be either on the local file system or on
 *     the hadoop file system (hfs).
 * 
 *     If the name starts with "/user/" the location is
 *     assumed to be on the (hfs) server and the name
 *     is returned "as is"
 *     In all other cases the location is assumed to be on the
 *     local file system and the name is returned as it's
 *     absolute path. This is the path string after resolving
 *     it against the current directory - if it's relative -
 *     resulting in a fully qualified path.
 *     This last is done so that the location can be used in a
 *     hfs "Path" object without hfs mistaking it for a
 *     location on the server.
 * 
 *     Because of the nature of the options value, the class does
 *     not (yet?) provide methods for testing if the location exists
 *     and is of the right type (file or folder). This is up to the
 *     methods that use the pathOption.
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpPathOption extends MrpArgumentOption {    
    private static final String userPath = System.getProperty("file.separator")+
            "user"+System.getProperty("file.separator");
    
    /**
     *     Constructor
     *
     */
    public MrpPathOption(){
        super();
    }
    
    /**
     *     This is the method that sets the actual value for
     *     the specific file.
     *
     * @param _name     the file or folder path or name
     */
    @Override
    public void setValue(String _name) {
        //MrpOption.debugLogger.debug("MrpPathOption: setting path: " + this.description + " = " + _name );
        this.value = _name;
    }
    
    /**
     *     Return the path value.
     *     If it is a file on the server - starts with "/user/" -
     *     then return the value "as is"
     *     Otherwise it is a location on the local file system
     *     return it's full (absolute) path
     *
     * @return  the name or path of the file/folder as a string
     * @throws IOException
     */
    public String getPath() throws IOException {
        if (this.value.startsWith(userPath))
            // then it is a location on the server; return as is
            return this.value;
        else
            // it is a location on the local file system
            return new File(this.value).getAbsolutePath();
    }
    
    /**
     *     indicates if the file is a local one
     *     or one on a server
     *
     * @return
     */
    public Boolean isLocal() {
        return !this.value.startsWith(userPath);
    }
}
