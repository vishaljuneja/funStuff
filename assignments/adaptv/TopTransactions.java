import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class TopTransactions {
	
	private HashMap<Double, List<String>> topN;
	private PriorityQueue<Double> pq;      // a heap for finding minimum at any intermediate time
	private int N;
	

	public TopTransactions(int n) {
		this.N = n;
		topN = new HashMap<Double, List<String>>();
		pq = new PriorityQueue<>();
	}

	public boolean add(String cust_id, double amount) {
		if(topN.containsKey(amount)) {
			topN.get(amount).add(cust_id);
			return true;
		} 
		
		if (pq.size() == N) {		
			if(amount < pq.peek()) return false; // ignore transaction		

			// remove minimum key from queue and hashmap
			double min_key = pq.poll();
			topN.remove(min_key);			
		}
		// add the new entry
		List<String> list = new ArrayList<String>();
		list.add(cust_id);
		topN.put(amount, list);
		pq.add(amount);
		return true;
	}
	
	public String get() {
		StringBuilder sb = new StringBuilder();
		for (double d : getSortedKeys()) {
			List<String> cust_ids = topN.get(d);
			Collections.sort(cust_ids);
			for (String id : cust_ids) {
				sb.append(id+" "+(int) d+",");	// casting as an "ugly" way of doing decimal formatting (in order to conform with the required output format)
			}
		}
		//sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}
	
	private List<Double> getSortedKeys() {
		ArrayList<Double> keys = new ArrayList<Double>();
		for (Double i : pq) {
			keys.add(i);
		}
		Comparator<Double> comparator = Collections.reverseOrder();
		Collections.sort(keys, comparator);
		return keys;
	}
	
}
