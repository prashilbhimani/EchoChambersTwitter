import json
import os
from google.cloud import storage
from IndependentStudy.Settings.Settings import Settings
from google.oauth2 import service_account


class googleblobstorage:
    def __init__(self):
        my_path = os.path.abspath(os.path.dirname(__file__))
        path = os.path.join(my_path, "../../../../googlecredstorage/googlestoragecred.json")
        project_id = 'community-detection-218018'
        self.location_settings = Settings('TWEETS_LOCATION')
        with open(path) as source:
            info = json.load(source)
        storage_credentials = service_account.Credentials.from_service_account_info(info)
        self.storage_client = storage.Client(project=project_id, credentials=storage_credentials)

    def create_bucket(self,bucket_name):
        bucket = self.storage_client.create_bucket(bucket_name)
        print("Bucket created with name:{0}".format(bucket_name))

    def list_bucket_objects(self,bucket_name):

        bucket = self.storage_client.get_bucket(bucket_name)

        blobs = bucket.list_blobs()

        for blob in blobs:
            print(blob.name)

    def list_buckets(self):
        for bucket in self.storage_client.list_buckets():
            print(bucket)

    def upload_to_bucket(self,bucket_name,destination_blob_name,json_source):
        bucket = self.storage_client.get_bucket(bucket_name)
        blob = bucket.blob(destination_blob_name)
        #json.dump(json_source, destination_blob_name)
        with open(destination_blob_name, 'w') as outfile:
            json.dump(json_source, outfile)
        blob.upload_from_filename(destination_blob_name)

        print('File {} uploaded .'.format(destination_blob_name))

    def upload_folder_to_bucket(self,bucket_name):
            os.system(self.location_settings['gsutil_loc']+' -m cp -r ' + self.location_settings['loc'] + ' gs://'+bucket_name)


bucket_name = 'comm-detection-bucket'
google_obj= googleblobstorage()
google_obj.upload_folder_to_bucket(bucket_name)
google_obj.upload_to_bucket(bucket_name,"my-new-blob.json",{"a":{"name":"rohit"}})
google_obj.list_buckets()
# The name for the new bucket

