#Install Java
sudo apt update
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

sudo /bin/systemctl daemon-reload
sudo /bin/systemctl enable elasticsearch.service

# Elasticsearch can be started and stopped as follows:
sudo systemctl start elasticsearch.service
# sudo systemctl stop elasticsearch.service
echo "Started ES Daemon"


