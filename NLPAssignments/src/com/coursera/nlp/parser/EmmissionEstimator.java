package com.coursera.nlp.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.coursera.nlp.exceptions.RunTimeException;
import com.coursera.nlp.utils.StringUtils;

public class EmmissionEstimator {
	
	public static final String DELIMITER = "@@";
	private HashSet<String> rare;
	private Set<String> allwords;
	
	private HashMap<String, Set<String[]>> rulescache = new HashMap<String, Set<String[]>>();
	
	private HashMap<String, TreeMap<String, TreeMap<String, Integer>>> rulecounts = 
			new HashMap<String, TreeMap<String, TreeMap<String, Integer>>>();
	private HashMap<String, Integer> counts = new HashMap<String, Integer>();
	
	public EmmissionEstimator(String inputfile, String countsfile){
		this.rare = PreProcessor.getRareWords(inputfile);
		this.allwords = PreProcessor.getAllWords(inputfile);
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(countsfile));
			String str = br.readLine();
			while(str!=null){
				String splits[] = StringUtils.split(str, " ");
				String category = splits[1];
				String X = splits[2];
			    Integer count = Integer.parseInt(splits[0]);
				if(category.equals("NONTERMINAL")){
					counts.put(X, count);
				} else {
					/*
					String key;
					if(category.equals("UNARYRULE")){
						//key = splits[3];
						key = getKey(splits)
					} else {
						//key = StringUtils.concatenate(DELIMITER, splits[3], splits[4]);
						key = getKey(splits[3], splits[4]);
					}
										
					TreeMap<String, Integer> map;
					if(rulecounts.containsKey(X)){
						map = rulecounts.get(X);
					} else {
						map = new TreeMap<String, Integer>();
					}
					map.put(key, count);
					rulecounts.put(X, map);
					*/
					
					String Y = splits[3];
					String Z;
					
					if(category.equals("UNARYRULE")){
						Z = "UNARY";
					} else {
						Z = splits[4];
					}
					
					TreeMap<String, TreeMap<String, Integer>> mapX;
					TreeMap<String, Integer> mapY;
					
					if(rulecounts.containsKey(X)){
						mapX = rulecounts.get(X);
					} else {
						mapX = new TreeMap<String, TreeMap<String, Integer>>();
					}
					
					
					if(mapX.containsKey(Y)) {
						mapY = mapX.get(Y);
					} else {
						mapY = new TreeMap<String, Integer>();
					}
					mapY.put(Z, count);
					mapX.put(Y, mapY);
					rulecounts.put(X, mapX);
				}
				str = br.readLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		// assert here
		/*
		for(Map.Entry<String, TreeMap<String,Integer>> e : rulecounts.entrySet()){
			int count = counts.get(e.getKey());
			int total = 0;
			for(Map.Entry<String,Integer> e1 : e.getValue().entrySet()){
				total = total + e1.getValue();
			}
			if(total!=count) System.exit(0);
		}
		*/
	}
	
	
	private boolean isUnseen(String w) {
		if(allwords.contains(w)) return false;
		return true;
	}

	private Integer count(String X, String Y, String Z){
		if(rulecounts.containsKey(X)){			
			Map<String, TreeMap<String, Integer>> mapX = rulecounts.get(X);
			if(mapX.containsKey(Y)) {
				Map<String, Integer> mapY = mapX.get(Y);
				if(mapY.containsKey(Z)){
					return mapY.get(Z);
				} else {
					return 0;
				}
			} else {
				return 0;
			}			
		} else {
			return 0;
		}
	}
	
	private Integer count(String X, String w){
		/*
		if(rulecounts.containsKey(X)){
			String key = w;
			Map<String, Integer> map = rulecounts.get(X);
			if(map.containsKey(key)){
				return map.get(key);
			} else {
				return 0;
			}
		} else {
			return 0;
		}
		*/
		return count(X, w, "UNARY");
	}
	
	private Integer count(String X){
		if(counts.containsKey(X)){
			return counts.get(X);
		} else {
			return 0;
		}
	}
	
	public double emmission(String X, String Y1, String Y2){
		int c1 = count(X, Y1, Y2);
		int c2 = count(X);
		if(c2==0)
			try {
				throw new RunTimeException("Unseen nonterminal "+X);
			} catch (RunTimeException e) {
				e.printStackTrace();
				System.exit(0);
			}
		
		return Math.log(c1) - Math.log(c2);
	}
	
	public double emmission(String X, String w){
		if(rare.contains(w) || isUnseen(w)) w = "_RARE_";
		int c1 = count(X, w);
		int c2 = count(X);
		if(c2==0)
			try {
				throw new RunTimeException("Unseen nonterminal "+X);
			} catch (RunTimeException e) {
				e.printStackTrace();
				System.exit(0);
			}
		
		return Math.log(c1) - Math.log(c2);
	}

	public static void main(String args[]){
		String input = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train.dat";
		String counts = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train.counts.out";
		EmmissionEstimator em = new EmmissionEstimator(input, counts);
		System.out.println(em.count("NP", "PRON", "NOUN")+ " " + em.emmission("NP", "PRON", "NOUN")
				+ " " + em.count("NP"));
		for(String nt: em.getNonTerminals()) {	
			em.getBinaryRules(nt);
		}
	}


	public Set<String> getNonTerminals() {
		return counts.keySet();
	}
	
	public Set<String[]> getBinaryRules(String nt) {
		if(rulescache.containsKey(nt)) return rulescache.get(nt);
		Set<String[]> result = new HashSet<String[]>();
		Map<String, TreeMap<String, Integer>> mapX = rulecounts.get(nt);
		for(String Y: mapX.keySet()) {
			Map<String, Integer> mapY = mapX.get(Y);
			for(String Z: mapY.keySet()){
				if(!Z.equals("UNARY")) {
					//if(!counts.containsKey(Y) || !counts.containsKey(Z)) System.out.println(nt+" "+Y+" "+Z);
					String[] rule = new String[2];
					rule[0] = Y;
					rule[1] = Z;
					result.add(rule);
				}
			}
		}
		rulescache.put(nt, result);
		return result;
	}
	
}
