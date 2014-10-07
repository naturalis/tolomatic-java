package org.phylotastic.mrppath;
//package org.phylotastic.SourcePackages.mrppath;

import org.apache.hadoop.io.Text;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ..
 */
public class PathNodeInternalTest {
    
    public PathNodeInternalTest() {
    }

    /**
     * Test of parseNode method, of class PathNodeInternal.
     */
    @Test
    public void testParseNode_String1() {
        System.out.println("* PathNodeInternalTest: testParseNode_String1()");
        PathNodeInternal instance = PathNodeInternal.parseNode("625:0.6,5");
        assertNotNull(instance);
        assertEquals(625, instance.getLabel());
        assertEquals(0, Double.compare(0.6, instance.getLength()));
        assertEquals("", instance.getName());
        assertEquals(5, instance.getTipCount());
    }

    /**
     * Test of parseNode method, of class PathNodeInternal.
     */
    @Test
    public void testParseNode_String2() {
        System.out.println("* PathNodeInternalTest: testParseNode_String2()");
        PathNodeInternal instance = PathNodeInternal.parseNode("625:0.5:Parkia,5");
        assertNull(instance);
    }

    /**
     * Test of parseNode method, of class PathNodeInternal.
     */
    @Test
    public void testParseNode_String3() {
        System.out.println("* PathNodeInternalTest: testParseNode_String3()");
        PathNodeInternal instance = PathNodeInternal.parseNode("625:,");
        assertNull(instance);
    }

    /**
     * Test of parseNode method, of class PathNodeInternal.
     */
    @Test
    public void testParseNode_Text() {
        System.out.println("* PathNodeInternalTest: testParseNodeText()");
        Text nodeText = new Text("625:0.6,5");
        PathNodeInternal instance = PathNodeInternal.parseNode(nodeText);
        assertNotNull(instance);
        assertEquals(625, instance.getLabel());
        assertEquals(0, Double.compare(0.6, instance.getLength()));
        assertEquals("", instance.getName());
        assertEquals(5, instance.getTipCount());
    }

    /**
     * Test of constructors, of class PathNodeInternal.
     */
    @Test
    public void testConstructor1() {
        System.out.println("* PathNodeInternalTest: testConstructor1()");
        int _label = 33 ;
        double _length = (double)0.3;
        String _name = "";
        int _count = 3;
        PathNodeInternal instance = new PathNodeInternal(_label, _length, _count);
        assertNotNull(instance);
        assertEquals(_label, instance.getLabel());
        assertEquals(0, Double.compare(_length, instance.getLength()));
        assertEquals(_name, instance.getName());
        assertEquals(_count, instance.getTipCount());
    }

    /**
     * Test of TipCount methods, of class PathNodeInternal.
     */
    @Test
    public void testTipCount() {
        System.out.println("* PathNodeInternalTest: testTipCount()");
        int oldCount = 0;
        PathNodeInternal instance = new PathNodeInternal(oldCount, 0.0);
        assertNotNull(instance);
        assertEquals(oldCount, instance.getTipCount());
        int newCount = 5;
        instance.setTipCount(newCount);
        assertEquals(newCount, instance.getTipCount());
    }
    
}
