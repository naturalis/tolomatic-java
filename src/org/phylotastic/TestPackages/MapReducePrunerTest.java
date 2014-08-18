//package org.phylotastic.TestPackages;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.Suite;
//import org.junit.runners.model.InitializationError;
//import org.junit.runners.model.RunnerBuilder;
//import org.phylotastic.SourcePackages.MapReducePruner;
//
///**
//* Created by carla on 6-6-14.
//*/
//public class MapReducePrunerTest extends Suite{
//
//    //Constructor
//    public MapReducePrunerTest(Class<?> klass, RunnerBuilder builder) throws InitializationError
//    {
//        super(klass, builder);
//    }
//
//    @RunWith(MapReducePrunerTest.class)
//    @Suite.SuiteClasses({ MapReducePrunerTest.Map.class })
//    public static class Map
//    {
//        @Test
//        public void map()
//        {
//            System.out.println("map");
//        }
//    }
//
//    @RunWith(MapReducePrunerTest.class)
//    @Suite.SuiteClasses({ MapReducePrunerTest.Map.class })
//    public static class Combine
//    {
//        @Test
//        public void reduce()
//        {
//            System.out.println("combine");
//        }
//    }
//
//    @RunWith(MapReducePrunerTest.class)
//    @Suite.SuiteClasses({ MapReducePrunerTest.Map.class })
//    public static class Reduce
//    {
//        @Test
//        public void reduce()
//        {
//            System.out.println("reduce");
//        }
//    }
//
//    @RunWith(MapReducePrunerTest.class)
//    @Suite.SuiteClasses({ MapReducePrunerTest.Map.class })
//    public static class main
//    {
//        String pathConfExp = "";
//
//        @Test
//        public void String ()
//        {
//            System.out.println("main dinges");
////            MapReducePruner mainMRP = new MapReducePruner();
////            pathConfExp = "conf/conf.ini";
////            assertEquals(pathConfExp, mainMRP.confFile);
//        }
//    }
//}
//
//
//
