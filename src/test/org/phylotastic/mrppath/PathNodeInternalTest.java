package org.phylotastic.mrppath;

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
    public void testFromString_1() {
        System.out.println();
        System.out.println("* PathNodeInternalTest: testFromString_1()");
        String _string = "625:0.6,5";
        PathNodeInternal instance = PathNodeInternal.fromString(_string);
        assertNotNull(instance);
        System.out.println("  expString = " + _string);
        System.out.println("  string    = " + instance.toString());
        assertEquals(625, instance.getLabel());
        assertEquals(0, Double.compare(0.6, instance.getLength()));
        assertEquals("", instance.getName());
        assertEquals(5, instance.getTipCount());
    }

    /**
     * Test of parseNode method, of class PathNodeInternal.
     */
    @Test
    public void testFromString_2() {
        System.out.println();
        System.out.println("* PathNodeInternalTest: testFromString_2()");
        String _string = "625:0.5:Parkia,5";
        PathNodeInternal instance = PathNodeInternal.fromString(_string);
        assertNull(instance);
    }

    /**
     * Test of parseNode method, of class PathNodeInternal.
     */
    @Test
    public void testFromString_3() {
        System.out.println();
        System.out.println("* PathNodeInternalTest: testFromString_3()");
        PathNodeInternal instance = PathNodeInternal.fromString("625:,");
        assertNull(instance);
    }

    /**
     * Test of parseNode method, of class PathNodeInternal.
     */
    @Test
    public void testFromText() {
        System.out.println();
        System.out.println("* PathNodeInternalTest: testFromText()");
        /*
        * this test cannot be implemented; junit in combination with
        * hadoop.io.text causes all sorts of version trouble
        */
    }

    /**
     * Test of constructors, of class PathNodeInternal.
     */
    @Test
    public void testConstructor() {
        System.out.println();
        System.out.println("* PathNodeInternalTest: testConstructor()");
        int _label = 33 ;
        double _length = (double)0.3;
        String _name = "";
        int _count = 3;
        PathNodeInternal instance = new PathNodeInternal(_label, _length, _count);
        assertNotNull(instance);
        System.out.println("  expString = " + "33:0.3,3");
        System.out.println("  string    = " + instance.toString());
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
        System.out.println();
        System.out.println("* PathNodeInternalTest: testTipCount()");
        int oldCount = 0;
        PathNodeInternal instance = new PathNodeInternal(oldCount, 0.0);
        assertNotNull(instance);
        System.out.println("  expResult = " + oldCount);
        System.out.println("  result    = " + instance.getTipCount());
        assertEquals(oldCount, instance.getTipCount());
        int newCount = 5;
        instance.setTipCount(newCount);
        System.out.println("  -");
        System.out.println("  expResult = " + newCount);
        System.out.println("  result    = " + instance.getTipCount());
        assertEquals(newCount, instance.getTipCount());
    }

    /**
     * Test of toString method, of class PathNodeInternal.
     */
    @Test
    public void testToString() {
        System.out.println();
        System.out.println("* PathNodeInternalTest: testToString()");
        int _label = 33 ;
        double _length = (double)0.3;
        int _count = 3;
        PathNodeInternal instance = new PathNodeInternal(_label, _length, _count);
        assertNotNull(instance);
        String expResult = "33:0.3,3";
        String result = instance.toString();
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + instance.toString());
        assertEquals(expResult, result);
    }
    
}
