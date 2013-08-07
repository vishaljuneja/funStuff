package com.coursera.nlp.translator;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class IBM2Array {

	private static final int _capacity = 150;

	private EMTrainerIBM1 em1;
	private List<Instance> data;

	private Double q[][][][] = new Double[_capacity][_capacity][_capacity][_capacity]; // m,l,i,j


	/**
	 * @param base
	 * @param foreign
	 * @param iterations1 iterations for model 1
	 * @param iterations2 iterations for model 2
	 */
	public IBM2Array(String base, String foreign, int iterations1, int iterations2) {
		em1 = new EMTrainerIBM1(base, foreign, iterations1);
		data = em1.getTrainingData();
		initialize();
		for(int i=0; i<iterations2; i++) {
			iterate();
			System.out.println("Model 2 iteration "+(i+1)+" complete ...");
		}
	}

	private void iterate() {
		// (j,i,l,m) & (i,l,m) counts
		Double jilm[][][][] = new Double[_capacity][_capacity][_capacity][_capacity];
		Double ilm[][][] = new Double[_capacity][_capacity][_capacity];

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

	private void updateQT(Double[][][][] jilm, Double[][][] ilm,
			HashMap<String, TreeMap<String, Double>> ef,
			HashMap<String, Double> e) {

		em1.updateT(ef, e);

		// update q map
		for(int m=0; m<_capacity; m++){
			for(int l=0; l<_capacity; l++){
				for(int i=0; i<_capacity; i++){
					double d = ilm[m][l][i];
					if(d==0) continue;
					for(int j=0; j<_capacity; j++){
						double n = jilm[m][l][i][j];
						q[m][l][i][j] = n/d;
					}
				}
			}
		}
		
	}

	private void updateCounts(Double[][][][] jilm, Double[][][] ilm, int j,
			int i, int l, int m,
			HashMap<String, TreeMap<String, Double>> ef,
			HashMap<String, Double> e, String fword, String eword, double delta) {
		
		em1.updateCounts(ef, e, fword, eword, delta);

		// update j,i,l,m counts
		jilm[m][l][i][j] += delta;

		// update i,l,m counts
		ilm[m][l][i] += delta;
		
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
		double qParameter = q[m][l][i][j];
		return qParameter * em1.t(f, e);
	}

	private void initialize() {
		for(Instance instance : data) {
			int lforeign = instance.foreign.size(); // length of foreign
			int lbase = instance.base.size() + 1;  // length of base/english + 1 (for NULL)
			double val = 1.0/(lbase+1);
			for(int i=1 ; i<=lforeign; i++){
				for(int j=0; j<=lbase; j++){
					//q.put(hash(j,i,lbase,lforeign), 1.0/(lbase+1));
					q[lforeign][lbase][i][j] = val;
				}
			}
		}
		System.out.println("IBM2Array initalized ...");
	}


	public static void main(String args[]) {
		String baseFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.en";
		String foriegnFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.es";

		int iterations1 = 1;
		int iterations2 = 1;

		long startTime = System.currentTimeMillis();
		IBM2Array em = new IBM2Array(baseFileName, foriegnFileName, iterations1, iterations2);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;

		System.out.println("Total time taken: "+elapsedTime/1000.0+"s" + " for "+iterations2+" iterations");
	}


	
}
