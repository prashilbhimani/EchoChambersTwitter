#Install Java
sudo apt-get update
sudo apt-get install -y openjdk-8-jre
export JAVA_HOME='/usr/lib/jvm/java-1.8.0-openjdk-amd64'

#Get Storm
wget http://mirror.cogentco.com/pub/apache/storm/apache-storm-1.2.1/apache-storm-1.2.1.zip
sudo apt-get  install -y unzip
unzip apache-storm-1.2.1.zip


mkdir tmp/storm/logs -p
#change this
echo "storm.zookeeper.servers: - \"$1\"" >> apache-storm-1.2.1/conf/storm.yaml
echo "storm.local.dir: \"~/tmp/storm/logs\"" >>  apache-storm-1.2.1/conf/storm.yaml
echo "nimbus.seeds : [\"$2\"]" >>  apache-storm-1.2.1/conf/storm.yaml
echo "supervisor.slots.port:" >> apache-storm-1.2.1/conf/storm.yaml

x=($(seq 1 $3))
for each in "${x[@]}";do
	echo "- $((6998+each))"  >> apache-storm-1.2.1/conf/storm.yaml
done
rm apache-storm-1.2.1.zip