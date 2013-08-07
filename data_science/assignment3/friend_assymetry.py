import MapReduce
import sys

mr = MapReduce.MapReduce()

def mapper(record):
    key = tuple(sorted((record[0], record[1])))
    value = 1
    mr.emit_intermediate(key, 1)

def reducer(key, list_of_values):
    if len(list_of_values) == 1:
        mr.emit((key[0], key[1]))
        mr.emit((key[1], key[0]))   

if __name__ == "__main__" :
    inputdata = open(sys.argv[1])
    mr.execute(inputdata, mapper, reducer)
