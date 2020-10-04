# Upload the two files on the Google cloud console and run it. it will work
ESMasterNodeName="esmaster"
ESSlave1NodeName="esslave1"
ESSlave2NodeName="esslave2"
ESTag="elasticsearchmultinode"

ProjectId="cu-boulder-community-detection"
InstanceZone="us-east1-b"
MachineType="n1-standard-4"
ESFirewallRuleName="elastic-search-port"
ESSSHFireWall="default-allow-ssh-elasticsearch"

ESScriptPath="es_multi_setup.sh"


gcloud beta compute --project=$ProjectId instances create $ESMasterNodeName --zone=$InstanceZone --machine-type=$MachineType --subnet=default --network-tier=PREMIUM --maintenance-policy=MIGRATE --scopes=https://www.googleapis.com/auth/cloud-platform --tags=http-server,https-server --image=debian-9-stretch-v20181011 --image-project=debian-cloud --boot-disk-size=100GB --boot-disk-type=pd-standard --boot-disk-device-name=$ESNodeName --tags=$ESTag
gcloud beta compute --project=$ProjectId instances create $ESSlave1NodeName --zone=$InstanceZone --machine-type=$MachineType --subnet=default --network-tier=PREMIUM --maintenance-policy=MIGRATE --scopes=https://www.googleapis.com/auth/cloud-platform --tags=http-server,https-server --image=debian-9-stretch-v20181011 --image-project=debian-cloud --boot-disk-size=100GB --boot-disk-type=pd-standard --boot-disk-device-name=$ESNodeName --tags=$ESTag
gcloud beta compute --project=$ProjectId instances create $ESSlave2NodeName --zone=$InstanceZone --machine-type=$MachineType --subnet=default --network-tier=PREMIUM --maintenance-policy=MIGRATE --scopes=https://www.googleapis.com/auth/cloud-platform --tags=http-server,https-server --image=debian-9-stretch-v20181011 --image-project=debian-cloud --boot-disk-size=100GB --boot-disk-type=pd-standard --boot-disk-device-name=$ESNodeName --tags=$ESTag

echo "Created ES Nodes"

# internal Ips
esmaster=$(gcloud compute instances describe $ESMasterNodeName --zone=$InstanceZone |sed -n "/networkIP\:\ /s/networkIP\:\ //p")
esslave1=$(gcloud compute instances describe $ESSlave1NodeName --zone=$InstanceZone |sed -n "/networkIP\:\ /s/networkIP\:\ //p")
esslave2=$(gcloud compute instances describe $ESSlave2NodeName --zone=$InstanceZone |sed -n "/networkIP\:\ /s/networkIP\:\ //p")
echo "Configured IPs are : $esmaster, $esslave1, $esslave2"

gcloud compute firewall-rules create $ESFirewallRuleName --direction=INGRESS --priority=1000 --network=default --action=ALLOW --rules=tcp:9200,tcp:9300-9400 --source-ranges=0.0.0.0/0 --target-tags=$ESTag
gcloud compute firewall-rules create $ESSSHFireWall --allow tcp:22 --target-tags=$ESTag
echo "Set up firewall-rules for tcp 9200 port and tcp 22 port"


gcloud compute  scp $ESScriptPath $ESMasterNodeName:~/ --zone $InstanceZone
gcloud compute  scp $ESScriptPath $ESSlave1NodeName:~/ --zone $InstanceZone
gcloud compute  scp $ESScriptPath $ESSlave2NodeName:~/ --zone $InstanceZone
echo "SCP-ed Script"

gcloud compute --project $ProjectId ssh --zone $InstanceZone $ESMasterNodeName  --command "chmod +x es_multi_setup.sh && ./es_multi_setup.sh $esmaster $esslave1 $esslave2 master" 
gcloud compute --project $ProjectId ssh --zone $InstanceZone $ESSlave1NodeName  --command "chmod +x es_multi_setup.sh && ./es_multi_setup.sh $esmaster $esslave1 $esslave2 slave1" 
gcloud compute --project $ProjectId ssh --zone $InstanceZone $ESSlave2NodeName  --command "chmod +x es_multi_setup.sh && ./es_multi_setup.sh $esmaster $esslave1 $esslave2 slave2" 

