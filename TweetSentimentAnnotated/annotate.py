import re
import os
import sys
import json

class CombineOutputs:
    def __init__(self, base_dir, outputpath):
        self.base_dir = base_dir
        self.mr_output_regex = re.compile(r'^(part)')
        self.outputpath = outputpath

    def combineFiles(self):
        with open(self.outputpath, "w", encoding='utf-8') as outfile:
            for dirpath, dirnames, filenames in os.walk(self.base_dir):
                for filename in filenames:
                    match = self.mr_output_regex.match(filename)
                    if (match):
                        path = dirpath + "/" + filename
                        with open(path, "r", encoding='utf-8') as infile:
                            for line in infile:
                                outfile.write(line)
                        outfile.write("\n")


class UserPrompt:
    def __init__(self, inputpath):
        self.inputpath = inputpath
        self.results = {}

    def prompt(self):
        results = []
        counter = 1
        with open(self.inputpath, "r") as infile:
            for line in infile:
                result = {}
                line = line.strip()
                print()
                if(len(line) >0):
                    analyticsjson = json.loads(line)
                    tweetText  = analyticsjson['tweets_text']
                    index = int(input("The tweet is: " + tweetText +"\n"+ "Annotate the tweet with  one of these options (Very negative = 0 ,Negative = 1,Neutral = 2 ,Positive = 3 ,Very positive = 4 \n"))
                    while True:
                        if index not in [0,1,2,3,4]:
                            input("Not a valid annotation.Enter the value again")
                        else:
                            break

                    result["stanford_sentiment"] = analyticsjson['sentiment']
                    result["annotated_sentiment"] = index
                    result["tweets_text"] =tweetText
                    print('*' * 50)
                    results.append(result)
                    counter = counter + 1
                if counter > 3:
                    break
        return results

    def calculate_accuracy(self,output_file):
        correct = 0
        output_json = json.load(open(output_file))
        total_predictions =len(output_json)
        for each_tweet in output_json:
                # print(each_tweet['stanford_sentiment'],each_tweet['annotated_sentiment'])
                if each_tweet['stanford_sentiment'] == each_tweet['annotated_sentiment']:
                    correct +=1
        accuracy=(correct/total_predictions) * 100
        return accuracy




inputdir = sys.argv[1]
combinedpath = sys.argv[2]
outputpath = sys.argv[3]

c = CombineOutputs(inputdir, combinedpath)
c.combineFiles()

u = UserPrompt(combinedpath)
results = u.prompt()
with open(outputpath,'w') as out:
    json.dump(results, out)

accuracy = u.calculate_accuracy(outputpath)
print(accuracy)


