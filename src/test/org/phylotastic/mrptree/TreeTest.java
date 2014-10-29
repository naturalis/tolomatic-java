package org.phylotastic.mrptree;

import org.junit.Test;
import static org.junit.Assert.*;

import org.phylotastic.mrppath.*;

/**
 *
 * @author ...
 */
public class TreeTest {
    
    public TreeTest() {
    }

    /**
     * Test of constructor, of class Tree.
     */
    @Test
    public void testConstructor() {
        System.out.println();
        System.out.println("* TreeTest: testConstructor()");
        Tree instance = new Tree();
        assertNotNull(instance);
        assertNull(instance.getRoot());
    }

    /**
     * Test of addNode method, of class Tree.
     */
    @Test
    public void testAddNode_TreeNode() {
        System.out.println();
        System.out.println("* TreeTest: testAddNode_TreeNode()");
        TreeNode _node = new TreeNode(10, 0.0, "Elephant");
        Tree instance = new Tree();
        instance.addNode(_node);
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.hasNode(5));
        assertFalse(instance.hasNode(5));
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.hasNode(10));
        assertTrue(instance.hasNode(10));
    }

    /**
     * Test of addNode method, of class Tree.
     */
    @Test
    public void testAddNode_2args() {
        System.out.println();
        System.out.println("* TreeTest: testAddNode_2args()");
        Tree instance = new Tree();
        TreeNode node = instance.addNode(11, 0.7);
        assertNotNull(node);
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.hasNode(5));
        assertFalse(instance.hasNode(5));
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.hasNode(11));
        assertTrue(instance.hasNode(11));
        TreeNode check = new TreeNode(11, 0.7);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + node.equals(check));
        assertTrue(node.equals(check));
    }

    /**
     * Test of addNode method, of class Tree.
     */
    @Test
    public void testAddNode_3args() {
        System.out.println();
        System.out.println("* TreeTest: testAddNode_3args()");
        Tree instance = new Tree();
        TreeNode node = instance.addNode(11, 0.7, "Elephant");
        assertNotNull(node);
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.hasNode(5));
        assertFalse(instance.hasNode(5));
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.hasNode(11));
        assertTrue(instance.hasNode(11));
        TreeNode check = new TreeNode(11, 0.7, "Elephant");
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + node.equals(check));
        assertTrue(node.equals(check));
    }

    /**
     * Test of addNode method, of class Tree.
     */
    @Test
    public void testAddNode_PathNode() {
        System.out.println();
        System.out.println("* TreeTest: testAddNode_PathNode()");
        PathNode pathNode = new PathNode(12, 0.5, "Elephant");
        Tree instance = new Tree();
        TreeNode node = instance.addNode(pathNode);
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.hasNode(12));
        assertTrue(instance.hasNode(12));
    }

    /**
     * Test of addNode method, of class Tree.
     */
    @Test
    public void testAddNode_PathNodeInternal() {
        System.out.println();
        System.out.println("* TreeTest: testAddNode_PathNodeInternal()");
        PathNodeInternal pathNode = new PathNodeInternal(13, 0.3, 4);
        Tree instance = new Tree();
        TreeNode node = instance.addNode(pathNode);
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.hasNode(13));
        assertTrue(instance.hasNode(13));
    }

    /**
     * Test of hasNode method, of class Tree.
     */
    @Test
    public void testHasNode() {
        System.out.println();
        System.out.println("* TreeTest: testHasNode()");
        Tree instance = new Tree();
        TreeNode elephant = instance.addNode(10, 0.7, "Elephant");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.hasNode(10));
        assertTrue(instance.hasNode(10));
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.hasNode(11));
        assertFalse(instance.hasNode(11));
        TreeNode mastodon = instance.addNode(11, 0.7, "Mastodon");
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.hasNode(11));
        assertTrue(instance.hasNode(11));
    }

    /**
     * Test of getNode method, of class Tree.
     */
    @Test
    public void testGetNode() {
        System.out.println();
        System.out.println("* TreeTest: testGetNode()");
        Tree instance = new Tree();
        TreeNode elephant = instance.addNode(10, 0.7, "Elephant");
        TreeNode mastodon = instance.addNode(11, 0.7, "Mastodon");
        assertTrue(instance.hasNode(10));
        TreeNode check = instance.getNode(10);
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + elephant.equals(check));
        assertSame(elephant, check);
        TreeNode wrong = instance.getNode(77);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + (wrong == null));
        assertNull(wrong);
    }

    /**
     * Test of setChild method, of class Tree.
     */
    @Test
    public void testSetChild() {
        System.out.println();
        System.out.println("* TreeTest: testSetChild()");
        Tree instance = new Tree();
        TreeNode trunked = instance.addNode(9, 0.7);
        TreeNode elephant = instance.addNode(50, 0.7, "Elephant");
        TreeNode mastodon = instance.addNode(51, 0.7, "Mastodon");
        instance.setChild(trunked, elephant);
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + trunked.hasChildren());
        assertTrue(trunked.hasChildren());
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + elephant.hasParent());
        assertTrue(elephant.hasParent());
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + elephant.hasChildren());
        assertFalse(elephant.hasChildren());
        TreeNode parent = elephant.getParent();
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + trunked.equals(parent));
        assertSame(parent, trunked);
    }

    /**
     * Test of root methods, of class Tree.
     */
    @Test
    public void testRoot_1() {
        System.out.println();
        System.out.println("* TreeTest: testRoot_1()");
        Tree instance = new Tree();
        TreeNode instanceN1 = instance.addNode(10, 0.5);
        TreeNode instanceN2 = instance.addNode(20, 0.5);
        TreeNode instanceN3 = instance.addNode(30, 0.5);
        TreeNode instanceN4 = instance.addNode(40, 0.5);
        TreeNode instanceA  = instance.addNode(100, 0.5, "Agoracea");
        TreeNode instanceB  = instance.addNode(110, 0.5, "Bendricea");
        TreeNode instanceC  = instance.addNode(120, 0.5, "Catonacea");
        TreeNode instanceD  = instance.addNode(130, 0.5, "Draconacea");
        TreeNode instanceE  = instance.addNode(140, 0.5, "Elegoracea");
        
        instance.setChild(instanceN1, instanceN2);
        instance.setChild(instanceN1, instanceE);
        instance.setChild(instanceN2, instanceN3);
        instance.setChild(instanceN2, instanceD);
        instance.setChild(instanceN3, instanceN4);
        instance.setChild(instanceN3, instanceC);
        instance.setChild(instanceN4, instanceA);
        instance.setChild(instanceN4, instanceB);
        
        instance.rootTheTree();
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instanceN1.isRootNode());
        assertTrue(instanceN1.isRootNode());
        TreeNode root = instance.getRoot();
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + root.equals(instanceN1));
        assertSame(root, instanceN1);
    }

    /**
     * Test of root methods, of class Tree.
     */
    @Test
    public void testRoot_2() {
        System.out.println();
        System.out.println("* TreeTest: testRoot_2()");
        Tree instance = new Tree();
        TreeNode instanceN1 = instance.addNode(10, 0.5);
        TreeNode instanceN2 = instance.addNode(20, 0.5);
        TreeNode instanceN3 = instance.addNode(30, 0.5);
        TreeNode instanceN4 = instance.addNode(40, 0.5);
        TreeNode instanceA  = instance.addNode(100, 0.5, "Agoracea");
        TreeNode instanceB  = instance.addNode(110, 0.5, "Bendricea");
        TreeNode instanceC  = instance.addNode(120, 0.5, "Catonacea");
        TreeNode instanceD  = instance.addNode(130, 0.5, "Draconacea");
        TreeNode instanceE  = instance.addNode(140, 0.5, "Elegoracea");
        
        instance.setChild(instanceN2, instanceN3);
        instance.setChild(instanceN2, instanceD);
        instance.setChild(instanceN3, instanceN4);
        instance.setChild(instanceN3, instanceC);
        instance.setChild(instanceN4, instanceA);
        instance.setChild(instanceN4, instanceB);
        
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instanceN2.hasParent());
        assertFalse(instanceN2.hasParent());
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instanceE.hasParent());
        assertFalse(instanceE.hasParent());
        instance.rootTheTree();
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instanceN1.isRootNode());
        assertTrue(instanceN1.isRootNode());
        TreeNode root = instance.getRoot();
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + root.equals(instanceN1));
        assertSame(root, instanceN1);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instanceN2.hasParent());
        assertTrue(instanceN2.hasParent());
        TreeNode parentN2 = instanceN2.getParent();
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + root.equals(parentN2));
        assertSame(parentN2, root);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instanceE.hasParent());
        assertTrue(instanceE.hasParent());
        TreeNode parentE = instanceE.getParent();
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + root.equals(parentE));
        assertSame(parentE, root);
    }

    /**
     * Test of toNewick method, of class Tree.
     */
    @Test
    public void testToNewick() {
        System.out.println();
        System.out.println("* TreeTest: testToNewick()");
        Tree instance = new Tree();
        TreeNode instanceN4 = instance.addNode(40, 0.5);
        TreeNode instanceA  = instance.addNode(100, 0.5, "Agoracea");
        TreeNode instanceB  = instance.addNode(110, 0.5, "Bendricea");
        instance.setChild(instanceN4, instanceA);
        instance.setChild(instanceN4, instanceB);
        instance.rootTheTree();
        String newick = instance.toNewick();
        String newickN4 = "(Agoracea:0.5,Bendricea:0.5)i40:0.5;";
        System.out.println("  expResult = " + newickN4);
        System.out.println("  result    = " + newick);
        assertEquals(newickN4, newick);
        
        TreeNode instanceN3 = instance.addNode(30, 0.5);
        TreeNode instanceC  = instance.addNode(120, 0.5, "Catonacea");
        instance.setChild(instanceN3, instanceN4);
        instance.setChild(instanceN3, instanceC);
        instance.rootTheTree();
        newick = instance.toNewick();
        String newickN3 = "((Agoracea:0.5,Bendricea:0.5)i40:0.5,Catonacea:0.5)i30:0.5;";
        System.out.println("  -");
        System.out.println("  expResult = " + newickN3);
        System.out.println("  result    = " + newick);
        assertEquals(newickN3, newick);
        
        TreeNode instanceN1 = instance.addNode(10, 0.5);
        TreeNode instanceN2 = instance.addNode(20, 0.5);
        TreeNode instanceD  = instance.addNode(130, 0.5, "Draconacea");
        TreeNode instanceE  = instance.addNode(140, 0.5, "Elegoracea");        
        instance.setChild(instanceN1, instanceN2);
        instance.setChild(instanceN1, instanceE);
        instance.setChild(instanceN2, instanceN3);
        instance.setChild(instanceN2, instanceD);
        instance.rootTheTree();
        newick = instance.toNewick();
        String newickN1 = "((((Agoracea:0.5,Bendricea:0.5)i40:0.5,Catonacea:0.5)i30:0.5,Draconacea:0.5)i20:0.5,Elegoracea:0.5)i10:0.5;";
        System.out.println("  -");
        System.out.println("  expResult = " + newickN1);
        System.out.println("  result    = " + newick);
        assertEquals(newickN1, newick);
    }
    
}
