/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.phylotastic.mrppath;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Jan
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({org.phylotastic.mrppath.PathNodeInternalTest.class, org.phylotastic.mrppath.PathNodeSetTest.class, org.phylotastic.mrppath.PathNodeTest.class})
public class MrppathSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
