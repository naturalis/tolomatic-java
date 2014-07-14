tolomatic-java
==============

###Introduction
Port of MapReduce pruner to Java/Hadoop

To simplify the use of phylogenetic trees in scientific research, a program has been written
to extract subsets from arbitrary-size trees. This program uses the MapReduce algorithm(originated from Google)
of the Apache Hadoop infrastructure to reduce the post-order traversal data of a tree, in order to extract the data
of the subtree. Once the subset data has been saved in a Newick format file, it can be used for research purposes.

**Authors:**
- @grvosa           
- @gaurav           
- @eightysteele
- @arlin
- @CStegehuis


###Dependencies

**Software**    |   **Hardware**
----------------|----------------
Linux OS        | ...
IntelliJ IDEA, including Maven plugin | ....


###Installation instructions

The simplest way to use this program is to install the IntelliJ IDE, import the Maven plugin and run the 
program from within the IDE. These instructions are written on the assumption that the Linux operating 
system are used. The installations will all be conducted by use of the terminal.    


**Installation Oracle JDK**

To install the Oracle JDK, first three commands have to be entered;
    
    $ sudo apt-get-repository pa: webupd8team/java
    
    $ sudo apt-get update
    
    $ sudo apt-get install oracle-java7-installer
    
Next, with the '$ java -version' command, it is possible to inspect whether the version is correct.
The outcome of this command should look something like this:

    java version "1.7.0_11"
    Java(TM) SE Runtime Environment (build 1.7.0_11-b21)
    Java HotSpot(TM) 64-Bit Server VM (build 23.6-b04, mixed mode)

If this is not how the outcome returns, the following command can be tried;
    
    $ sudo update-java-alternatives -s java-7-oracle


**Installation IntelliJ IDEA**

To install IntelliJ, first the program must be downloaded, from the Jetbrains 
website; http://www.jetbrains.com/idea/download/. When downloading the appropriate file, be sure to
verify whether the OS is set to the one used on the computer or server. 
The downloaded file is a .tar.gz file, which must be extracted. After which, open the containing folder, 
then go to the bin folder and open the 'idea.sh' file with a text editor. After opening, add the following
command on the first line of the file after the commends.
    
    export IDEA_JDK=/*path to java-7-oracle*/

The path should be something like this;
    
    export IDEA_JDK=/usr/lib/jvm/java-7-oracle

The installation is now completed and the program can be executed by typing the commands below in
the terminal;

    $ cd /<location to tar file, extracted folder>/idea-IU-135.690/bin/
    
    $ ./idea.sh
       
    
**Installation Maven plugin**



**Setting up the project**

1. To set up the project, first create a new Java project in IntelliJ. This can be done, by performing the steps
in the instructions below.

    Creating a new Java project:   
    
    1. Press 'File > New Project';
    
    2. First menu
        * Java should be selected in the options menu on the left. Presumably Java will be selected by default.
        * Make sure that the Java EE version is set to Java EE 7. 
    
    3. Second menu
        * It is not required, but if wanted the option 'create project from template' can be selected.
        
    4. Third menu
        * Enter the project name. 
        * The project location should be correct, but if not, change it to the
        wanted location. 
        * The project SDK should be set to *1.7 (java version "1.7.0_51")*. If another version of Java is used, 
         select that version. 
    
    5. Press finish

2. After creating the Java project, the pom.xml file should be imported in the root of the project. Simply copy paste the file 
after downloading it from Github. Now right click on the pom.xml file and choose the option 'Add as Maven project'. 
Now IntelliJ will download the sources and documentation of all the packages included in the pom file. 

3. Now the Source Package files can be imported in the source folder of the project.

    1. Right-click on the src folder. Choose 'New > Package' and enter *'org.phylotastic'* as name for the package.
    
    2. In this package create a package *'SourcePackages'* and copy paste the .java files in here. 
