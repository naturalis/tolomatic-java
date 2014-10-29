/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.org.phylotastic.mrptree;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import test.org.phylotastic.mrptree.TreeTest;

/**
 *
 * @author ...
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({org.phylotastic.mrptree.TreeNodeTest.class, TreeTest.class})
public class MrptreeSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
}
