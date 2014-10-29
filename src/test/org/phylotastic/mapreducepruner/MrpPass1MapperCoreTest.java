package org.phylotastic.mapreducepruner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.phylotastic.mrppath.*;

/**
 *
 * @author ...
 */
public class MrpPass1MapperCoreTest {
    MrpPass1Mapper.Core instance;
    
    public MrpPass1MapperCoreTest() {
    }
    
    @Before
    public void setUp() throws IOException {
        instance = new MrpPass1Mapper.Core();
        instance.setup("/user/naturalis/", 5, null, Path.SEPARATOR);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of tidyTaxonName method, of class MrpPass1Mapper.Core.
     */
    @Test
    public void testTidyTaxonName() {
        System.out.println("* MrpPass1MapperCoreTest: testTidyTaxonName()");
        String dirtyName = "  banksia_Spinulosa_Collina     ";
        String expResult = "Banksia spinulosa collina";
        String result = instance.tidyTaxonName(dirtyName);
        System.out.println("  dirtyName = " + dirtyName);
        System.out.println("  expResult = " + expResult);
        System.out.println("  result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of determineTaxonFile method, of class MrpPass1Mapper.Core.
     * @throws java.lang.Exception
     */
    @Test
    public void testDetermineTaxonFile() throws Exception {
        System.out.println("* MrpPass1MapperCoreTest: testDetermineTaxonFile()");
        instance.hashDepth = (int)4;
        instance.dataPath  = "/user/naturalis/";
        String taxon = "Banksia spinulosa collina";
        String expResult = "/user/naturalis/1/5/d/1/15d18c5423b7d99034a97ecc9a59f78d";
        String result = instance.determineTaxonFile(taxon);
        System.out.println("  Taxon     = " + taxon);
        System.out.println("  hashDepth = " + instance.hashDepth);
        System.out.println("  expResult = " + expResult);
        System.out.println("  Result    = " + result);
        assertEquals(expResult, result);
        instance.hashDepth = (int)5;
        expResult = "/user/naturalis/1/5/d/1/8/15d18c5423b7d99034a97ecc9a59f78d";
        result = instance.determineTaxonFile(taxon);
        System.out.println();
        System.out.println("  Taxon     = " + taxon);
        System.out.println("  hashDepth = " + instance.hashDepth);
        System.out.println("  expResult = " + expResult);
        System.out.println("  Result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of makeSafeString method, of class MrpPass1Mapper.Core.
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeSafeString() throws Exception {
        System.out.println("* MrpPass1MapperCoreTest: testMakeSafeString()");
        String unsafeString = "Banksia spinulosa collina";
        String expResult = "15d18c5423b7d99034a97ecc9a59f78d";
        String result = instance.makeSafeString(unsafeString);
        System.out.println("  expResult = " + expResult);
        System.out.println("  Result    = " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of readTaxonPath method, of class MrpPass1Mapper.Core.
     * Test is not (yet) implemented because it needs an
     * operational hadoop filesystem to read the path file
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testReadTaxonPath() throws Exception {
        System.out.println("* MrpPass1MapperCoreTest: testReadTaxonPath()");
        System.out.println("  - is not implemented");
        
        // trying to execute the test below causes an error
        // with relation to hadoop filesystem
        // and a whole lot of problems relating to
        // versions of underlying library jars, like
        // google/./preconditions, etc.
        // possibly caused by the combination with junit
        
//        String filePath = "C:/Users/Jan/Mijn netBeans/MapReducePruner/unitTestunitTest/banksia/siabank";
//        String expResult = "628:18|625:1|624:1|623:1|622:1|621:1|581:1|513:1|505:1| ..... |5:1|4:1|3:1|2:1|1:1";
//        String result = instance.readTaxonPath(filePath);
//        System.out.println("  expResult = " + expResult);
//        System.out.println("  Result    = " + result);
//        assertEquals(expResult, result);
    }

    /**
     * Test of getTaxonNodes method, of class MrpPass1Mapper.Core.
     */
    @Test
    public void testGetTaxonNodes() {
        System.out.println("* MrpPass1MapperCoreTest: testGetTaxonNodes()");
        List<PathNode> expResult = new ArrayList<>();
        expResult.add(new PathNode(7, 0.1));
        expResult.add(new PathNode(6, 0.2));
        expResult.add(new PathNode(5, 0.3));
        expResult.add(new PathNode(4, 0.4));
        expResult.add(new PathNode(3, 0.5));
        expResult.add(new PathNode(2, 0.6));
        expResult.add(new PathNode(1, 0.7));
        String taxonPath = "7:0.1|6:0.2|5:0.3|4:0.4|3:0.5|2:0.6|1:0.7";
        List<PathNode> result = instance.getTaxonNodes(taxonPath);
        System.out.println("  expResult    = " + expResult.toString());
        System.out.println("  result       = " + result.toString());
        assertEquals(expResult, result);
    }
    
}
