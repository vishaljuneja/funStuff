import urllib
import json

def readTweets(query, num):
    """ Reads data given query """
    uri = "http://search.twitter.com/search.json"
    url = uri + "?q=" + query
    response = urllib.urlopen(url)
    results = json.load(response)
    readPage(results)
    for i in range(num-1):
        response = urllib.urlopen(uri+results["next_page"])
        results = json.load(response)
        readPage(results)
    

def readPage(results):
    for tweet in results["results"]:
        print tweet["text"]
