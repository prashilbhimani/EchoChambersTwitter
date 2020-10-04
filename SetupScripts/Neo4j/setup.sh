NEO4JNodeName="neo4jnodetest"

NeoTag="neo4jnode"

ProjectId="cu-boulder-community-detection"
InstanceZone="us-east1-b"
MachineType="n1-standard-4"
# ServiceAccount="196775195773-compute@developer.gserviceaccount.com"
ESFirewallRuleName="neo4j-port"
ESSSHFireWall="default-allow-ssh-neo4j"

Neo4JScriptPath="./neo4j.sh"


gcloud beta compute --project=$ProjectId instances create $NEO4JNodeName --zone=$InstanceZone --machine-type=$MachineType --subnet=default --network-tier=PREMIUM --maintenance-policy=MIGRATE --scopes=https://www.googleapis.com/auth/cloud-platform --tags=http-server,https-server --image=debian-9-stretch-v20181011 --image-project=debian-cloud --boot-disk-size=50GB --boot-disk-type=pd-standard --boot-disk-device-name=$ESNodeName --tags=$NeoTag

gcloud compute firewall-rules create $ESFirewallRuleName --direction=INGRESS --priority=1000 --network=default --action=ALLOW --rules=tcp:7474,tcp:7687 --source-ranges=0.0.0.0/0 --target-tags=$NeoTag
gcloud compute firewall-rules create $ESSSHFireWall --allow tcp:22 --target-tags=$NeoTag
echo "Set up firewall-rules for tcp 7474,7687 port and tcp 22 port"

gcloud compute  scp $Neo4JScriptPath $NEO4JNodeName:~/ --zone $InstanceZone
echo "SCP-ed Script"

gcloud compute --project $ProjectId ssh --zone $InstanceZone $NEO4JNodeName  --command "chmod +x neo4j.sh && ./neo4j.sh" 
