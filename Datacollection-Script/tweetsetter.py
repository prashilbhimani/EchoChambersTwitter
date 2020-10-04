import os
import json
class JsonParser:
    def __init__(self, folder_loc):
        self.file_list=os.listdir(folder_loc)

    def load_file(self, file_path, count):
        file_content=''
        with open(file_path) as json_file:
            file_content = json.load(json_file)
            json_file.close()
        file = open("tweets_last/"+str(i)+".json", "w")
        for mydict in file_content:
            file.write(json.dumps(mydict)+"\n")
        file.close()


myobj = JsonParser("./tweets_last/")
i=1000
for file_name in myobj.file_list:
    print(file_name)
    myobj.load_file("tweets_last/"+file_name, i)
    i=i+1