import sys
import json
import re

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
    for l in fp:
        tweets.append(json.loads(l))
    return tweets

def getWords(text):
    return re.sub("[^\w]", " ", text).split()

def main():
    sent_file = open(sys.argv[1])
    tweet_file = open(sys.argv[2])
##    lines(sent_file)
##    lines(tweet_file)
    scores = getScores(sent_file)
    tweets = parseTweets(tweet_file)
    for t in tweets:
        score = 0
        words = getWords(t["text"].encode('utf-8'))
        for w in words:
            if scores.has_key(w):
                score += scores[w]
        #print "<sentiment:{0}>".format(float(score))
        print float(score)



if __name__ == '__main__':
    main()
