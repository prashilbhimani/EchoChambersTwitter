import pandas
import json

# f = open('data.json')
# data = json.load(f)
# f.close()
# df =pandas.read_json(data)
# ab= df.to_csv()
# cd=""
#
# import json
# import pandas as pd
# with open('data.json', 'r') as f:
#     data = json.load(f)
# df = pd.DataFrame(data)
# df.to_csv('out.csv', sep=',',index=False)
# cd=""

import csv, json, sys

fileInput = "data1.json"
fileOutput = "data2.csv"
inputFile = open(fileInput) #open json file
outputFile = open(fileOutput, 'w') #load csv file
data = json.load(inputFile) #load json content
inputFile.close() #close the input file
output = csv.writer(outputFile) #create a csv.write
output.writerow(data[0].keys())  # header row
for row in data:
    output.writerow(row.values()) #values row