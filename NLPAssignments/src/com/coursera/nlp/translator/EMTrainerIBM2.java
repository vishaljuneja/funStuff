package com.coursera.nlp.translator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EMTrainerIBM2 {

	private EMTrainerIBM1 em1;
	private List<Instance> data;
	
	// key = hash of (j,i,l,m)     j->index of english; i->index of foreign
	// value = count
	private HashMap<Integer, Double> q = 
			new HashMap<Integer, Double>();
	
	
	//private Integer q[][][][] = new Integer[100][100][100][100];
	
	
	/**
	 * @param base
	 * @param foreign
	 * @param iterations1 iterations for model 1
	 * @param iterations2 iterations for model 2
	 */
	public EMTrainerIBM2(String base, String foreign, int iterations1, int iterations2) {
		em1 = new EMTrainerIBM1(base, foreign, iterations1);
		data = em1.getTrainingData();
		initialize();
		for(int i=0; i<iterations2; i++) {
			iterate();
			System.out.println("Model 2 iteration "+i+" completed ...");
		}
	}

	private void iterate() {
		// (j,i,l,m) & (i,l,m) counts
		HashMap<JILM, Double> JILMcounts = new HashMap<JILM, Double>();
		HashMap<Integer, Double> ILMcounts = new HashMap<Integer, Double>();
		
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
				updateCounts(JILMcounts, ILMcounts, 0, findex, blength, flength , ef, e, f, EMTrainerIBM1._NULL, delta);
				
				// update counts for other words
				int bindex = 1;
				for (String b : i.base) {
					delta = qt(bindex, findex, blength, flength, f, b)/qt_sentence;
					updateCounts(JILMcounts, ILMcounts, bindex, findex, blength, flength , ef, e, f, b, delta);
					bindex++;
				}
				findex++;
			}
		}
		updateQT(JILMcounts, ILMcounts, ef, e);
		
	}

	private void updateQT(HashMap<JILM, Double> JILMcounts, HashMap<Integer, Double> ILMcounts,
			HashMap<String, TreeMap<String, Double>> ef,
			HashMap<String, Double> e) {
		
		em1.updateT(ef, e);
		
		// update q map
		for(Map.Entry<JILM, Double> entry : JILMcounts.entrySet()) {
			JILM jilm = entry.getKey();
			double val = entry.getValue()/ILMcounts.get(hash(jilm.i, jilm.l, jilm.m));
			q.put(jilm.hashCode(), val);
		}
		
	}

	private void updateCounts(HashMap<JILM, Double> JILMcounts, HashMap<Integer, Double> ILMcounts, int j,
			int i, int l, int m,
			HashMap<String, TreeMap<String, Double>> ef,
			HashMap<String, Double> e, String fword, String eword, double delta) {
		
		em1.updateCounts(ef, e, fword, eword, delta);
		
		// update j,i,l,m counts
		JILM jilm = new JILM(j, i, l, m);
		double jilmcount = delta;
		if(JILMcounts.containsKey(jilm)) {
			jilmcount += JILMcounts.get(jilm);
		}
		JILMcounts.put(jilm, jilmcount);
		
		// update i,l,m counts
		int hash = hash(i, l, m);
		double ilmcount = delta;
		if(ILMcounts.containsKey(hash)) {
			ilmcount += ILMcounts.get(hash);
		}
		ILMcounts.put(hash, ilmcount);
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
		double qParameter = 0;
		int hash = hash(j,i,l,m);
		if(q.containsKey(hash)) qParameter = q.get(hash);
		return qParameter * em1.t(f, e);
	}

	private void initialize() {
		for(Instance instance : data) {
			int lforeign = instance.foreign.size(); // length of foreign
			int lbase = instance.base.size();  // length of base/english + 1 (for NULL)
			for(int i=1 ; i<=lforeign; i++) {
				for(int j=0; j<=lbase; j++) {
					q.put(hash(j,i,lbase,lforeign), 1.0/(lbase+1));
				}
			}
		}
		System.out.println(q.size()+" q parameters initialized ... ");
	}

	private static Integer hash(Integer j, Integer i, Integer l, Integer m) {
		
		int hashCode = m.hashCode();
		hashCode += 31*l.hashCode();
		hashCode += 31*i.hashCode();
		hashCode += 31*j.hashCode();
		return hashCode;
		/*
		return ((j.hashCode() << 32) + (i << 16) + (l << 8) + m);
		*/
	}
	
	private static Integer hash(Integer i, Integer l, Integer m) {
		
		int hashCode = m.hashCode();
		hashCode += 31*l.hashCode();
		hashCode += 31*i.hashCode();
		return hashCode;
		/*
		return ((i << 16) + (l << 8) + m);
		*/
	}
	
	
	private class JILM {
		public int j;
		public int i;
		public int l;
		public int m;
		
		Integer hash = null;
		
		public JILM(int j, int i, int l, int m) {
			this.j = j;
			this.i = i;
			this.l = l;
			this.m = m;
		}
		
		public int hashCode(){
			if(hash!=null) return hash;
			
			this.hash = EMTrainerIBM2.hash(j, i, l, m);
			return hash;
		}
		
	}
	
	
	public static void main(String args[]) {
		String baseFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.en";
		String foriegnFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.es";
		
		int iterations1 = 5;
		int iterations2 = 5;
		
		long startTime = System.currentTimeMillis();
		EMTrainerIBM2 em = new EMTrainerIBM2(baseFileName, foriegnFileName, iterations1, iterations2);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		System.out.println("Total time taken: "+elapsedTime/1000.0+"s" + " for "+iterations2+" iterations");
	}
}
