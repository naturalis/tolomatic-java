package org.phylotastic.TestPackages;

//Imports
import org.apache.hadoop.fs.Path;
import org.phylotastic.SourcePackages.Util;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import static junit.framework.TestCase.assertSame;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.util.List;

import junit.framework.ComparisonFailure;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertTrue;

public class UtilTest{

    /**
     *     Constructor
     */
//    extends Suite
//    public UtilTest(Class<?> klass, RunnerBuilder builder) throws InitializationError
//    {
//        super(klass, builder);
//    }

//    @Test
//    Util myUtil = new Util();
//    String systemSeperator = File.separator;
//    assertEquals(systemSeperator, myUtil.slash);

    @Test
    public void encodeTaxonTest()
    {
        Util myUtil = new Util();
        String utilSB = myUtil.getEncodeTaxon("Psilotaceae");

        String taxonCoded = "7bc3f2f1dfa7a08b65f153b9369d22b2";

        System.out.println("utilSB = " + utilSB);
        System.out.println("taxonCoded = " + taxonCoded);

        assertEquals(taxonCoded, utilSB);
    }

    @Test
    public void TaxonDirTest()
//    public void TaxonDirTest() throws MalformedURLException
    {
        String taxon = "Psilotaceae";
        URL evoio = null;

//        evoio = new URL("http://www.evoio.org/wg/evoio/images/9/9c/Phylomatictree.nex");
        try {
            evoio = new URL("http://www.evoio.org/wg/evoio/images/9/9c/Phylomatictree.nex");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Util myUtil = new Util(new File("/home/carla/IdeaProjects/tolomatic-java/conf/conf.ini"));

        File taxonDir = myUtil.getTaxonDir(evoio, taxon);
        File file = new File("/home/carla/IdeaProjects/tolomatic-java/data/File_Phylomatictree/7/b/c/3/f");

        System.out.println("file = " + file);
        System.out.println("taxonDir = " + taxonDir);

        assertEquals(file, taxonDir);
    }

//    @Test
//    public void GetTreeTest()
////    public void GetTreeTest() throws MalformedURLException
//    {
//        Util myUtil = new Util();
//        URL URLTree = myUtil.getTree();
//        URL url = null;
////        URL url = new URL("http://www.evoio.org/wg/evoio/images/9/9c/Phylomatictree.nex");
//        try {
//            url = new URL("http://www.evoio.org/wg/evoio/images/9/9c/Phylomatictree.nex");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("URL Tree = " + URLTree);
//        System.out.println("URL = " + url);
//        assertEquals(URLTree, url);
//
//    }

    @Test
    public void GetOutputPathTest()
    {
        Util myUtil = new Util(new File("/home/carla/IdeaProjects/tolomatic-java/conf/conf.ini"));

        Path taxonDir = myUtil.getOutputPath();
        Path tempOutputPath = new Path("OutputData");

        System.out.println("tempOutputPath = " + tempOutputPath);
        System.out.println("taxonDir = " + taxonDir);

        assertEquals(tempOutputPath, taxonDir);
    }

    @Test
    public void ReadTaxonFileTest()
    {
        Util myUtil = new Util();
//        Util myUtil = new Util(new File("/home/carla/IdeaProjects/tolomatic-java/conf/conf.ini"));

//        List result = myUtil.readTaxonFile();

        Path taxonDir = myUtil.getOutputPath();
        Path tempOutputPath = new Path("OutputData");

        System.out.println("tempOutputPath = " + tempOutputPath);
        System.out.println("taxonDir = " + taxonDir);

        assertEquals(tempOutputPath, taxonDir);
    }
}
