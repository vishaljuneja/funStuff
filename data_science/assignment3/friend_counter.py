import MapReduce
import sys

mr = MapReduce.MapReduce()

def mapper(record):
    key = record[0]
    value = 1
    mr.emit_intermediate(key, value)

def reducer(key, list_of_values):
    total = len(list_of_values)
    mr.emit((key, total))
    

if __name__ == "__main__" :
    inputdata = open(sys.argv[1])
    mr.execute(inputdata, mapper, reducer)
