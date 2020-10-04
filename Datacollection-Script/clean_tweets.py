import json 
import os

def get_cleaned_tweet(tweet):

    cleaned_tweet = {}

    cleaned_tweet['created_at'] = tweet['created_at']
    cleaned_tweet['id'] = tweet['id']
    cleaned_tweet['user'] = {}
    cleaned_tweet['user']['id'] = tweet['user']['id']
    cleaned_tweet['user']['name'] = tweet['user']['name']
    cleaned_tweet['user']['screen_name'] = tweet['user']['screen_name']
    cleaned_tweet['user']['followers_count'] = tweet['user']['followers_count']
    cleaned_tweet['user']['friends_count'] = tweet['user']['friends_count']
    cleaned_tweet['user']['listed_count'] = tweet['user']['listed_count']
    cleaned_tweet['user']['favourites_count'] = tweet['user']['favourites_count']
    cleaned_tweet['user']['statuses_count'] = tweet['user']['statuses_count']

    try:
        if tweet["extended_tweet"]:
            cleaned_tweet["extended_tweet"]= {}
            cleaned_tweet["extended_tweet"]["full_text"] = tweet["extended_tweet"]["full_text"]
    except KeyError as e:
        cleaned_tweet["text"]=tweet["text"]
    cleaned_tweet['retweet_count'] = tweet['retweet_count']
    cleaned_tweet['quote_count'] = tweet['quote_count']
    cleaned_tweet['reply_count'] = tweet['reply_count']
    cleaned_tweet['favorite_count'] = tweet['favorite_count']
    cleaned_tweet['entities'] = tweet['entities']
    return cleaned_tweet


def clean_tweets( input_file, output_file):
    """
    input_file: name or path of input twitter json data where each line is a json tweet
    output_file: file name or path where cleaned twitter json data is stored (default='cleaned_tweets.json')
    """
    in_file = open(input_file, 'r')
    out_file = open(output_file, 'w')

    while True:
        line = in_file.readline()
        if line == '' :
            break
        tweets_list = json.loads(line)
        cleaned_tweets_list = []
        for tweet in tweets_list:
            cleaned_tweet = get_cleaned_tweet(tweet)
            if cleaned_tweet is None:
                continue
            if 'retweeted_status' in tweet:
                cleaned_tweet['retweeted_status'] = get_cleaned_tweet(tweet['retweeted_status'])
                if cleaned_tweet['retweeted_status'] is None:
                    continue
            if 'quoted_status' in tweet:
                cleaned_tweet['quoted_status'] = get_cleaned_tweet(tweet['quoted_status'])
                if cleaned_tweet['quoted_status'] is None:
                    continue
            cleaned_tweets_list.append(cleaned_tweet)
        out_file.write(json.dumps(cleaned_tweets_list))
    in_file.close()
    out_file.close()


def main():
    files_dir= r"C:\Users\anupm\Desktop\MyFiles\courses\IS-qin\Repo\IndependentStudy\Datacollection-Script\tweets"
    for each_file in os.listdir(files_dir):
        if each_file.endswith('.json'):
            clean_tweets(os.path.join(files_dir,each_file), os.path.join(files_dir,'cleaned_'+each_file))

if __name__ == "__main__":
    main()