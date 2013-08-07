package com.coursera.nlp.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.coursera.nlp.json.JSONArray;
import com.coursera.nlp.utils.StringUtils;

public class CKYParser implements Parser {

	private static final String DELIMITER = "@@";
	private static final double MIN = -Double.MAX_VALUE;
	
	private HashMap<Integer, Pie> invariants;
	private static Set<String> nonterminals;
	
	private static EmmissionEstimator em;
		
	public CKYParser(String inputfile, String countsfile){
		CKYParser.em = new EmmissionEstimator(inputfile, countsfile);
		CKYParser.nonterminals = em.getNonTerminals();
		this.invariants = new HashMap<Integer, Pie>();
	}
	
	public CKYParser(){
		this.invariants = new HashMap<Integer, Pie>();
	}
	
	@Override
	public JSONArray parse(List<String> sentence) {
		fillInvariants(sentence);
		
		int i = 0;
		int j = sentence.size()-1;
		return toJSON(invariants.get(getKey(i, j, "SBARQ")), sentence);
	}

	private JSONArray toJSON(Pie pie, List<String> sentence) {
		JSONArray output = new JSONArray();
		output.put(pie.NT);
		if(pie.bpLeft == null || pie.bpRight == null){
			output.put(sentence.get(pie.start));
			return output;
		}				
		output.put(toJSON(pie.bpLeft, sentence));
		output.put(toJSON(pie.bpRight, sentence));
		return output;
	}


	private void fillInvariants(List<String> sentence) {
		int n = sentence.size();
		invariants = new HashMap<Integer, Pie>(4*n*n*nonterminals.size());
		for(int l=1; l<=n-1; l++){
			for(int i=0; i<n-l; i++){
				int j = i + l;
				
				for(String X : nonterminals){
					Integer key = getKey(i, j, X);
					double max = MIN;
					String Y = null;
					String Z = null;
					Integer split = null;
					for(String rule[] : em.getBinaryRules(X)){
						//if(!rule.contains(EmmissionEstimator.DELIMITER)) continue;	// bi-pass unary rule
						//String splits[] = StringUtils.split(rule, EmmissionEstimator.DELIMITER);
						String y = rule[0];
						String z = rule[1];
						double emmission = em.emmission(X, y, z);						
						for(int k=i; k<j; k++){	//determine best split (if any)
							double pieLeft = pie(i, k, y, sentence);
							if(!(pieLeft > MIN)) continue;
							double pieRight = pie(k+1, j, z, sentence);
							double value = emmission + pieLeft + pieRight;
							if(value>max) {
								max = value;
								Y = y;
								Z = z;
								split = k;
							}
						}						
					}
					
					Pie p = null;
					if(max > MIN){
						p = new Pie(i, j, X, max, split, 
								invariants.get(getKey(i, split, Y)), 
								invariants.get(getKey(split+1, j, Z)));
									
					} else {
						p = new Pie(i, j, X, max, null, null, null);   // no split, no backpointers
					}
					invariants.put(key, p);
				}								
			}
		}
				
	}


	private double pie(Integer i, Integer j, String X, List<String> sentence){
		Integer key = getKey(i, j, X);
		if(invariants.containsKey(key)) return invariants.get(key).value;
		if(i.equals(j)) {
			double value = em.emmission(X, sentence.get(i));
			Pie p = new Pie(i, i, X, value, -1, null, null);
			invariants.put(key, p);
			return value;
		}
		System.out.println("What am I doing here->"+i+" "+j+" "+X);
		System.exit(0);
		return MIN;
		/*
		double max = MIN;
		String Y = null;
		String Z = null;
		Integer split = null;
		for(String y : nonterminals){
			for(String z : nonterminals) {
				double emmission = em.emmission(X, y, z);
				if(emmission > MIN){
					for(int k=i; k<j; k++) {
						double pieLeft = pie(i, k, y, sentence);
						if(!(pieLeft > MIN)) continue;
						double pieRight = pie(k+1, j, z, sentence);
						double value = emmission * pieLeft * pieRight;
						if(value>max) {
							max = value;
							Y = y;
							Z = z;
							split = k;
						}
					}
				}
			}
		}
		
		if(max > MIN){
			Pie p = new Pie(i, j, X, max, split, 
					invariants.get(StringUtils.concatenate(DELIMITER, i.toString(), split.toString(), Y)), 
					invariants.get(StringUtils.concatenate(DELIMITER, split.toString(), j.toString(), Z)));
			invariants.put(key, p);			
		}
		
		return max;
		*/
	}
	
	private Integer getKey(Integer i, Integer j, String X) {
		
		return ((X.hashCode() << 16) + (i << 8) + j);
		/*
		int hash = 17;
		hash = 31*hash + i.hashCode();
		hash = 31*hash + j.hashCode();
		hash = 31*hash + X.hashCode();
		return hash;
		*/
		
		/* (this is bull-shit and will eat your application)
		 * return StringUtils.concatenate(DELIMITER, i.toString(), j.toString(), X);
		 */
	}

	private class Pie {
		public Pie(int i, int j, String X, double value, Integer split,
				Pie left, Pie right) {
			this.start = i;
			this.end = j;
			this.NT = X;
			this.value = value;
			this.split = split;
			this.bpLeft = left;
			this.bpRight = right;
		}
		
		public int start;
		public int end;
		public String NT;
		public double value;
		public Integer split;
		public Pie bpLeft;
		public Pie bpRight;
	}
	
	public static void main(String args[]){
		String input = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train.dat";
		String counts = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train.counts.out";
		CKYParser parser = new CKYParser(input, counts);
		List<String> sentence = Arrays.asList(StringUtils.split("What was the monetary value of the Nobel Peace Prize in 1989 ?", " "));
		//System.out.println(parser.pie(0, sentence.size()-1, "SBARQ", sentence));		

		long startTime = System.currentTimeMillis();
		System.out.println(parser.parse(sentence));
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("Total time taken: "+elapsedTime/1000.0+"s");
	}
	
	
}
