import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class which keeps counts for all unigram counts.
 * Returns emmission parameters in terms of log of probability of occurance of a unigram.
 * @author vishal
 */

public class UnigramEstimator {
	
	public static final String INPUT_FILE = 
			"count_1w.txt";
	
	public static final String _RARE_ = "_RARE_";	// rare/unseen word token
	
	private static HashMap<String, Double> parameters;	// stores log estimators of unigram probabilities
	private static final int initial_capacity = 333333; // initial capacity of parameters hashmap

	static {
		new UnigramEstimator(UnigramEstimator.INPUT_FILE);
	}
	
	
	private UnigramEstimator(String filename) {
		parameters = new HashMap<String, Double>(initial_capacity);
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(filename));
			String str = br.readLine();
			double total = 0;
			double count = 0;	// freq of each word
			while(str!=null) {
				String[] vals = str.split("\t");
				count = Double.parseDouble(vals[1]);
				parameters.put(vals[0], count);
				total += count;
				str = br.readLine();
			}
			br.close(); // close file
			updateRare();	// _RARE_ words to handle unseen word occurances
			takeLogs(total);    // store log likelihoods to prevent underflow
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void updateRare() {
		parameters.put(_RARE_, 0.00000001);
	}

	private void takeLogs(double total) {
		for(Map.Entry<String, Double> e : parameters.entrySet()) {
			String word = e.getKey();
			Double freq = e.getValue();
			Double probability = Math.log(freq/total);
			parameters.put(word, probability);
		}
	}
	
	/**
	 * gets log(count(word)/count(allCorpusWords));
	 * @param word
	 * @return
	 */
	public static double getEmmission(String word) {
		if(parameters.containsKey(word)) {
			return parameters.get(word);
		}
		return parameters.get(_RARE_);
	}
	
}
