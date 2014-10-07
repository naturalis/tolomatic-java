package org.phylotastic.mrppath;
//package org.phylotastic.SourcePackages.mrppath;

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
    public void testParsePathNodeSet() {
        System.out.println("* PathNodeSetTest: testParsePathNodeSet()");
        String listString = "628:1.8|625:0.1|624:0.3|623:0.6";
        PathNodeSet instance = PathNodeSet.parsePathNodeSet(listString);
        assertNotNull(instance);
        assertEquals(4, instance.getSize());
        assertEquals("628:1.8|625:0.1|624:0.3|623:0.6", instance.toString());
    }

    /**
     * Test of addNode method, of class PathNodeSet.
     */
    @Test
    public void testAddNode() {
        System.out.println("* PathNodeSetTest: testAddNode()");
        String listString = "628:1.8|625:0.1|623:0.6|620:0.4";
        PathNodeSet instance = PathNodeSet.parsePathNodeSet(listString);
        assertNotNull(instance);
        assertEquals(4, instance.getSize());
        assertEquals(listString, instance.toString());
        PathNode node = new PathNode(624, 0.3);
        instance.addNode(node);
        assertEquals(5, instance.getSize());
        assertEquals("628:1.8|625:0.1|624:0.3|623:0.6|620:0.4", instance.toString());
    }

    /**
     * Test of addNode method, of class PathNodeSet.
     */
    @Test
    public void testGetSize() {
        System.out.println("* PathNodeSetTest: testGetSize()");
        String listString = "628:1.8|625:0.1|624:0.3|623:0.6";
        PathNodeSet instance = PathNodeSet.parsePathNodeSet(listString);
        assertEquals(4, instance.getSize());
        instance.addNode(new PathNode(620, 0.4));
        assertEquals(5, instance.getSize());
        instance.addNode(new PathNode(618, 0.8));
        assertEquals(6, instance.getSize());
    }

    /**
     * Test of toString method, of class PathNodeSet.
     */
    @Test
    public void testToString() {
        System.out.println("* PathNodeSetTest: testGetSize()");
        PathNodeSet instance = new PathNodeSet();
        instance.addNode(new PathNode(628, 0.8));
        assertEquals("628:0.8", instance.toString());
        instance.addNode(new PathNode(623, 0.1));
        assertEquals("628:0.8|623:0.1", instance.toString());
        instance.addNode(new PathNode(626, 0.3));
        assertEquals("628:0.8|626:0.3|623:0.1", instance.toString());
    }
    
}
