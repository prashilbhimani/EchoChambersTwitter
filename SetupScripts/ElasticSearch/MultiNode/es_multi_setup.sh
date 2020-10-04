esmaster=$1
esslave1=$2
esslave2=$3
name=$4

echo "(esmaster, esslave1, esslave2) = $esmaster, $esslave1, $esslave2"

sudo apt update && sudo apt-get upgrade -Y
#Install Java
sudo apt install -y openjdk-8-jre
export JAVA_HOME='/usr/lib/jvm/java-1.8.0-openjdk-amd64'

wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -
echo "Fetched elasticsearch"

sudo apt install -y apt-transport-https
echo "Installed HTTPS"

echo "deb https://artifacts.elastic.co/packages/6.x/apt stable main" | sudo tee -a /etc/apt/sources.list.d/elastic-6.x.list
echo "Saved the repository definition /etc/apt/sources.list.d/elastic-6.x.list"



sudo apt update && sudo apt install -y elasticsearch
echo "Updated and Installed ElasticSearch"

sudo chmod 777 /etc/elasticsearch
sudo chmod 777 /etc/elasticsearch/elasticsearch.yml
sudo echo "network.host: 0.0.0.0" >> /etc/elasticsearch/elasticsearch.yml

#give your cluster a name.
sudo echo "cluster.name: my-cluster" >> /etc/elasticsearch/elasticsearch.yml


#give your nodes a name (change node number from node to node).
if [ "$name" == "master" ]
then
	sudo echo "node.name: \"es-master\""  >> /etc/elasticsearch/elasticsearch.yml
	sudo echo "node.master: true"  >> /etc/elasticsearch/elasticsearch.yml
fi


if [ "$name" == "slave1" ]
then
	sudo echo "node.name: \"es-slave-1\""  >> /etc/elasticsearch/elasticsearch.yml
	sudo echo "node.data: true"  >> /etc/elasticsearch/elasticsearch.yml
fi

if [ "$name" == "slave2" ]
then
	sudo echo "node.name: \"es-slave-2\""  >> /etc/elasticsearch/elasticsearch.yml
	sudo echo "node.data: true"  >> /etc/elasticsearch/elasticsearch.yml
fi


#detail the private IPs of your nodes:
echo "(esmater, esslave2, esslave2) = $esmaster, $esslave1, $esslave2"
sudo echo "discovery.zen.ping.unicast.hosts: [$esmaster, $esslave1, $esslave2]" >> /etc/elasticsearch/elasticsearch.yml

echo "Finished Setting ES Master YAML"


sudo /bin/systemctl daemon-reload
sudo /bin/systemctl enable elasticsearch.service

# Elasticsearch can be started and stopped as follows:
sudo systemctl start elasticsearch.service
# sudo systemctl stop elasticsearch.service
echo "Started ES Daemon"
