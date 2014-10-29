package org.phylotastic.mrppath;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ...
 */
public class PathNodeSetTest {
    
    public PathNodeSetTest() {
    }

    /**
     * Test of parsePathNodeSet method, of class PathNodeSet.
     */
    @Test
    public void testFromString() {
        System.out.println();
        System.out.println("* PathNodeSetTest: testFromString()");
        String _string = "628:1.8|625:0.1|624:0.3|623:0.6";
        PathNodeSet instance = PathNodeSet.fromString(_string);
        assertNotNull(instance);
        System.out.println("  expString = " + _string);
        System.out.println("  string    = " + instance.toString());
        assertEquals(4, instance.getSize());
        assertEquals(_string, instance.toString());
    }

    /**
     * Test of addNode method, of class PathNodeSet.
     */
    @Test
    public void testAddNode() {
        System.out.println();
        System.out.println("* PathNodeSetTest: testAddNode()");
        String _string = "628:1.8|625:0.1|623:0.6|620:0.4";
        PathNodeSet instance = PathNodeSet.fromString(_string);
        assertNotNull(instance);
        System.out.println("  expString = " + _string);
        System.out.println("  string    = " + instance.toString());
        assertEquals(4, instance.getSize());
        assertEquals(_string, instance.toString());
        PathNode node = new PathNode(624, 0.3);
        instance.addNode(node);
        _string = "628:1.8|625:0.1|624:0.3|623:0.6|620:0.4";
        System.out.println("  -");
        System.out.println("  expString = " + _string);
        System.out.println("  string    = " + instance.toString());
        assertEquals(5, instance.getSize());
        assertEquals(_string, instance.toString());
    }

    /**
     * Test of addNode method, of class PathNodeSet.
     */
    @Test
    public void testGetSize() {
        System.out.println();
        System.out.println("* PathNodeSetTest: testGetSize()");
        String listString = "628:1.8|625:0.1|624:0.3|623:0.6";
        PathNodeSet instance = PathNodeSet.fromString(listString);
        System.out.println("  expResult = " + 4);
        System.out.println("  result    = " + instance.getSize());
        assertEquals(4, instance.getSize());
        instance.addNode(new PathNode(620, 0.4));
        System.out.println("  -");
        System.out.println("  expResult = " + 5);
        System.out.println("  result    = " + instance.getSize());
        assertEquals(5, instance.getSize());
        instance.addNode(new PathNode(618, 0.8));
        System.out.println("  -");
        System.out.println("  expResult = " + 6);
        System.out.println("  result    = " + instance.getSize());
        assertEquals(6, instance.getSize());
    }

    /**
     * Test of toString method, of class PathNodeSet.
     */
    @Test
    public void testToString() {
        System.out.println();
        System.out.println("* PathNodeSetTest: testGetSize()");
        PathNodeSet instance = new PathNodeSet();
        instance.addNode(new PathNode(628, 0.8));
        String result = instance.toString();
        String expResult = "628:0.8";
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        instance.addNode(new PathNode(623, 0.1));
        result = instance.toString();
        expResult = "628:0.8|623:0.1";
        System.out.println("  -");
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
        instance.addNode(new PathNode(626, 0.3));
        result = instance.toString();
        expResult = "628:0.8|626:0.3|623:0.1";
        System.out.println("  -");
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }
    
}
