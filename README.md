tolomatic-java
==============

Port of MapReduce pruner to Java/Hadoop

To simplify the use of phylogenetic trees in scientific research, a program has been written
to extract subsets from arbitrary-size trees. This program uses the MapReduce algorithm(originated from Google)
of the Apache Hadoop infrastructure to reduce the post-order traversal data of a tree, in order to extract the data
of the subtree. Once the subset data has been saved in a Newick format file, it can be used for research purposes.

Authors:
- @grvosa           
- @gaurav           
- @eightysteele
- @arlin
- @CStegehuis


###Dependencies

**Software**
1. Linux operating system
2. IntelliJ IDEA, including Maven plugin

**Hardware**
1. 

###Short installation instructions:

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






