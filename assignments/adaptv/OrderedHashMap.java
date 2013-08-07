import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrderedHashMap {
	
	private TreeMap<Integer, List<String>> sorted;
	
	public OrderedHashMap(HashMap<String, Integer> original) {
		sorted = new TreeMap<Integer, List<String>>();
		// convert original into treemap
		for(Map.Entry<String, Integer> entry : original.entrySet()) {
			String id = entry.getKey();
			Integer val = entry.getValue();
			List<String> l;
			if(sorted.containsKey(val)) {
				l = sorted.get(val);
			} else {
				l = new ArrayList<String>();
			}
			l.add(id);
			sorted.put(val, l);
		}
	}
	
	
	public String getTop(int n) {
		int num = 0;
		StringBuilder sb = new StringBuilder();
		while (num < n && !sorted.isEmpty()) {
			Integer key = sorted.lastKey();
			List<String> vals = sorted.get(key);
			Collections.sort(vals);
			for (String val : vals) {
				sb.append(val+ " " + key + ",");
			}
			num += 1;
			sorted.remove(key);
		}
		return sb.toString();
	}
	
	public String getBottom(int n) {
		int num = 0;
		StringBuilder sb = new StringBuilder();
		while (num < n && !sorted.isEmpty()) {
			Integer key = sorted.firstKey();
			List<String> vals = sorted.get(key);
			Collections.sort(vals);
			for (String val : vals) {
				sb.append(val+ " " + key + ",");
			}
			num += 1;
			sorted.remove(key);
		}
		return sb.toString();
	}
	
}
