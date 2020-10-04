from elasticsearch import Elasticsearch
from Settings.Settings import Settings
import datetime
class ElasticSearchHelper:
    """Elastic Search Basic Operations"""
    def __init__(self):
        cred = Settings('ES_CONN')
        host= cred['ip']+':'+str(cred['port'])
        userpass=cred['user']+':'+cred['pass']
        self.es = Elasticsearch([host], http_auth=userpass)
        for index in self.es.indices.get('*'):
            print(index)


    def createindex(self,index,shards,replication):
        '''Creates an index  with the given shards and replication factor'''
        request_body = {
            "settings": {
                "number_of_shards": shards,
                "number_of_replicas": replication
            }
        }
        res = self.es.indices.create(index=index, body=request_body)
        print(res)

    def deleteindex(self,index):
        '''Deletes an index '''
        res = self.es.indices.delete(index='test-index')
        print(res)

    def index_data(self,index,doc_type,data,id=None):
        '''Creates an index if not present and inserts data in that index.
        The index API adds or updates a typed JSON document in a specific index, making it searchable
        Input parameters:
        1. Index Name
        2. Document Type
        3. Document ID ( Optional)
        4. Data: JSON
        '''
        if(id):
            res = self.es.index(index=index, doc_type=doc_type, id=id, body=data)

        else:
            res = self.es.index(index=index, doc_type=doc_type, body=data)
        print(res['result'])

        pass

    def get_data(self,index, doc_type, id, filter_fields=[]):
        '''Gets Data from an index
        Input parameters:
        1. Index Name
        2. Document Type
        3. Document ID
        4. Filter Fields (optional): Accepts an array of fields and only those fields are returned in the result.
        '''
        if len(filter_fields)>0:
            res = self.es.get(index=index, doc_type=doc_type, id=id,_source=filter_fields)
        else:
            res = self.es.get(index=index, doc_type=doc_type, id=id)
        print(res['_source'])

    def delete_data(self,index, doc_type, id):
        '''Delete doc  from an index
                Input parameters:/elastic/elasticsearch-py/issues/470
                1. Index Name
                2. Document Type
                3. Document ID'''
        res = self.es.delete(index=index,doc_type=doc_type,id=id)
        print(res['result'])

    def update_data(self,index, doc_type, id,body_doc):
        '''Updates a doc  within an index
            Input parameters:
            1. Index Name
            2. Document Type
            3. Document ID
            4. Body Document: Json document having fields to be updated in the document
            '''
        res= self.es.update
        res = self.es.update(index=index, doc_type=doc_type, id=id,body={"doc": body_doc})
        print(res)

#a = ElasticSearchHelper()
# #a.CreateIndex('testindex',3,4)
# doc = {
#     'author': 'kimcho',
#     'text': 'Elasticsearch: cool. bonsai cool.',
#     'timestamp': datetime.datetime.now(),
# }
# a.index_data('testindex','twitter',data=doc,id=2)
# a.get_data('testindex','twitter',id=1,filter_fields=['author','text'])
# #a.delete_data('testindex','twitter',id=2)
# a.update_data('testindex','twitter',id=1,body_doc={"author": 'rome', "text": 'Again Changed'})