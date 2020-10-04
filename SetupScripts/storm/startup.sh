##Do not run this. The commands are blocking - Check how to set them to background

gcloud compute --project "cu-boulder-community-detection" ssh --zone "us-east1-b" "zookeeper" --command "./zookeeper-3.4.13/bin/zkServer.sh start"
gcloud compute --project "cu-boulder-community-detection" ssh --zone "us-east1-b" "nimbus" --command "./apache-storm-1.2.1/bin/storm nimbus"
for i in `seq 1 $1`; do
	gcloud compute --project "cu-boulder-community-detection" ssh --zone "us-east1-b" "slave$i" --command "./apache-storm-1.2.1/bin/storm supervisor"
done
gcloud compute --project "cu-boulder-community-detection" ssh --zone "us-east1-b" "nimbus" --command "./apache-storm-1.2.1/bin/storm ui"
