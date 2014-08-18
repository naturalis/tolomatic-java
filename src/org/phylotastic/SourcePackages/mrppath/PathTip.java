/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

package org.phylotastic.SourcePackages.mrppath;

import java.util.ArrayList;
import java.util.List;

public class PathTip {
    public int label;
    public String stringLabel;
    public PathNode tip;
    public List<PathNodeInternal> ancestors;
    

    // constructor
    // ------------------------------------------------------------------------
    /** Construct a config object
    */
    public PathTip() {
        this.label = 0;
        this.stringLabel = String.valueOf(label);
        this.tip = null;
        this.ancestors = new ArrayList<>();
    }
    

    // constructor
    // ------------------------------------------------------------------------
    /** Construct a config object
     * @param _tip
    */
    public PathTip(PathNode _tip) {
        this.label = _tip.getLabel();
        this.stringLabel = String.valueOf(label);
        this.tip = _tip;
        this.ancestors = new ArrayList<>();
    }
    
}
