package org.phylotastic.mrppath;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ..
 */
public class PathNodeTest {
    
    public PathNodeTest() {
    }

    /**
     * Test of parseNode method, of class PathNode.
     */
    @Test
    public void testFromString_1() {
        System.out.println();
        System.out.println("* PathNodeTest: testFromString_1()");
        String _string = "625:0.6";
        PathNode instance = PathNode.fromString(_string);
        System.out.println("  expString = " + _string);
        System.out.println("  string    = " + instance.toString());
        assertNotNull(instance);
        assertEquals(625, instance.getLabel());
        assertEquals(0, Double.compare(0.6, instance.getLength()));
        assertEquals("", instance.getName());
    }

    /**
     * Test of parseNode method, of class PathNode.
     */
    @Test
    public void testFromString_2() {
        System.out.println();
        System.out.println("* PathNodeTest: testFromString_2()");
        String _string = "625:0.5:Parkia";
        PathNode instance = PathNode.fromString(_string);
        System.out.println("  expString = " + _string);
        System.out.println("  string    = " + instance.toString());
        assertNotNull(instance);
        assertEquals(625, instance.getLabel());
        assertEquals(0, Double.compare(0.5, instance.getLength()));
        assertEquals("Parkia", instance.getName());
    }

    /**
     * Test of parseNode method, of class PathNode.
     */
    @Test
    public void testFromString_3() {
        System.out.println();
        System.out.println("* PathNodeTest: testFromString_3()");
        String _string = "625::";
        PathNode instance = PathNode.fromString(_string);
        assertNull(instance);
    }

    /**
     * Test of constructors, of class PathNode.
     */
    @Test
    public void testConstructor_1() {
        System.out.println();
        System.out.println("* PathNodeTest: testConstructor_1()");
        int _label = 33 ;
        double _length = (double)0.3;
        String _name = "";        
        PathNode instance = new PathNode(_label, _length);
        System.out.println("  expString = " + "33:0.3");
        System.out.println("  string    = " + instance.toString());
        assertNotNull(instance);
        assertEquals(_label, instance.getLabel());
        assertEquals(0, Double.compare(_length, instance.getLength()));
        assertEquals(_name, instance.getName());
    }

    /**
     * Test of constructors, of class PathNode.
     */
    @Test
    public void testConstructor_2() {
        System.out.println();
        System.out.println("* PathNodeTest: testConstructor_2()");
        int _label = 33;
        double _length = (double)0.3;
        String _name = "Elephant";
        PathNode instance = new PathNode(_label, _length, _name);
        System.out.println("  expString = " + "33:0.3:Elephant");
        System.out.println("  string    = " + instance.toString());
        assertNotNull(instance);
        assertEquals(_label, instance.getLabel());
        assertEquals(0, Double.compare(_length, instance.getLength()));
        assertEquals(_name, instance.getName());
    }

    /**
     * Test of Label methods, of class PathNode.
     */
    @Test
    public void testLabel() {
        System.out.println();
        System.out.println("* PathNodeTest: testLabel()");
        int oldLabel = 0;
        PathNode instance = new PathNode(oldLabel, 0.0);
        assertNotNull(instance);
        System.out.println("  expResult = " + oldLabel);
        System.out.println("  result    = " + instance.getLabel());
        assertEquals(oldLabel, instance.getLabel());
        int newLabel = 10;
        instance.setLabel(newLabel);
        System.out.println("  -");
        System.out.println("  expResult = " + newLabel);
        System.out.println("  result    = " + instance.getLabel());
        assertEquals(newLabel, instance.getLabel());
    }

    /**
     * Test of Length methods, of class PathNode.
     */
    @Test
    public void testLength() {
        System.out.println();
        System.out.println("* PathNodeTest: testLength()");
        Double oldLength = (double)0.0;
        PathNode instance = new PathNode(0, oldLength);
        assertNotNull(instance);
        System.out.println("  expResult = " + oldLength);
        System.out.println("  result    = " + instance.getLength());
        assertEquals(0, Double.compare(oldLength, instance.getLength()));
        Double newLength = (double)0.3;
        instance.setLength(newLength);
        System.out.println("  -");
        System.out.println("  expResult = " + newLength);
        System.out.println("  result    = " + instance.getLength());
        assertEquals(0, Double.compare(newLength, instance.getLength()));
    }

    /**
     * Test of Name methods, of class PathNode.
     */
    @Test
    public void testName() {
        System.out.println();
        System.out.println("* PathNodeTest: testName()");
        String oldName = "";
        PathNode instance = new PathNode(0, 0.0);
        assertNotNull(instance);
        System.out.println("  expResult = " + oldName);
        System.out.println("  result    = " + instance.getName());
        assertEquals(oldName, instance.getName());
        String newName = "Elephant";
        instance.setName(newName);
        System.out.println("  -");
        System.out.println("  expResult = " + newName);
        System.out.println("  result    = " + instance.getName());
        assertEquals(newName, instance.getName());
    }

    /**
     * Test of toString method, of class PathNode.
     */
    @Test
    public void testToString_1() {
        System.out.println();
        System.out.println("* PathNodeTest: testToString1()");
        PathNode instance = new PathNode(10, 0.6);
        String result = instance.toString();
        String expResult = "10:0.6";
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class PathNode.
     */
    @Test
    public void testToString_2() {
        System.out.println();
        System.out.println("* PathNodeTest: testToString2()");
        PathNode instance = new PathNode(10, 0.6, "Elephant");
        String result = instance.toString();
        String expResult = "10:0.6:Elephant";
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of compareTo method, of class PathNode.
     */
    @Test
    public void testCompareTo() {
        System.out.println();
        System.out.println("* PathNodeTest: testCompareTo()");
        PathNode instance = new PathNode(10, 0.5);
        PathNode empty = new PathNode(0, 0.0);
        PathNode smaller = new PathNode(05, 0.5);
        PathNode equal = new PathNode(10, 0.5);
        PathNode larger = new PathNode(15, 0.5);
        
        System.out.println("  instance = " + instance.toString());    
        System.out.println("  empty    = " + empty.toString());    
        assertEquals(-1, instance.compareTo(empty));
        System.out.println("  smaller  = " + smaller.toString());
        assertEquals(-1, instance.compareTo(smaller));
        System.out.println("  equal    = " + equal.toString());
        assertEquals( 0, instance.compareTo(equal));
        System.out.println("  larger   = " + larger.toString());
        assertEquals(+1, instance.compareTo(larger));
    }

    /**
     * Test of equals method, of class PathNode.
     */
    @Test
    public void testEquals() {
        System.out.println();
        System.out.println("* PathNodeTest: testEquals");
        PathNode instance               = new PathNode(10, 0.5);  
        PathNode instanceNull           = null;
        PathNode instanceEmpty          = new PathNode(0,0.0);
        PathNode instanceEqual          = new PathNode(10, 0.5);
        PathNode instanceNotEqual1      = new PathNode(10, 0.3);
        PathNode instanceNotEqual2      = new PathNode(10, 0.5, "Elephant");
        PathNode instanceSame           = instance;
        PathNode instanceOther          = new PathNode(11, 0.5);
        PathNodeInternal instanceInt    = new PathNodeInternal(10,0.5,5);
        
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance.equals(instanceInt));
        assertFalse(instance.equals(instanceInt));
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
        
        PathNode instance1 = new PathNode(0,0.0);
        PathNode instance2 = new PathNode(0,0.0);
        System.out.println("  - - - - - - - -");
        System.out.println("  expResult = " + true);
        System.out.println("  result    = " + instance1.equals(instance2));
        assertTrue(instance1.equals(instance2));
        instance1.setLabel(10);
        System.out.println("  -");
        System.out.println("  expResult = " + false);
        System.out.println("  result    = " + instance1.equals(instance2));
        assertFalse(instance1.equals(instance2));
        instance2.setLabel(10);
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
    
}
