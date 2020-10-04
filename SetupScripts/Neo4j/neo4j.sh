#Java
sudo echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | sudo tee /etc/apt/sources.list.d/webupd8team-java.list
sudo echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | sudo tee -a /etc/apt/sources.list.d/webupd8team-java.list
sudo apt-get install dirmngr
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
sudo apt-get update
sudo apt-get install oracle-java8-installer
sudo update-java-alternatives --set java-8-oracle
#Neo4j
sudo apt-get install apt-transport-https ca-certificates
wget -O - https://debian.neo4j.org/neotechnology.gpg.key | sudo apt-key add -
echo 'deb https://debian.neo4j.org/repo stable/' | sudo tee -a /etc/apt/sources.list.d/neo4j.list
sudo apt-get update
sudo apt-get install neo4j=1:3.4.9

#Changing permission of neo4j folder and adding listen from any ip
sudo chmod 777 /etc/neo4j
sudo chmod 777 /etc/neo4j/neo4j.conf
sudo echo "dbms.connectors.default_listen_address=0.0.0.0" >> /etc/neo4j/neo4j.conf
sudo echo "dbms.connector.bolt.listen_address=0.0.0.0:7687" >> /etc/neo4j/neo4j.conf
sudo echo "dbms.connector.http.listen_address=0.0.0.0:7474" >> /etc/neo4j/neo4j.conf
sudo echo "dbms.connector.https.listen_address=0.0.0.0:7473" >> /etc/neo4j/neo4j.conf
#Restart Server
sudo service neo4j restart