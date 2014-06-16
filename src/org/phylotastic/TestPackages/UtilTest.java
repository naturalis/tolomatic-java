package org.phylotastic.TestPackages;

/**
 * Imports
 */
import org.phylotastic.SourcePackages.Util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * Created by carla on 16-6-14.
 */
public class UtilTest extends Suite{

    /**
     *     Constructor
     */
    public UtilTest(Class<?> klass, RunnerBuilder builder) throws InitializationError
    {
        super(klass, builder);
    }

    @Test
    public void encodeTaxonTest()
    {
        Util myUtil = new Util();
        boolean b;
        boolean sbString = true;
        myUtil.getsbToString();
//        if (myUtil.sbToString instanceof String) b = true;
//        else b = false;
//        assertEquals(sbString, sb);

    }


}
