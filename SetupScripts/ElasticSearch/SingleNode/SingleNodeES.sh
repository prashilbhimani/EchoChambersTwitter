# Upload this file and the es_single_setup file to Google Shell on their website and run it. It'll work
ESNodeName="esmaster"
ESTag="esmaster"
ProjectId="cu-boulder-community-detection"
InstanceZone="us-east1-b"
MachineType="n1-standard-4"
ESFirewallRuleName="elastic-search-port"
ESSSHFireWall="default-allow-ssh-elasticsearch"


gcloud beta compute --project=$ProjectId instances create $ESNodeName --zone=$InstanceZone --machine-type=$MachineType --subnet=default --network-tier=PREMIUM --maintenance-policy=MIGRATE --scopes=https://www.googleapis.com/auth/cloud-platform --tags=http-server,https-server --image=debian-9-stretch-v20181011 --image-project=debian-cloud --boot-disk-size=50GB --boot-disk-type=pd-standard --boot-disk-device-name=$ESNodeName --tags=$ESTag
echo "Created ES Master"

gcloud compute firewall-rules create $ESFirewallRuleName --direction=INGRESS --priority=1000 --network=default --action=ALLOW --rules=tcp:9200,tcp:9300-9400 --source-ranges=0.0.0.0/0 --target-tags=$ESTag
#gcloud beta compute firewall-rules create $ESFirewallRuleName --allow tcp:9200 --source-tags=$ESTag 
gcloud compute firewall-rules create $ESSSHFireWall --allow tcp:22 --target-tags=$ESTag
echo "Set up firewall-rules for tcp 9200 port and tcp 22 port"


gcloud compute  scp ./es_single_setup.sh $ESNodeName:~/ --zone $InstanceZone
echo "SCP-ed Script"

gcloud compute --project $ProjectId ssh --zone $InstanceZone $ESNodeName  --command "chmod +x es_single_setup.sh && ./es_single_setup.sh" 