import MapReduce
import sys

_ORDER_ = "order"
_LINE_ITEM_ = "line_item"

mr = MapReduce.MapReduce()

def mapper(record):
    key = record[1]
    value = record
    mr.emit_intermediate(key, value)

def reducer(key, list_of_values):
    order = []
    line_item = []
    for l in list_of_values:
        if l[0] == _ORDER_ :
            order.append(l)
        elif l[0] == _LINE_ITEM_ :
            line_item.append(l)

    for o in order:
        for li in line_item:
            result = []
            result.extend(o)
            result.extend(li)
            mr.emit(result)

if __name__ == "__main__":
    inputdata = open(sys.argv[1])
    mr.execute(inputdata, mapper, reducer)
