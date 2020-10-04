import json
inputFile = open("dataa.json")
data = json.load(inputFile)
modified_data=[line['_source'] for line in data]
with open('tweets.json', 'w') as outfile:
    json.dump(modified_data, outfile)
v=""