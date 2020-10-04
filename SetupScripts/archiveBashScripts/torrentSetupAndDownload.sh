
#Create Instance
gcloud beta compute --project="independentstudy-219521" instances create try1 --zone=us-east1-b --machine-type=n1-standard-4 --subnet=default --network-tier=PREMIUM --maintenance-policy=MIGRATE --service-account=35692078130-compute@developer.gserviceaccount.com --scopes=https://www.googleapis.com/auth/cloud-platform --tags=http-server,https-server --image=debian-9-stretch-v20181011 --image-project=debian-cloud --boot-disk-size=50GB --boot-disk-type=pd-standard --boot-disk-device-name=try1

sleep 5
#SSH in Machine
gcloud compute --project "independentstudy-219521" ssh --zone "us-east1-b" "try1"

echo "Instance created"


#Setup gscfuse
export GCSFUSE_REPO=gcsfuse-`lsb_release -c -s`
echo "deb http://packages.cloud.google.com/apt $GCSFUSE_REPO main" | sudo tee /etc/apt/sources.list.d/gcsfuse.list
curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -

sudo apt-get update
sudo apt-get install gcsfuse
mkdir data/torrent
gcsfuse cu-boulder-echo-chambers-twitter-data data/torrent



#Setup Transmission

sudo add-apt-repository ppa:transmissionbt/ppa
sudo apt-get update
sudo apt-get install transmission-cli transmission-common transmission-daemon
sudo service transmission-daemon start


#Get Torrent and start it

wget https://archive.org/download/archiveteam-twitter-stream-2017-04/archiveteam-twitter-stream-2017-04_archive.torrent
mkdir tar
transmission-cli archiveteam-twitter-stream-2017-04_archive.torrent -w tar/

read -p "Press enter to exit"