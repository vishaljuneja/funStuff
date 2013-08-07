import sys
import json
import re

LANGUAGE = "en"
LANGUAGE_KEY = "lang"

PLACE_KEY = "place"
COUNTRY_KEY = "country_code"
COUNTRY_CODE = "US"
STATE_KEY = "full_name"
states = {'AK': 'Alaska','AL': 'Alabama','AR': 'Arkansas','AS': 'American Samoa',
        'AZ': 'Arizona', 'CA': 'California','CO': 'Colorado', 'CT': 'Connecticut',
        'DC': 'District of Columbia', 'DE': 'Delaware','FL': 'Florida','GA': 'Georgia',
        'GU': 'Guam','HI': 'Hawaii','IA': 'Iowa','ID': 'Idaho','IL': 'Illinois',
        'IN': 'Indiana','KS': 'Kansas','KY': 'Kentucky','LA': 'Louisiana','MA': 'Massachusetts',
        'MD': 'Maryland','ME': 'Maine','MI': 'Michigan','MN': 'Minnesota','MO': 'Missouri',
        'MP': 'Northern Mariana Islands','MS': 'Mississippi','MT': 'Montana','NA': 'National',
        'NC': 'North Carolina','ND': 'North Dakota','NE': 'Nebraska','NH': 'New Hampshire',
        'NJ': 'New Jersey','NM': 'New Mexico','NV': 'Nevada','NY': 'New York','OH': 'Ohio',
        'OK': 'Oklahoma','OR': 'Oregon','PA': 'Pennsylvania','PR': 'Puerto Rico',
        'RI': 'Rhode Island','SC': 'South Carolina','SD': 'South Dakota','TN': 'Tennessee',
        'TX': 'Texas','UT': 'Utah','VA': 'Virginia','VI': 'Virgin Islands','VT': 'Vermont','WA': 'Washington',
        'WI': 'Wisconsin','WV': 'West Virginia','WY': 'Wyoming'
}


def lines(fp):
    print str(len(fp.readlines()))

def getScores(fp):
    scores = {}
    for l in fp:
        term, score = l.split("\t")
        scores[term] = int(score)
    return scores

def parseTweets(fp):
    tweets = []
    total = 0
    for l in fp:
        total += 1
        tw = json.loads(l)
        tweets.append(tw)
    return tweets

def parseLanguageWise(tweets):
    result = []
    total = 0
    for tw in tweets:
        if tw.has_key(LANGUAGE_KEY):
            total += 1
            if tw[LANGUAGE_KEY]==LANGUAGE:
                result.append(tw)
    return result

def parseCountryWise(tweets):
    result = []
    total = 0
    for tw in tweets:
        if tw.has_key(PLACE_KEY):
            if tw[PLACE_KEY] is not None:
                total += 1
                if tw[PLACE_KEY][COUNTRY_KEY] == COUNTRY_CODE:
                    result.append(tw)                    
    return result

def binStateWise(tweets):
    abbv = dict((v,k) for k, v in states.iteritems())
    result = {}
    for tw in tweets:
        city, state = tw[PLACE_KEY][STATE_KEY].split(', ')
        if state == COUNTRY_CODE:
            if abbv.has_key(city):
                code = abbv[city]
            else:
                continue
        else:
            code = state
        if result.has_key(code):
            result[code].append(tw)
        else:
            result[code] = [tw]
    return result

def happiest(state_tweets, scores):
    max_senti = -10
    st = ""
    for state_code, tweet_list in state_tweets.iteritems():
        total = 0.0
        for tw in tweet_list:
            words = getWords(tw["text"].encode('utf-8'))
            for w in words:
                if scores.has_key(w):
                    total += scores[w]
        total = total/len(tweet_list)
        if total>max_senti:
            st = state_code
            max_senti = total
    print st

def getWords(text):
    return re.sub("[^\w]", " ", text).split()

def main():
    sent_file = open(sys.argv[1])
    tweet_file = open(sys.argv[2])
    scores = getScores(sent_file)
    all_tweets = parseTweets(tweet_file)
    english_tweets = parseLanguageWise(all_tweets)
    us_tweets = parseCountryWise(english_tweets)
    state_wise = binStateWise(us_tweets)
    happiest(state_wise, scores)
        

if __name__ == '__main__':
    main()
