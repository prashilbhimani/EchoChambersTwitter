#Install Java
sudo apt-get update
sudo apt-get install -y openjdk-8-jre
export JAVA_HOME='/usr/lib/jvm/java-1.8.0-openjdk-amd64'

#Get zookeeper
wget http://www.trieuvan.com/apache/zookeeper/zookeeper-3.4.13/zookeeper-3.4.13.tar.gz
tar -xvf zookeeper-3.4.13.tar.gz

#Set myid file
mkdir tmp/zookeeper/logs -p
echo '1' >> tmp/zookeeper/logs/myid


cp zookeeper-3.4.13/conf/zoo_sample.cfg zookeeper-3.4.13/conf/zoo.cfg
sed -i 's/dataDir=\/tmp\/zookeeper/dataDir=~\/tmp\/zookeeper\/logs/g' zookeeper-3.4.13/conf/zoo.cfg
echo "server.1 = $1:2888:3888" >> zookeeper-3.4.13/conf/zoo.cfg