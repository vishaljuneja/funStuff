import MapReduce
import sys

mr = MapReduce.MapReduce()

def mapper(record):
    matrix = record[0]
    value = record[3]
    if matrix == "a":
        for i in range(5):
            mr.emit_intermediate((record[1], i), (record[2], value))
    elif matrix == "b":
        for i in range(5):
            mr.emit_intermediate((i, record[2]), (record[1], value))

def reducer(key, list_of_values):
    last = (-1, 0)
    total = 0
    for t in sorted(list_of_values):
        if t[0] == last[0]:
            total += last[1] * t[1]
        last = t
    mr.emit((key[0], key[1], total))
            

if __name__ == "__main__" :
    inputdata = open(sys.argv[1])
    mr.execute(inputdata, mapper, reducer)
