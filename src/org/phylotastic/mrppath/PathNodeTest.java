package org.phylotastic.mrppath;
//package org.phylotastic.SourcePackages.mrppath;

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
    public void testParseNode1() {
        System.out.println("* PathNodeTest: testParseNode1()");
        PathNode instance = PathNode.parseNode("625:0.6");
        assertNotNull(instance);
        assertEquals(625, instance.getLabel());
        assertEquals(0, Double.compare(0.6, instance.getLength()));
        assertEquals("", instance.getName());
    }

    /**
     * Test of parseNode method, of class PathNode.
     */
    @Test
    public void testParseNode2() {
        System.out.println("* PathNodeTest: testParseNode2()");
        PathNode instance = PathNode.parseNode("625:0.5:Parkia");
        assertNotNull(instance);
        assertEquals(625, instance.getLabel());
        assertEquals(0, Double.compare(0.5, instance.getLength()));
        assertEquals("Parkia", instance.getName());
    }

    /**
     * Test of parseNode method, of class PathNode.
     */
    @Test
    public void testParseNode3() {
        System.out.println("* PathNodeTest: testParseNode3()");
        PathNode instance = PathNode.parseNode("625::");
        assertNull(instance);
    }

    /**
     * Test of constructors, of class PathNode.
     */
    @Test
    public void testConstructor1() {
        System.out.println("* PathNodeTest: testConstructor1()");
        int _label = 33 ;
        double _length = (double)0.3;
        String _name = "";        
        PathNode instance = new PathNode(_label, _length);
        assertNotNull(instance);
        assertEquals(_label, instance.getLabel());
        assertEquals(0, Double.compare(_length, instance.getLength()));
        assertEquals(_name, instance.getName());
    }

    /**
     * Test of constructors, of class PathNode.
     */
    @Test
    public void testConstructor2() {
        System.out.println("* PathNodeTest: testConstructor2()");
        int _label = 33;
        double _length = (double)0.3;
        String _name = "Elephant";
        PathNode instance = new PathNode(_label, _length, _name);
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
        System.out.println("* PathNodeTest: testLabel()");
        int oldLabel = 0;
        PathNode instance = new PathNode(oldLabel, 0.0);
        assertNotNull(instance);
        assertEquals(oldLabel, instance.getLabel());
        int newLabel = 10;
        instance.setLabel(newLabel);
        assertEquals(newLabel, instance.getLabel());
    }

    /**
     * Test of Length methods, of class PathNode.
     */
    @Test
    public void testLength() {
        System.out.println("* PathNodeTest: testLength()");
        Double oldLength = (double)0.0;
        PathNode instance = new PathNode(0, oldLength);
        assertNotNull(instance);
        assertEquals(0, Double.compare(oldLength, instance.getLength()));
        Double newLength = (double)0.3;
        instance.setLength(newLength);
        assertEquals(0, Double.compare(newLength, instance.getLength()));
    }

    /**
     * Test of Name methods, of class PathNode.
     */
    @Test
    public void testName() {
        System.out.println("* PathNodeTest: testName()");
        String oldName = "";
        PathNode instance = new PathNode(0, 0.0);
        assertNotNull(instance);
        assertEquals(oldName, instance.getName());
        String newName = "Elephant";
        instance.setName(newName);
        assertEquals(newName, instance.getName());
    }

    /**
     * Test of toString method, of class PathNode.
     */
    @Test
    public void testToString1() {
        System.out.println("* PathNodeTest: testToString1()");
        PathNode instance = new PathNode(10, 0.6);
        String asString = instance.toString();
        assertEquals("10:0.6", asString);
    }

    /**
     * Test of toString method, of class PathNode.
     */
    @Test
    public void testToString2() {
        System.out.println("* PathNodeTest: testToString2()");
        PathNode instance = new PathNode(10, 0.6, "Elephant");
        String asString = instance.toString();
        assertEquals("10:0.6:Elephant", asString);
    }

    /**
     * Test of compareTo method, of class PathNode.
     */
    @Test
    public void testCompareTo() {
        System.out.println("* PathNodeTest: testCompareTo()");
        PathNode instance = new PathNode(10, 0.5);
        PathNode empty = new PathNode(0, 0.0);
        PathNode smaller = new PathNode(05, 0.5);
        PathNode equal = new PathNode(10, 0.5);
        PathNode larger = new PathNode(15, 0.5);
        
        assertEquals(-1, instance.compareTo(empty));
        assertEquals(-1, instance.compareTo(smaller));
        assertEquals( 0, instance.compareTo(equal));
        assertEquals(+1, instance.compareTo(larger));
    }

    /**
     * Test of equals method, of class PathNode.
     */
    @Test
    public void testEquals() {
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
        
        assertFalse(instance.equals(instanceInt));
        assertFalse(instance.equals(instanceNull));
        assertFalse(instance.equals(instanceEmpty));
        assertFalse(instance.equals(instanceOther));
        assertFalse(instance.equals(instanceNotEqual1));
        assertFalse(instance.equals(instanceNotEqual2));
        assertTrue(instance.equals(instanceEqual));
        assertTrue(instance.equals(instanceSame));
        
        PathNode instance1 = new PathNode(0,0.0);
        PathNode instance2 = new PathNode(0,0.0);
        assertTrue(instance1.equals(instance2));
        instance1.setLabel(10);
        assertFalse(instance1.equals(instance2));
        instance2.setLabel(10);
        assertTrue(instance1.equals(instance2));
        instance1.setName("Elephant");
        assertFalse(instance1.equals(instance2));
        instance2.setName("Elephant");
        assertTrue(instance1.equals(instance2));
        instance1.setLength(0.5);
        assertFalse(instance1.equals(instance2));
        instance2.setLength(0.5);
        assertTrue(instance1.equals(instance2));
    }
    
}
