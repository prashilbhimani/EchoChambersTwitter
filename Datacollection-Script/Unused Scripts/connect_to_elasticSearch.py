import json
import requests
from elasticsearch import Elasticsearch

# https://curl.trillworks.com/

# This was a simple function written by Rohit and Sharan to test ElasticSearch-Python connectivity.

#headers = {
#    'Content-Type': 'application/json',
#}
#
#data = '{"user" : "kimchy",    "post_date" : "2009-11-15T14:12:12",   "message" : "trying out Elasticsearch"}'
#jsondata = json.loads(data);
#
#response = requests.put('http://localhost:9200/twitter/_doc/1', headers=headers, data=jsondata)

tweets = json.loads('./tweet.json')
print(tweets)
#es = Elasticsearch([{'host': 'localhost', 'port': 9200}])

#print(response.json())