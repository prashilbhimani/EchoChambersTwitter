import base64
import requests
import urllib.parse
import json
import time
import random
import collections
import hmac
import binascii
import hashlib
from IndependentStudy.Settings.Settings import Settings
OAUTH2_TOKEN = 'https://api.twitter.com/oauth2/token'

def get_bearer_token(consumer_key, consumer_secret):
    consumer_key = urllib.parse.quote(consumer_key)
    consumer_secret = urllib.parse.quote(consumer_secret)
    bearer_token = consumer_key + ':' + consumer_secret
    base64_encoded_bearer_token = base64.b64encode(bearer_token.encode('utf-8'))
    # set headers
    headers = {
        "Authorization": "Basic " + base64_encoded_bearer_token.decode('utf-8') + "",
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        "Content-Length": "29"}

    response = requests.post(OAUTH2_TOKEN, headers=headers, data={'grant_type': 'client_credentials'})
    to_json = response.json()
    #print("token_type = %s\naccess_token  = %s" % (to_json['token_type'], to_json['access_token']))
    return to_json['access_token']

def get_tweets_count(bearer_token,Credentials,hashtag):
    headers = {
        'Authorization': 'Bearer '+bearer_token,
    }
    params = (
        ('query', hashtag),
    )
    full_url = "https://api.twitter.com/1.1/tweets/search/" + Credentials['product'] + "/" + Credentials['label'] + "counts.json"
    response = requests.get(full_url, headers=headers, params=params)
    counts_json = response.json()
    count = counts_json["totalCount"]
    return count


# def get_tweets(bearer_token, Credentials):
#     headers = {
#         'Authorization': 'Bearer '+bearer_token,
#     }
#     params = (
#         ('query', 'ufc'),
#
#         ('maxResults', Credentials['maxResults']),
#     )
#
#     full_url = "https://api.twitter.com/1.1/tweets/search/" + Credentials['product'] + "/" + Credentials['label'] + ".json"
#     response = requests.get(full_url, headers=headers, params=params)
#     # print(response)
#     tweetsJSON = response.json()
#     print(tweetsJSON)
#     print(type(tweetsJSON))
#     print(tweetsJSON['results'])
#     return tweetsJSON


def get_tweets(bearer_token, Credentials):
    next = ''
    count = 844
    while True:
        headers = {
            'Authorization': 'Bearer '+bearer_token,
        }
        if next is not "":
            params = (
                ('query', 'lang:en (climatechange OR actonclimate OR climatemarch OR climatechangeisreal OR climatejustice OR climatecontrol OR climatereality OR climatecrisis OR climatechangeeffect OR climatechangeeffects OR climatehope OR climatescience OR climaterealist OR climatechanges OR climatechangehoax OR climatenews OR climateactionnow OR climatehoax)'),
                # ('query','lang:fr (climatechange OR actonclimate)'),
                ('next',next),
                ('fromDate','201706070000'),
                ('toDate','201706120000'),
                ('maxResults', Credentials['maxResults']),
            )
        else:
            params = (
                # ('query','lang:fr (climatechange OR actonclimate)'),
                ('query', 'lang:en (climatechange OR actonclimate OR climatemarch OR climatechangeisreal OR climatejustice OR climatecontrol OR climatereality OR climatecrisis OR climatechangeeffect OR climatechangeeffects OR climatehope OR climatescience OR climaterealist OR climatechanges OR climatechangehoax OR climatenews OR climateactionnow OR climatehoax)'),
                ('fromDate','201706070000'),
                ('toDate','201706120000'),
                ('maxResults', Credentials['maxResults']),
            )

        full_url = "https://api.twitter.com/1.1/tweets/search/" + Credentials['product'] + "/" + Credentials['label'] + ".json"
        response = requests.get(full_url, headers=headers, params=params)
        if response.status_code != 200:
            break
        tweetsJSON = response.json()
        count = count+1
        tweets = tweetsJSON['results']
        with open("tweets/" + str(count) + '.json', 'w') as outfile:
            json.dump(tweets, outfile)
        if 'next' not in tweetsJSON:
            break
        next = tweetsJSON['next']
        with open("tweets/next_token"+".txt",'w') as tokenfile:
            tokenfile.write(next)
        tokenfile.close()
        if count > 1000:
            break

def printTweets(tweetsJSON):
    tweet_loc = Settings('TWEETS_LOCATION')
    tweets = tweetsJSON['results'];
    counter = 844
    for tweet in tweets:
        print(tweet)
        print(type(tweet)) # dict
        print(tweet.keys())
        counter += 1

        with open(tweet_loc['loc'] + '/' + str(counter) + '.json', 'w') as outfile:
            json.dump(tweet, outfile)


def get_nonce():
    n = base64.b64encode(''.join([str(random.randint(0, 9))
                                  for i in range(24)]).encode('UTF-8')).decode('UTF-8')
    return n


def escape(s):
    """Percent Encode the passed in string"""
    return urllib.parse.quote(s, safe='~')


def  get_oauth_parameters(consumer_key, access_token):
    oauth_parameters = {
        'oauth_timestamp': str(int(time.time())),
        'oauth_signature_method': "HMAC-SHA1",
        'oauth_version': "1.0",
        'oauth_token': access_token,
        'oauth_nonce': get_nonce(),
        'oauth_consumer_key': consumer_key
    }
    return oauth_parameters


def collect_parameters(oauth_parameters, url_parameters):
    """Combines oauth, url and status parameters"""
    #Add the oauth_parameters to temp hash
    temp = oauth_parameters.copy()
    #Add the url_parameters to the temp hash
    for k, v in url_parameters.items():
        temp[k] = v
    return temp


def stringify_parameters(parameters):
    """Orders parameters, and generates string representation of parameters"""
    output = ''
    ordered_parameters = {}
    ordered_parameters = collections.OrderedDict(sorted(parameters.items()))
    counter = 1
    for k, v in ordered_parameters.items():
        output += escape(str(k)) + '=' + escape(str(v))
        if counter < len(ordered_parameters):
            output += '&'
            counter += 1

    return output


def create_signing_key(oauth_consumer_secret, oauth_token_secret=None):
    """Create key to sign request with"""
    signing_key = escape(oauth_consumer_secret) + '&'

    if oauth_token_secret is not None:
        signing_key += escape(oauth_token_secret)

    return signing_key


def generate_signature(method, url, url_parameters, oauth_parameters,
                       oauth_consumer_key, oauth_consumer_secret,
                       oauth_token_secret=None):

    #Combine parameters into one hash
    temp = collect_parameters(oauth_parameters,  url_parameters)

    #Create string of combined url and oauth parameters
    parameter_string = stringify_parameters(temp)

    #Create your Signature Base String
    signature_base_string = (
            method.upper() + '&' +
            escape(str(url)) + '&' +
            escape(parameter_string)
    )
    signing_key = create_signing_key(oauth_consumer_secret, oauth_token_secret)
    return calculate_signature(signing_key, signature_base_string)


def calculate_signature(signing_key, signature_base_string):
    """Calculate the signature using SHA1"""
    signing_key = bytes(signing_key, encoding= 'utf-8')
    signature_base_string = bytes(signature_base_string, encoding= 'utf-8')
    hashed = hmac.new(signing_key,signature_base_string, hashlib.sha1)
    sig = binascii.b2a_base64(hashed.digest())[:-1]

    return escape(sig)


def create_auth_header(parameters):
    """For all collected parameters, order them and create auth header"""
    # ordered_parameters = {}
    ordered_parameters = collections.OrderedDict(sorted(parameters.items()))
    auth_header = (
        '%s="%s"' % (k, v) for k, v in ordered_parameters.items())
    val = "OAuth " + ', '.join(auth_header)
    return val


def get_api_response(method, url, url_parameters, consumer_key,
                     access_token, consumer_secret,access_token_secret):

    oauth_parameters = get_oauth_parameters(consumer_key,
                                            access_token)
    oauth_parameters['oauth_signature'] = generate_signature(
        method, url, url_parameters, oauth_parameters, consumer_key,
        consumer_secret,
        access_token_secret
    )
    headers = {'Authorization': create_auth_header(oauth_parameters)}
    url += '?' + urllib.parse.urlencode(url_parameters)

    response = requests.get(url, headers=headers)
    return response


def main():
    Credentials = Settings('API_CONNECTION')
    CONSUMER_KEY = Credentials['CONSUMER_KEY']
    CONSUMER_SECRET = Credentials['CONSUMER_SECRET']
    ACCESS_TOKEN = Credentials['ACCESS_TOKEN']
    ACCESS_TOKEN_SECRET = Credentials['ACCESS_TOKEN_SECRET']
    bearer_token = get_bearer_token(CONSUMER_KEY, CONSUMER_SECRET)
    tweetsJSON = get_tweets(bearer_token, Credentials)


    ############to get count of tweets###################
    # hashtags_list = [ ]
    # for each_tag in hashtags_list:
    #     count =  get_tweets_count(bearer_token,Credentials,each_tag)
    #     print("Hashtag and their count",each_tag,count)



    ##############################################
    #getting list of user followers

    method = "get"
    url = "https://api.twitter.com/1.1/friends/ids.json"
    url_parameters = {
        'screen_name': 'Anupama81986850'
    }

    ###############################################

if __name__ == "__main__":
    main()