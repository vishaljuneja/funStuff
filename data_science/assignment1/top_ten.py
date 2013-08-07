import sys
import json

def lines(fp):
    print str(len(fp.readlines()))

def parseTweets(fp):
    tweets = []
    total = 0
    for l in fp:
        tw = json.loads(l)
        if tw.has_key("entities"):
            tweets.append(tw)
            total += 1
    return tweets

def parseLanguageWise(tweets):
    result = []
    total = 0
    for tw in tweets:
        if tw.has_key("lang"):
            total += 1
            if tw["lang"]=="en":
                result.append(tw)
    return result

def main():
    tweet_file = open(sys.argv[1])
    tweets = parseTweets(tweet_file)
    tag_counts = {}
    for tw in tweets:
        for tag in tw["entities"]["hashtags"]:
            text = tag["text"].encode('utf-8')
            num = len(tag["indices"])
            if tag_counts.has_key(text):
                tag_counts[text] += 1
            else:
                tag_counts[text] = 1
##    final = sorted(tag_counts.iteritems(),
##                           key=operator.itemgetter(1), reverse=True)
##    final = collections.Counter(tag_counts).most_common(10)
##    i = 1
##    for k, v in final:
##        if i>10: break
##        print k, float(v)
##        i += 1
    for hashtag in sorted(tag_counts, key=tag_counts.get, reverse=True)[:10]:
        print hashtag, float(tag_counts[hashtag]) 
        



if __name__ == '__main__':
    main()
