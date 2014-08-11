/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

        import java.util.ArrayList;
        import java.util.List;
        import jebl.evolution.graphs.Node;
        import jebl.evolution.taxa.Taxon;
        import jebl.evolution.trees.RootedTree;
        import jebl.evolution.trees.SimpleTree;
        import jebl.evolution.trees.Utils;

/**
 * Given a single taxon name as argument, this method reads in a file whose name is
 * an encoded version of the taxon name. That file should contain one line: a tab-separate
 * list that describes the path, in pre-order indexed integers, from taxon to the root.
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

public class JeblTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SimpleTree jebleTree = new SimpleTree();

        // create JEBL tips
        Taxon taxonA = Taxon.getTaxon("A");
        Taxon taxonB = Taxon.getTaxon("B");
        Taxon taxonC = Taxon.getTaxon("C");
        Taxon taxonD = Taxon.getTaxon("D");

        Node nodeA = jebleTree.createExternalNode(taxonA);
        Node nodeB = jebleTree.createExternalNode(taxonB);
        Node nodeC = jebleTree.createExternalNode(taxonC);
        Node nodeD = jebleTree.createExternalNode(taxonD);

        List<Node> childrenN3 = new ArrayList<>();
        //childrenN3.add(nodeA);
        //childrenN3.add(nodeB);
        Node nodeN3 = jebleTree.createInternalNode(childrenN3);
        jebleTree.addEdge(nodeN3, nodeA, (Double)0.1);
        jebleTree.addEdge(nodeN3, nodeB, (Double)0.2);

        List<Node> childrenN2 = new ArrayList<>();
        //childrenN2.add(nodeN3);
        //childrenN2.add(nodeC);
        Node nodeN2 = jebleTree.createInternalNode(childrenN2);
        jebleTree.addEdge(nodeN2, nodeN3, (Double)0.3);
        jebleTree.addEdge(nodeN2, nodeC, (Double)0.4);

        List<Node> childrenN1 = new ArrayList<>();
        //childrenN1.add(nodeN2);
        //childrenN1.add(nodeD);
        Node nodeN1 = jebleTree.createInternalNode(childrenN1);
        jebleTree.addEdge(nodeN1, nodeN2, (Double)0.5);
        jebleTree.addEdge(nodeN1, nodeD, (Double)0.6);

        RootedTree rootedJebleTree = Utils.rootTheTree(jebleTree);
        String newickTree = Utils.toNewick(rootedJebleTree);
        System.out.println(newickTree + ";");

    }

}