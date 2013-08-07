package com.coursera.nlp.translator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EMTrainerIBM1 {
	
	public static final String _NULL = "_NULL";
	public static final Integer _INITIAL = 9000;
	
	private List<Instance> data;
	
	private HashMap<String, TreeMap<String, Double>> t =
			new HashMap<String, TreeMap<String, Double>>(_INITIAL);
	
	public EMTrainerIBM1(String base, String foreign, int iterations) {
		data = PreProcessor.read(base, foreign);
		initialize();
		for(int i = 0; i<iterations; i++) {
			iterate();
		}
	}
	
	
	private void iterate() {
		// counts(e, f)
		HashMap<String, TreeMap<String, Double>> ef =
				new HashMap<String, TreeMap<String, Double>>(_INITIAL);
		// count(e)
		HashMap<String, Double> e = new HashMap<String, Double>(_INITIAL);
		
		for(Instance i : data){
			for(String f : i.foreign) {
				double t_sentence = t(f, i.base);
				// update counts for NULL
				double delta = t(f, _NULL)/t_sentence;
				updateCounts(ef, e, f, _NULL, delta);
				// update counts for other words
				for (String b : i.base) {
					delta = t(f, b)/t_sentence;
					updateCounts(ef, e, f, b, delta);
				}
			}
		}
		
		updateT(ef, e);
	}


	public void updateT(HashMap<String, TreeMap<String, Double>> ef,
			HashMap<String, Double> e) {
		
		for(String eword : t.keySet()) {
			TreeMap<String, Double> map = t.get(eword);
			for(String fword : map.keySet()) {
				double tparameter = ef.get(eword).get(fword)/e.get(eword);
				map.put(fword, tparameter);
			}
			t.put(eword, map);
		}
	}


	public void updateCounts(HashMap<String, TreeMap<String, Double>> ef,
			HashMap<String, Double> e, String fword, String eword, double delta) {
		
		double ecount = delta;
		if(e.containsKey(eword)){
			ecount += e.get(eword);
		}
		e.put(eword, ecount);
		
		double efcount = delta;
		TreeMap<String, Double> map;
		if(ef.containsKey(eword)) {
			map = ef.get(eword);
			if(map.containsKey(fword)) 	efcount += map.get(fword);
		} else {
			map = new TreeMap<String, Double>();
		}
		map.put(fword, efcount);
		ef.put(eword, map);
	}


	public double t(String f, String e) {
		if(!t.containsKey(e)) {
			System.out.println(e + " is not in t map");
			System.exit(0);
		}
		
		Map<String, Double> map = t.get(e);
		if(map.containsKey(f)) {
			return map.get(f);
		}
		return 0;
	}


	public double t(String f, List<String> sentence) {
		double count = t.get(_NULL).get(f);
		for(String e : sentence){
			count += t(f, e);
		}
		return count;
	}


	private void initialize() {
		t.put(_NULL, new TreeMap<String, Double>()); // initialize NULL
		
		for(Instance i : data) {
			for(String b : i.base) {
				
				TreeMap<String, Double> map;
				if(t.containsKey(b)) {
					map = t.get(b);
				} else {
					map = new TreeMap<String, Double>();
				}
				
				for(String f : i.foreign) {
					map.put(f, 0.0);
					t.get(_NULL).put(f, 0.0);
				}
				
				t.put(b, map);
			}
		}
		
		for(Map.Entry<String, TreeMap<String, Double>> e : t.entrySet()) {
			Map<String, Double> map = e.getValue();
			Integer count = map.size();
			for(String f : map.keySet()){
				map.put(f, 1.0/count);
			}
		}
		
		System.out.println(t.size() + " distinct native words found ...");
		System.out.println(t.get(_NULL).size() + " distinct foreign words found ...");
	}
	
	public static void main(String args[]) {
		String baseFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.en";
		String foriegnFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.es";
		
		int iterations = 5;
		
		long startTime = System.currentTimeMillis();
		EMTrainerIBM1 em = new EMTrainerIBM1(baseFileName, foriegnFileName, iterations);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		System.out.println("Total time taken: "+elapsedTime/1000.0+"s" + " for "+iterations+" iterations");
		
		System.out.println(em.t("sesiones", "session"));
		//System.out.println(em.t.get("session"));
	}


	public List<Instance> getTrainingData() {
		return data;
	}
}
