package com.coursera.nlp.translator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class EMTrainerIBM2Maps implements Serializable {


	private static final long serialVersionUID = 1L;
	private EMTrainerIBM1 em1;
	private List<Instance> data;
	
	// key = hash of (j,i,l,m)     j->index of english; i->index of foreign
	// value = count
	private HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>> q = 
			new HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>>();
	
	
	
	/**
	 * @param base
	 * @param foreign
	 * @param iterations1 iterations for model 1
	 * @param iterations2 iterations for model 2
	 */
	public EMTrainerIBM2Maps(String base, String foreign, int iterations1, int iterations2) {
		em1 = new EMTrainerIBM1(base, foreign, iterations1);
		data = em1.getTrainingData();
		initialize();
		for(int i=0; i<iterations2; i++) {
			iterate();
			System.out.println("Model 2 iteration "+(i+1)+" completed ...");
		}
	}

	
	private void iterate() {
		// (j,i,l,m) & (i,l,m) counts
		HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>> jilm = 
				new HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>>();
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> ilm =
				new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		
		// counts(e, f)
				HashMap<String, TreeMap<String, Double>> ef =
						new HashMap<String, TreeMap<String, Double>>(EMTrainerIBM1._INITIAL);
		// count(e)
				HashMap<String, Double> e = new HashMap<String, Double>(EMTrainerIBM1._INITIAL);
		
		for(Instance i : data){
			int findex = 1;
			
			int flength = i.foreign.size();
			int blength = i.base.size();
			for(String f : i.foreign) {
				double qt_sentence = qt(findex, blength, flength, f, i.base);
				// update counts for NULL
				double delta = qt(0, findex, blength, flength, f, EMTrainerIBM1._NULL)/qt_sentence;
				updateCounts(jilm, ilm, 0, findex, blength, flength , ef, e, f, EMTrainerIBM1._NULL, delta);
				
				// update counts for other words
				int bindex = 1;
				for (String b : i.base) {
					delta = qt(bindex, findex, blength, flength, f, b)/qt_sentence;
					updateCounts(jilm, ilm, bindex, findex, blength, flength , ef, e, f, b, delta);
					bindex++;
				}
				findex++;
			}
		}
		updateQT(jilm, ilm, ef, e);
		
	}
	
	
	
	private void updateQT(
			HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>> jilm,
			HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> ilm,
			HashMap<String, TreeMap<String, Double>> ef,
			HashMap<String, Double> e) {

		em1.updateT(ef, e);

		// update q
		for(int m : jilm.keySet()){			
			HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> mapL = 
					jilm.get(m);
			
			for(int l : mapL.keySet()){
				HashMap<Integer, HashMap<Integer, Double>> mapI = 
						mapL.get(l);
				
				for(int i : mapI.keySet()){
					HashMap<Integer, Double> mapJ = mapI.get(i);				
					double cilm = ilm.get(m).get(l).get(i);
					
					for(int j : mapJ.keySet()){
						double cjilm = jilm.get(m).get(l).get(i).get(j);
						//System.out.println(cjilm/cilm);
						insertQ(j, i, l, m, cjilm/cilm);
						//System.out.println(q.get(m).get(l).get(i).get(j));
					}
				}
			}
		}

	}



	private void updateCounts(
			HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>> jilm,
			HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> ilm,
			int j, int i, int l, int m,
			HashMap<String, TreeMap<String, Double>> ef,
			HashMap<String, Double> e, String fword, String eword, double delta) {
		
		em1.updateCounts(ef, e, fword, eword, delta);
		/*
		try {
			System.out.println("Before "  + jilm.get(m).get(l).get(i).get(j));
		} catch (Exception ex) {}
		
		
		
		try {
			System.out.println("After " + jilm.get(m).get(l).get(i).get(j));
		} catch (Exception ex) {}
		*/
		
		updateJILM(j, i, l, m, delta, jilm);
		
		updateILM(i, l, m, delta, ilm);
	}



	private double qt(int i, int l, int m, String f, List<String> sentence) {
		double total = qt(0, i, l, m, f, EMTrainerIBM1._NULL);
		int j = 1;
		for(String e : sentence){
			total += qt(j, i, l, m, f, e);
			j++;
		}
		return total;
	}
	
	public double qt(int j, int i, int l, int m, String f, String e) {
		double qparam = q.get(m).get(l).get(i).get(j);
		double tparam = em1.t(f, e);
		return qparam * tparam;
	}
	
	
	private void initialize() {
		//int fmax = 0;
		//int bmax = 0;
		for(Instance instance : data) {
			int lforeign = instance.foreign.size(); // length of foreign
			int lbase = instance.base.size();  // length of base
			
			//if(lforeign>fmax) fmax = lforeign;
			//if(lbase>bmax) bmax = lbase;
			
			double val = 1.0/(lbase+1);
			for(int i=1 ; i<=lforeign; i++) {
				for(int j=0; j<=lbase; j++) {
					//q.put(hash(j,i,lbase,lforeign), 1.0/(lbase+1));
					insertQ(j, i, lbase, lforeign, val);
				}
			}
		}
		System.out.println(q.size()+" q parameters initialized ... ");
		//System.out.println(fmax + " is max length of a foreign sentence ... ");
		//System.out.println(bmax + " is max length of a native sentence ... ");
	}
	
	public static void main(String args[]) {
		String baseFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.en";
		String foriegnFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.es";
		
		int iterations1 = 5;
		int iterations2 = 5;
		
		long startTime = System.currentTimeMillis();
		EMTrainerIBM2Maps em = new EMTrainerIBM2Maps(baseFileName, foriegnFileName, iterations1, iterations2);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		System.out.println("Total time taken: "+elapsedTime/1000.0+"s" + " for "+iterations2+" iterations");
	}
	

	
	
	
	private void insertQ(int j, int i, int l, int m, double val) {
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> mapL;
		if(q.containsKey(m)) {
			mapL = q.get(m);
		} else {
			mapL = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		} 
		
		HashMap<Integer, HashMap<Integer, Double>> mapI;
		if(mapL.containsKey(l)) {
			mapI = mapL.get(l);
		} else {
			mapI = new HashMap<Integer, HashMap<Integer, Double>>();
		}
		
		HashMap<Integer, Double> mapJ;
		if(mapI.containsKey(i)) {
			mapJ = mapI.get(i);
		} else {
			mapJ = new HashMap<Integer, Double>();
		}
		
		mapJ.put(j, val);
		mapI.put(i, mapJ);
		mapL.put(l, mapI);
		q.put(m, mapL);
		
	}
	
	
	private void updateJILM(int j, int i, int l, int m, double delta, 
			HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>> jilm) {
		HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> mapL;
		if(jilm.containsKey(m)) {
			mapL = jilm.get(m);
		} else {
			mapL = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
		} 
		
		HashMap<Integer, HashMap<Integer, Double>> mapI;
		if(mapL.containsKey(l)) {
			mapI = mapL.get(l);
		} else {
			mapI = new HashMap<Integer, HashMap<Integer, Double>>();
		}
		
		HashMap<Integer, Double> mapJ;
		if(mapI.containsKey(i)) {
			mapJ = mapI.get(i);
		} else {
			mapJ = new HashMap<Integer, Double>();
		}
		
		double val = delta;
		if(mapJ.containsKey(j)) {
			val += mapJ.get(j);
		}
		
		mapJ.put(j, val);
		mapI.put(i, mapJ);
		mapL.put(l, mapI);
		jilm.put(m, mapL);
		
	}
	
	private void updateILM(int i, int l, int m, double delta, 
			HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> ilm) {
		 
		HashMap<Integer, HashMap<Integer, Double>> mapL;
		if(ilm.containsKey(m)) {
			mapL = ilm.get(m);
		} else {
			mapL = new HashMap<Integer, HashMap<Integer, Double>>();
		} 
		
		HashMap<Integer, Double> mapI;
		if(mapL.containsKey(l)) {
			mapI = mapL.get(l);
		} else {
			mapI = new HashMap<Integer, Double>();
		}
		
		double val = delta;
		if(mapI.containsKey(i)) {
			val += mapI.get(i);
		}
		
		mapI.put(i, val);
		mapL.put(l, mapI);
		ilm.put(m, mapL);
		
	}
	
}
