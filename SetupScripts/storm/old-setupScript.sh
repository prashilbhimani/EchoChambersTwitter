# Create nodes
gcloud beta compute  instances create zookeeper --zone=us-east1-b --machine-type=n1-standard-4 --subnet=default --network-tier=PREMIUM --maintenance-policy=MIGRATE --service-account=35692078130-compute@developer.gserviceaccount.com --scopes=https://www.googleapis.com/auth/cloud-platform --tags=http-server,https-server --image=debian-9-stretch-v20181011 --image-project=debian-cloud --boot-disk-size=50GB --boot-disk-type=pd-standard --boot-disk-device-name=zookeeper
gcloud beta compute  instances create nimbus --zone=us-east1-b --machine-type=n1-standard-4 --subnet=default --network-tier=PREMIUM --maintenance-policy=MIGRATE --service-account=35692078130-compute@developer.gserviceaccount.com --scopes=https://www.googleapis.com/auth/cloud-platform --tags=http-server,https-server --image=debian-9-stretch-v20181011 --image-project=debian-cloud --boot-disk-size=50GB --boot-disk-type=pd-standard --boot-disk-device-name=nimbus
for i in "seq 1 $1";do
	gcloud beta compute  instances create "slave$i" --zone=us-east1-b --machine-type=n1-standard-4 --subnet=default --network-tier=PREMIUM --maintenance-policy=MIGRATE --service-account=35692078130-compute@developer.gserviceaccount.com --scopes=https://www.googleapis.com/auth/cloud-platform --tags=http-server,https-server --image=debian-9-stretch-v20181011 --image-project=debian-cloud --boot-disk-size=50GB --boot-disk-type=pd-standard --boot-disk-device-name="slave$i"
done


# Get IP
zk=$(gcloud compute instances describe zookeeper --zone=us-east1-b |sed -n "/networkIP\:\ /s/networkIP\:\ //p")
nimbus=$(gcloud compute instances describe nimbus --zone=us-east1-b |sed -n "/networkIP\:\ /s/networkIP\:\ //p")
slaves=()
for i in seq 1 $1; do
	slaves+=($(gcloud compute instances describe "slave$i" --zone=us-east1-b |sed -n "/networkIP\:\ /s/networkIP\:\ //p"))
done

# Install java and python on machines
function install_java() {
	sudo apt-get install -y openjdk-8-jre
	export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk-amd64"
}


zookeeper_setup="sudo apt-get install -y openjdk-8-jre && \
export JAVA_HOME='/usr/lib/jvm/java-1.8.0-openjdk-amd64' && \
wget http://www.trieuvan.com/apache/zookeeper/zookeeper-3.4.13/zookeeper-3.4.13.tar.gz &$
tar -xvf zookeeper-3.4.13.tar.gz && \
mkdir tmp/zookeeper/logs -p && \
echo '1' >> tmp/zookeeper/logs/myid && \
cp zookeeper-3.4.13/conf/zoo_sample.cfg zookeeper-3.4.13/conf/zoo.cfg && \
sed -i 's/dataDir=\/tmp\/zookeeper/dataDir=..\/tmp\/zookeeper\/logs/g' zookeeper-3.4.13/conf/zoo.cfg && \
echo 'server.1 = `echo $zk`:2888:3888' >> zookeeper-3.4.13/conf/zoo.cfg"


#Setup zookeeper
gcloud compute --project "independentstudy-219521" ssh --zone "us-east1-b" "zookeeper" --command "$zookeeper_setup"

# Setup Storm
storm_setup="wget http://mirror.cogentco.com/pub/apache/storm/apache-storm-1.2.1/apache-storm-1.2.1.zip && \
sudo apt-get install -y openjdk-8-jre && \
export JAVA_HOME='/usr/lib/jvm/java-1.8.0-openjdk-amd64' && \
unzip apache-storm-1.2.1.zip && \
mkdir tmp/storm/logs -p && \
echo 'storm.zookeeper.servers:`echo $zk`' >> apache-storm-1.2.1/conf/storm.yaml && \
echo 'storm.local.dir: \"~/tmp/storm/logs\"' >>  apache-storm-1.2.1/conf/storm.yaml && \
echo 'nimbus.seeds : [\"$nimbus\"]' >>  apache-storm-1.2.1/conf/storm.yaml && \
echo 'supervisor.slots.port:' >> apache-storm-1.2.1/conf/storm.yaml && \
for i in `seq 1 3`;do \
	echo '- $((6699 + i))'>>apache-storm-1.2.1/conf/storm.yaml \
done && \
rm apache-storm-1.2.1.zip"

#Nimbus setup
gcloud compute --project "independentstudy-219521" ssh --zone "us-east1-b" "nimbus" --command "$setup_storm"
#to-do start nimbus
exit

#setup slaves
for i in "seq 1 $1"; do
	gcloud compute --project "independentstudy-219521" ssh --zone "us-east1-b" "slave$1" --command $setup_storm
	#to-do start worker nodes
	exit
done
