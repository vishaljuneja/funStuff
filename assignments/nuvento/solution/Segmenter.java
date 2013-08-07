import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Nice assignment. Thanks!
 * 
 * This class implements the Viterbi (Dynamic Programming) algorithm to find the 
 * most likely sequence of segmentations in a hashtag. Google n-gram corpus is 
 * used to compute the likelihood of unigrams. UnigramEstimator has the
 * required emmission parameters to compute the splits.
 * 
 * Q) Can I obtain better splits?
 * A) Yes, the model does not incorporate bigram counts. They can be incorporated
 * 	  to obtain higher accuracy.
 * 
 * Q) Can I obtain better splits on our data?
 * A) Yes, the data does contain some Hindi words. I would collect data from a live 
 * 	  twitter stream (filtered on country=India) to collect some more data to better tackle
 * 	  such cases.
 * 
 * Q) Is there another way to solve this problem?
 * A) Yes ofcourse, a simple but less efficient brute-force approach would do as well (specially 
 *    if one do not care to tackle ambiguous cases). Supervised approaches might be considered
 *    if annotations can be obtained. (mechanical turk!!!).
 *    Mr Srinivasan http://dl.acm.org/citation.cfm?id=2398410 might add some interesting approaches.
 *    
 * Other comments: Probably a development set of a gold standard (with baselines?) might have been provided
 * 				   so that one could get motivated/think about/compare more complex approaches.
 * 
 * Aside: Sometimes I wonder if any technical person even reads these solutions and in what details?
 * 
 * @author vishal
 */
public class Segmenter {
	
	// used for memorization
	private HashMap<Integer, List<Integer>> pie_splits;
	private HashMap<Integer, Double> pie_probs;
	
	public List<String> segmentHashTag(String hashtag) {
		
		List<String> result = new ArrayList<String>();
		for(String s : preprocess(hashtag)) {
			// initialize maps
			pie_splits = new HashMap<Integer, List<Integer>>();
			pie_probs = new HashMap<Integer, Double>();
			
			result.addAll(toStringList(s, segment(s, 0)));	// segment tag
		}
		
		return result;
	}
	
	// ---------------------------------> private functions
	
	/**
	 * preprocesses the hashtag
	 * @param hashtag
	 * @return
	 */
	private List<String> preprocess(String hashtag) {
		// trim
		hashtag = hashtag.trim();
		// to lower case
		hashtag = hashtag.toLowerCase();
		// remove punctuations #,-,. etc
		hashtag = filterPunc(hashtag);
		// separate numbers
		return splitNumeric(hashtag);
	}


	/**
	 * 1000colorsofindia is returned as list of ["1000", "colorsofindia"]
	 * each tag is processed separately in the next stage
	 * @param hashtag
	 * @return
	 */
	private List<String> splitNumeric(String hashtag) {
		List<String> parts = new ArrayList<String>();
		Matcher matcher = Pattern.compile("-?\\d+").matcher(hashtag);
		int begin = 0;
		while(matcher.find()) {
			int start = matcher.start();
			if(start!=0) parts.add(hashtag.substring(begin, start));
			parts.add(matcher.group());
			begin = matcher.end();
		}
		if(begin <= hashtag.length() - 1) parts.add(hashtag.substring(begin, hashtag.length()));
		return parts;
	}


	private String filterPunc(String hashtag) {
		StringBuilder sb = new StringBuilder();
		for(String s : hashtag.split("\\W+")) {
			sb.append(s);
		}
		return sb.toString();
	}


	private List<String> toStringList(String hashtag, List<Integer> segments) {
		List<String> result = new ArrayList<String>();
		result.add(hashtag.substring(0, segments.get(0) + 1));	// add first word
		for(int i = 0; i < segments.size() - 1; i++) {
			result.add(hashtag.substring(segments.get(i) + 1, segments.get(i + 1) + 1));
		}
		return result;
	}

	// -------------> the following 2 functions largely implement the Viterbi algorithm
	
	private List<Integer> segment(String sequence, int index) {
		if(pie_splits.containsKey(index)) return pie_splits.get(index);
		
		ArrayList<ArrayList<Integer>> candidates = new ArrayList<ArrayList<Integer>>();
		for (int i = index; i < sequence.length(); i++) {
			ArrayList<Integer> splits = new ArrayList<Integer>();
			splits.add(i);	// add first
			if(i != sequence.length() - 1) {
				splits.addAll(segment(sequence, i + 1));
			}
			candidates.add(splits);
		}
		return findMax(sequence, index, candidates);
	}



	private List<Integer> findMax(String sequence, int index, ArrayList<ArrayList<Integer>> candidates) {
		List<Integer> max_candidate = null;
		double max_prob = -Integer.MIN_VALUE;
		for(List<Integer> candidate : candidates) {
			int first = candidate.get(0);
			double prob_sum = UnigramEstimator.getEmmission(sequence.substring(index, first + 1));
			if(first  != sequence.length() - 1)
				prob_sum += pie_probs.get(first+1);		// the sweet spot of Dynamic Programming algorithm
			if(prob_sum > max_prob) {
				max_prob = prob_sum;
				max_candidate = candidate;
			}
		}
		//update pie tables
		pie_probs.put(index, max_prob);
		pie_splits.put(index, max_candidate);
		return max_candidate;
	}



	public static void main(String[] args) {
		
		Segmenter segmenter = new Segmenter();
		StringBuffer sb = new StringBuffer();
		for (String s: segmenter.segmentHashTag("expertsexchange")) {
			sb.append(s+ " ");
		}
		System.out.println(sb.toString());
		
	}
	
}
