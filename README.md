tolomatic-java
==============

###Introduction
Port of MapReduce pruner to Java/Hadoop

**Authors:**
- @grvosa           
- @gaurav           
- @eightysteele
- @arlin
- @CStegehuis

###Instructions for running inside a docker container

Assuming your docker environment is set up correctly, you can issue the following command
in this folder location for building a container that depends on hadoop 2.6.0:

	$ docker build -t rvosa/tolomatic-java-docker .

The build process will print a number of environment variable definitions to the terminal.
Most likely they will look like the following, which will have to be added to the environment:

    $ export DOCKER_CERT_PATH=$HOME/.boot2docker/certs/boot2docker-vm
    $ export DOCKER_TLS_VERIFY=1
    $ export DOCKER_HOST=tcp://192.168.59.103:2376

To use the Docker image you have just built use:

	$ docker run -i -t rvosa/tolomatic-java-docker /etc/bootstrap.sh -bash

You will now be logged into a (su) shell where everything should be available (maybe
run `source /etc/profile.d/tolomatic.sh` to be sure?). Next, copy the database and the 
input files into the hadoop file system:

	# cd $TOLOMATIC_HOME
	# hdfs dfs -put input tolomatic-input
	# hdfs dfs -put data tolomatic-data

Now you should be able to run a test:

	# <tolomatic> -c conf/config.ini -r <hdfs>/data -d <subdir> -i <hdfs>/input.txt

The following applies:

- `tolomatic` refers to the command to run the jar, e.g. `/usr/local/hadoop/bin/hadoop jar /usr/local/src/tolomatic-java/target/MapReducePrune-0.0.1-SNAPSHOT.jar`
- `hdfs` refers to the file structure inside the Hadoop file system, so these are paths
  and path fragments that were created when you uploaded data using `hdfs dfs -put`
- `subdir` refers to a path fragment inside an `hdfs` folder structure

###Installation instructions for developers

The simplest way to use this program is to install the IntelliJ IDE, import the Maven plugin and run the 
program from within the IDE. These instructions are written on the assumption that the Linux operating 
system is used. The installations will all be conducted by use of the terminal.    


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

In the later versions of IntelliJ IDEA, it is not necessary to integrate a Maven plugin. For the convenience of
the user, the latest version of Maven has been integrated into IntelliJ by default. 

However, in case one wants to use an older version of Maven, it is possible to import the plugin by hand.
The Jetbrains company has a separate webpage for downloading plugins for IntelliJ. Here, any version of the 
Maven 2 integration plugin can be downloaded(http://plugins.jetbrains.com/plugin/1166?pr=). 

**Downloading files from Github**

To download or clone the data from the Github repository, go to the directory the data has to be stored using the 
terminal. Then type the command to clone the data;

~$ git clone https://github.com/naturalis/tolomatic-java.git

The URL used in the command can be found on the main page of the tolomatic-java repository. It is also possible 
to download the entire repository as a zip file.


**Setting up the project**

1. To set up the project, first create a new Java project in IntelliJ. This can be done, by performing the steps
in the instructions below.

    Creating a new Java project:
    ```   
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
    ```

2. After creating the Java project, the pom.xml file should be imported in the root of the project. Simply copy paste the file 
after downloading it from Github. Now right click on the pom.xml file and choose the option 'Add as Maven project'. 
Now IntelliJ will download the sources and documentation of all the packages included in the pom file. 

3. Now the Source Package files can be imported in the source folder of the project.
    
    ```
    1. Right-click on the src folder. Choose 'New > Package' and enter *'org.phylotastic'* as name for the package.
    
    2. In this package create a package *'SourcePackages'* and copy paste the .java files in here. 
    ```

###About the data

The data in the `bik` folder were kindly provided, and patiently explained, by Holly Bik
(@hollybik) for the express purpose of testing the algorithms developed in this project.
Please contact Holly if you want to use these data for something else. She'll probably
be OK with that, but you cannot assume that just because the files are here in this 
repository. 

The paper where these data were originally presented is:

**Holly M. Bik, Kenneth M. Halanych, Jyotsna Sharma** and **W. Kelley Thomas**. 2012. 
Dramatic Shifts in Benthic Microbial Eukaryote Communities following the Deepwater Horizon 
Oil Spill. _PLoS One_. **7**(6): e38550. 
[doi:[10.1371/journal.pone.0038550](http://dx.doi.org/10.1371/journal.pone.0038550)]

Raw pyrosequencing data obtained from marine sediments have been deposited in Dryad 
[doi:[10.5061/dryad.4sd51d4b](http://dx.doi.org/10.5061/dryad.4sd51d4b)], MG-RAST 
(Submission ID: 4478931.3), and the NCBI SRA (Accession No. SRA050276.2).
