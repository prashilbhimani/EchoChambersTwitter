import json
f = open('relationship_new.txt', 'r+')
lines=[]
for line in f.readlines():
    newline=json.loads(line)
    # if (newline['country_code']):
    #     newline['country_code']=newline['country_code']
    # else:
    #     newline['country_code']="NULL"
    lines.append(newline)
# lines = [json.loads(line) for line in f.readlines()]
f.close()
created=[a for a in lines if a['type']=="CREATED"]
retweeted=[a for a in lines if a['type']=="RETWEETED"]
quoted=[a for a in lines if a['type']=="QUOTED"]
with open('retweeted_relations_new.json', 'w') as outfile:
    json.dump(retweeted, outfile)

with open('quoted_relations_new.json', 'w') as outfile:
    json.dump(quoted, outfile)

with open('created_relations_new.json', 'w') as outfile:
    json.dump(created, outfile)