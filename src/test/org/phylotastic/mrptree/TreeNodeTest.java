package org.phylotastic.mrptree;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ...
 */
public class TreeNodeTest {
    
    public TreeNodeTest() {
    }

    /**
     * Test of constructors, of class TreeNode.
     */
    @Test
    public void testConstructor1() {
        System.out.println();
        System.out.println("* TreeNodeTest: testConstructor1()");
        int _id = 0;
        double _length = (double)0.0;
        String _name = "";
        TreeNode instance = new TreeNode();
        String _string = "0::0.0";
        System.out.println("  expString = " + _string);
        System.out.println("  string    = " + instance.toString());
        
        assertEquals(_id, instance.getID());
        assertEquals(0, Double.compare(_length, instance.getLength()));
        assertEquals(_name, instance.getName());
        
        assertEquals(_name, instance.getName());
        assertFalse(instance.hasParent());
        assertFalse(instance.hasChildren());
        assertFalse(instance.isRootNode());
    }

    /**
     * Test of constructors, of class TreeNode.
     */
    @Test
    public void testConstructor2() {
        System.out.println();
        System.out.println("* TreeNodeTest: testConstructor2()");
        int _id = 10;
        double _length = (double)0.5;
        String _name = "";
        TreeNode instance = new TreeNode(_id, _length);
        String _string = "10::0.5";
        System.out.println("  expString = " + _string);
        System.out.println("  string    = " + instance.toString());
        
        assertEquals(_id, instance.getID());
        assertEquals(0, Double.compare(_length, instance.getLength()));
        assertEquals(_name, instance.getName());
    }

    /**
     * Test of constructors, of class TreeNode.
     */
    @Test
    public void testConstructor3() {
        System.out.println();
        System.out.println("* TreeNodeTest: testConstructor3()");
        int _id = 20;
        double _length = (double)0.3;
        String _name = "Elephant";        
        TreeNode instance = new TreeNode(_id, _length, _name);
        String _string = "20:Elephant:0.3";
        System.out.println("  expString = " + _string);
        System.out.println("  string    = " + instance.toString());
        
        assertEquals(_id, instance.getID());
        assertEquals(0, Double.compare(_length, instance.getLength()));
        assertEquals(_name, instance.getName());
    }

    /**
     * Test of get- and setID methods, of class TreeNode.
     */
    @Test
    public void testID() {
        System.out.println();
        System.out.println("* TreeNodeTest: testID()");
        int _id = 0;
        TreeNode instance = new TreeNode();
        System.out.println("  expResult = " + _id);
        System.out.println("  result    = " + instance.getID());
        assertEquals(_id, instance.getID());
        _id = 10;
        instance.setID(_id);
        System.out.println("  -");
        System.out.println("  expResult = " + _id);
        System.out.println("  result    = " + instance.getID());
        assertEquals(_id, instance.getID());
    }

    /**
     * Test of get- and setLength method, of class TreeNode.
     */
    @Test
    public void testLength() {
        System.out.println();
        System.out.println("* TreeNodeTest: testLength()");
        Double _length = (double)0.0;
        TreeNode instance = new TreeNode();
        System.out.println("  expResult = " + _length);
        System.out.println("  result    = " + instance.getLength());
        assertEquals(0, Double.compare(_length, instance.getLength()));
        _length = (double)0.3;
        instance.setLength(_length);
        System.out.println("  -");
        System.out.println("  expResult = " + _length);
        System.out.println("  result    = " + instance.getLength());
        assertEquals(0, Double.compare(_length, instance.getLength()));
    }

    /**
     * Test of get- and setName method, of class TreeNode.
     */
    @Test
    public void testName() {
        System.out.println();
        System.out.println("* TreeNodeTest: testName()");
        String _name = "";
        TreeNode instance = new TreeNode();
        System.out.println("  expResult = " + _name);
        System.out.println("  result    = " + instance.getName());
        assertEquals(_name, instance.getName());
        _name = "Elephant";
        instance.setName(_name);
        System.out.println("  -");
        System.out.println("  expResult = " + _name);
        System.out.println("  result    = " + instance.getName());
        assertEquals(_name, instance.getName());
    }

    /**
     * Test of Parent methods, of class TreeNode.
     */
    @Test
    public void testParent() {
        System.out.println();
        System.out.println("* TreeNodeTest: testParent()");
        TreeNode instanceC = new TreeNode(10, 0.3);
        TreeNode instanceP = new TreeNode(15, 0.5);
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instanceC.hasParent());
        assertFalse(instanceC.hasParent());
        instanceC.setParent(instanceP);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instanceC.hasParent());
        assertTrue(instanceC.hasParent());
        TreeNode instanceX = instanceC.getParent();
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instanceX.equals(instanceP));
        assertSame(instanceX, instanceP);
    }

    /**
     * Test of Child methods, of class TreeNode.
     */
    @Test
    public void testChildren() {
        System.out.println();
        System.out.println("* TreeNodeTest: testChildren()");
        TreeNode instanceC = new TreeNode(10, 0.3);
        TreeNode instanceP = new TreeNode(15, 0.5);
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instanceP.hasChildren());
        assertFalse(instanceP.hasChildren());
        instanceP.addChild(instanceC);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instanceP.hasChildren());
        assertTrue(instanceP.hasChildren());
    }

    /**
     * Test of Root methods, of class TreeNode.
     */
    @Test
    public void testRoot() {
        System.out.println();
        System.out.println("* TreeNodeTest: testRoot()");
        Boolean _value = true;
        TreeNode instance = new TreeNode(10, 0.3);
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.isRootNode());
        assertFalse(instance.isRootNode());
        instance.setAsRootNode(_value);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.isRootNode());
        assertTrue(instance.isRootNode());
    }

    /**
     * Test of getNodeName method, of class TreeNode.
     */
    @Test
    public void testNodeName() {
        System.out.println();
        System.out.println("* TreeNodeTest: testNodeName()");
        TreeNode instancePi = new TreeNode(10, 0.5);
        System.out.println("  expResult = " + "e10");
        System.out.println("  result    = " + instancePi.getNodeName());
        assertEquals("e10", instancePi.getNodeName());
        TreeNode instanceCe = new TreeNode(20, 0.3);
        System.out.println("  -");
        System.out.println("  expResult = " + "e20");
        System.out.println("  result    = " + instanceCe.getNodeName());
        assertEquals("e20", instanceCe.getNodeName());
        instancePi.addChild(instanceCe);
        System.out.println("  -");
        System.out.println("  expResult = " + "i10");
        System.out.println("  result    = " + instancePi.getNodeName());
        assertEquals("i10", instancePi.getNodeName());
        String _name = "Elephant";
        TreeNode instanceCl = new TreeNode(32, 0.3, _name);
        System.out.println("  -");
        System.out.println("  expResult = " + _name);
        System.out.println("  result    = " + instanceCl.getNodeName());
        assertEquals(_name, instanceCl.getNodeName());
        instancePi.addChild(instanceCl);
        System.out.println("  -");
        System.out.println("  expResult = " + _name);
        System.out.println("  result    = " + instanceCl.getNodeName());
        assertEquals(_name, instanceCl.getNodeName());
    }

    /**
     * Test of toString method, of class TreeNode.
     */
    @Test
    public void testToString() {
        System.out.println();
        System.out.println("* TreeNodeTest: testToString()");
        TreeNode instance = new TreeNode();
        String result = instance.toString();
        String expResult = "0::0.0";
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        instance.setID(10);
        result = instance.toString();
        expResult = "10::0.0";
        System.out.println("  -");
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        instance.setName("Elephant");
        result = instance.toString();
        expResult = "10:Elephant:0.0";
        System.out.println("  -");
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        instance.setLength(0.5);
        result = instance.toString();
        expResult = "10:Elephant:0.5";
        System.out.println("  -");
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of compareTo method, of class TreeNode.
     */
    @Test
    public void testCompareTo() {
        System.out.println();
        System.out.println("* TreeNodeTest: testCompareTo()");
        TreeNode instance = new TreeNode(10, 0.5);
        TreeNode empty = new TreeNode();
        TreeNode smaller = new TreeNode(05, 0.5);
        TreeNode equal = new TreeNode(10, 0.5);
        TreeNode larger = new TreeNode(15, 0.5);
        
        System.out.println("  instance = " + instance.toString());    
        System.out.println("  empty    = " + empty.toString()); 
        assertEquals(+1, instance.compareTo(empty));
        System.out.println("  smaller  = " + smaller.toString());
        assertEquals(+1, instance.compareTo(smaller));
        System.out.println("  equal    = " + equal.toString());
        assertEquals( 0, instance.compareTo(equal));
        System.out.println("  larger   = " + larger.toString());
        assertEquals(-1, instance.compareTo(larger));
    }

    /**
     * Test of equals method, of class TreeNode.
     */
    @Test
    public void testEquals() {
        System.out.println();
        System.out.println("* TreeNodeTest: testEquals");
        TreeNode instance           = new TreeNode(10, 0.5);  
        TreeNode instanceNull       = null;
        TreeNode instanceEmpty      = new TreeNode();
        TreeNode instanceEqual      = new TreeNode(10, 0.5);
        TreeNode instanceNotEqual1  = new TreeNode(10, 0.3);
        TreeNode instanceNotEqual2  = new TreeNode(10, 0.5, "Elephant");
        TreeNode instanceSame       = instance;
        TreeNode instanceOther      = new TreeNode(11, 0.5);
        Tree instanceTree           = new Tree();
        
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.equals(instanceTree));
        assertFalse(instance.equals(instanceTree));
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.equals(instanceNull));
        assertFalse(instance.equals(instanceNull));
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.equals(instanceEmpty));
        assertFalse(instance.equals(instanceEmpty));
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.equals(instanceOther));
        assertFalse(instance.equals(instanceOther));
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.equals(instanceNotEqual1));
        assertFalse(instance.equals(instanceNotEqual1));
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.equals(instanceNotEqual2));
        assertFalse(instance.equals(instanceNotEqual2));
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.equals(instanceEqual));
        assertTrue(instance.equals(instanceEqual));
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance.equals(instanceSame));
        assertTrue(instance.equals(instanceSame));
        
        TreeNode instance1 = new TreeNode();
        TreeNode instance2 = new TreeNode();
        System.out.println("  - - - - - - - -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance1.equals(instance2));
        assertTrue(instance1.equals(instance2));
        instance1.setID(10);
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance1.equals(instance2));
        assertFalse(instance1.equals(instance2));
        instance2.setID(10);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance1.equals(instance2));
        assertTrue(instance1.equals(instance2));
        instance1.setName("Elephant");
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance1.equals(instance2));
        assertFalse(instance1.equals(instance2));
        instance2.setName("Elephant");
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance1.equals(instance2));
        assertTrue(instance1.equals(instance2));
        instance1.setLength(0.5);
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance1.equals(instance2));
        assertFalse(instance1.equals(instance2));
        instance2.setLength(0.5);
        System.out.println("  -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance1.equals(instance2));
        assertTrue(instance1.equals(instance2));
    }

    /**
     * Test of toNewick method, of class TreeNode.
     */
    @Test
    public void testToNewick() {
        System.out.println();
        System.out.println("* TreeNodeTest: testToNewick()");
        TreeNode instanceN1 = new TreeNode(10, 0.5);
        TreeNode instanceN2 = new TreeNode(20, 0.5);
        TreeNode instanceN3 = new TreeNode(30, 0.5);
        TreeNode instanceN4 = new TreeNode(40, 0.5);
        TreeNode instanceA  = new TreeNode(100, 0.5, "Agoracea");
        TreeNode instanceB  = new TreeNode(110, 0.5, "Bendricea");
        TreeNode instanceC  = new TreeNode(120, 0.5, "Catonacea");
        TreeNode instanceD  = new TreeNode(130, 0.5, "Draconacea");
        TreeNode instanceE  = new TreeNode(140, 0.5, "Elegoracea");
        
        instanceN1.addChild(instanceN2);
        instanceN1.addChild(instanceE);
        instanceN2.addChild(instanceN3);
        instanceN2.addChild(instanceD);
        instanceN3.addChild(instanceN4);
        instanceN3.addChild(instanceC);
        instanceN4.addChild(instanceA);
        instanceN4.addChild(instanceB);
        instanceN1.setAsRootNode(true);
        
        String newickN4 = "(Agoracea:0.5,Bendricea:0.5)i40:0.5";
        String newickN3 = "((Agoracea:0.5,Bendricea:0.5)i40:0.5,Catonacea:0.5)i30:0.5";
        String newickN2 = "(((Agoracea:0.5,Bendricea:0.5)i40:0.5,Catonacea:0.5)i30:0.5,Draconacea:0.5)i20:0.5";
        String newickN1 = "((((Agoracea:0.5,Bendricea:0.5)i40:0.5,Catonacea:0.5)i30:0.5,Draconacea:0.5)i20:0.5,Elegoracea:0.5)i10:0.5;";
        
        StringBuilder newickString = new StringBuilder();
        instanceN4.toNewick(newickString);
        String newickTree = newickString.toString();
        System.out.println("  expResult = " + newickN4);
        System.out.println("  result    = " + newickTree);
        assertEquals(newickN4, newickTree);
        
        newickString = new StringBuilder();
        instanceN3.toNewick(newickString);
        newickTree = newickString.toString();
        System.out.println("  -");
        System.out.println("  expResult = " + newickN3);
        System.out.println("  result    = " + newickTree);
        assertEquals(newickN3, newickTree);
        
        newickString = new StringBuilder();
        instanceN2.toNewick(newickString);
        newickTree = newickString.toString();
        System.out.println("  -");
        System.out.println("  expResult = " + newickN2);
        System.out.println("  result    = " + newickTree);
        assertEquals(newickN2, newickTree);
        
        newickString = new StringBuilder();
        instanceN1.toNewick(newickString);
        newickTree = newickString.toString();
        System.out.println("  -");
        System.out.println("  expResult = " + newickN1);
        System.out.println("  result    = " + newickTree);
        assertEquals(newickN1, newickTree);
    }
    
}
