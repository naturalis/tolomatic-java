/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

package org.phylotastic.SourcePackages.mrptree;

import java.util.Locale;

public class MrpTree {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
         * Each segment of that path is emitted as node ID => taxon. For example, for tree
         * (((A,B)n3,C)n2,D)n1
         *  A    B
         *   \  /
         *   (n3)  C
         *     \  /
         *     (n2)  D
         *       \  /
         *       (n1)
         */
        
        // => to force a period as the decimal separator instead of a comma.
        Locale.setDefault(new Locale("en", "US"));
        
        Tree tree = new Tree();
        
        TreeNode nodeA = tree.addNode((int)10, "A", (double)0.1);
        TreeNode nodeB = tree.addNode((int)11, "B", (double)0.2);
        TreeNode nodeN3 = tree.addNode((int)3, "", (double)0.1);
        tree.setChild(nodeN3, nodeA);
        tree.setChild(nodeN3, nodeB);
        
        TreeNode nodeC = tree.addNode((int)12, "C", (double)0.3);
        TreeNode nodeN2 = tree.addNode((int)2, "", (double)0.2);
        tree.setChild(nodeN2, nodeN3);
        tree.setChild(nodeN2, nodeC);
        
        TreeNode nodeD = tree.addNode((int)13, "", (double)0.4);
        TreeNode nodeN1 = tree.addNode((int)1, "", (double)0.3);
        tree.setChild(nodeN1, nodeN2);
        tree.setChild(nodeN1, nodeD);
        
        System.out.println("node = ID,name:length");
        System.out.println("---------------------");
        System.out.println("A = " + nodeA.toString());
        System.out.println("B = " + nodeB.toString());
        System.out.println("N3 = " + nodeN3.toString());
        System.out.println("C = " + nodeC.toString());
        System.out.println("N2 = " + nodeN2.toString());
        System.out.println("D = " + nodeD.toString());
        System.out.println("N1 = " + nodeN3.toString());
        
        tree.rootTheTree();
        System.out.println("");
        System.out.println("Root = " + tree.getRoot().toString());
        //System.out.println("");
        
        String newickString = tree.toNewick();
        System.out.println("");
        System.out.println("Resulting Newick is:");
        System.out.println("--------------------");
        System.out.println(newickString);
    }
}
