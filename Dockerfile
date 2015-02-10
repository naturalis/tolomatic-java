# Creates hadoop 2.6.0 environment with MapReduce pruner
#
# docker build -t rvosa/tolomatic-java-docker .

FROM sequenceiq/hadoop-docker:2.6.0
MAINTAINER rvosa

USER root

# install maven
RUN curl http://mirror.olnevhost.net/pub/apache/maven/maven-3/3.0.5/binaries/apache-maven-3.0.5-bin.tar.gz -o /usr/local/apache-maven-3.0.5-bin.tar.gz
RUN cd /usr/local/ && tar -xvzf apache-maven-3.0.5-bin.tar.gz && cd -
RUN rm /usr/local/apache-maven-3.0.5-bin.tar.gz

# set up environment
ENV M2_HOME /usr/local/apache-maven-3.0.5
ENV M2 $M2_HOME/bin 
ENV PATH $M2:$PATH

# set up env in /etc/profile.d/maven.sh
RUN echo 'export M2_HOME=/usr/local/apache-maven-3.0.5' > /etc/profile.d/maven.sh
RUN echo 'export M2=$M2_HOME/bin' >> /etc/profile.d/maven.sh
RUN echo 'export PATH=$M2:$PATH' >> /etc/profile.d/maven.sh

# set up env for c-shell
RUN echo 'setenv M2_HOME /usr/local/apache-maven-3.0.5' > /etc/profile.d/maven.csh
RUN echo 'setenv M2 $M2_HOME/bin' >> /etc/profile.d/maven.csh
RUN echo 'setenv PATH $M2:$PATH' >> /etc/profile.d/maven.csh

# install git
RUN yum install -y git

# check out tolomatic-java
RUN cd /usr/local/src/ && git clone https://github.com/naturalis/tolomatic-java.git

# run maven
RUN cd /usr/local/src/tolomatic-java && mvn package

# set up env in /etc/profile.d/tolomatic.sh
RUN echo 'export TOLOMATIC_HOME=/usr/local/src/tolomatic-java' > /etc/profile.d/tolomatic.sh
RUN echo 'alias tolomatic="/usr/local/hadoop/bin/hadoop jar /usr/local/src/tolomatic-java/target/MapReducePrune-0.0.1-SNAPSHOT.jar"' >> /etc/profile.d/tolomatic.sh

# set up env for c-shell
RUN echo 'setenv TOLOMATIC_HOME /usr/local/src/tolomatic-java' > /etc/profile.d/tolomatic.csh
RUN echo 'alias tolomatic="/usr/local/hadoop/bin/hadoop jar /usr/local/src/tolomatic-java/target/MapReducePrune-0.0.1-SNAPSHOT.jar"' >> /etc/profile.d/tolomatic.csh

# now copy the input and data files into the hdfs after which point you should now be able to do:
# tolomatic -c <config> -i <input> -t <tempdir> -o <outfile> -r <dataroot> -u <tree url> -d <datadir>