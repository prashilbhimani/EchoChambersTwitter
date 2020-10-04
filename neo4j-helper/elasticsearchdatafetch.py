import json
import elasticsearch
import elasticsearch.helpers
es = elasticsearch.Elasticsearch([{'host': 'localhost', 'port': 9200}])
results = elasticsearch.helpers.scan(es,
    index="mentions",
    # doc_type="my_document",
    preserve_order=True,
    query={"query": {"match_all": {}}},
)
mydata=[]
for item in results:
    mydata.append(item)
with open('mentions.json', 'w') as outfile:
    json.dump(mydata, outfile)
#print(item);
   #print(item['_id'], item['_source']['name'])

