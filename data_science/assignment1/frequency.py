import sys
import json
import re

def lines(fp):
    print str(len(fp.readlines()))

def parseTweets(fp):
    tweets = []
    for l in fp:
        tweets.append(json.loads(l))
    return tweets

def getWords(text):
    return re.sub("[^\w]", " ", text).split()

def main():
    tweet_file = open(sys.argv[1])
    tweets = parseTweets(tweet_file)
    counts = {}
    total = 0.0
    for tw in tweets:
        words = getWords(tw["text"].encode('utf-8'))
        for w in words:
            total += 1
            val = 1
            if counts.has_key(w):
                val = counts[w] + 1
            counts.update({w:val})

    for c in counts:
        print c, counts[c]/total


if __name__ == '__main__':
    main()
