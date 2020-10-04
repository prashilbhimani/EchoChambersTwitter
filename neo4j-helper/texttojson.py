import json
f = open('dummy.txt', 'r+')
lines=[]
for line in f.readlines():
    newline=json.loads(line)
    if (newline['country_code']):
        newline['country_code']=newline['country_code']
    else:
        newline['country_code']="NULL"
    lines.append(newline)
# lines = [json.loads(line) for line in f.readlines()]
f.close()
with open('data1.json', 'w') as outfile:
    json.dump(lines, outfile)