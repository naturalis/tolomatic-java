/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mapreducepruner;

import java.util.ArrayList;
import java.util.List;

/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

public class MrpTip {
    public int label;
    public String stringLabel;
    public TreeNode tip;
    public List<InternalTreeNode> ancestors;
    

    // constructor
    // ------------------------------------------------------------------------
    /** Construct a config object
    */
    public MrpTip() {
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
    public MrpTip(TreeNode _tip) {
        this.label = _tip.getLabel();
        this.stringLabel = String.valueOf(label);
        this.tip = _tip;
        this.ancestors = new ArrayList<>();
    }
    
}
