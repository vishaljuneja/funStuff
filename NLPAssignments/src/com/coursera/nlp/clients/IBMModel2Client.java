package com.coursera.nlp.clients;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.coursera.nlp.translator.EMTrainerIBM1;
import com.coursera.nlp.translator.EMTrainerIBM2Maps;
import com.coursera.nlp.translator.Instance;
import com.coursera.nlp.translator.PreProcessor;

public class IBMModel2Client {

	private static final String dev_en = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\dev.en";
	private static final String dev_es = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\dev.es";
	
	private static final String out_file = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\dev.out";
	
	private static List<Instance> testSet;
	
	public static void main(String args[]) {
		
		// initialize EMTrainer
		String baseFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.en";
		String foriegnFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.es";
		
		int iterations = 5;
		
		long startTime = System.currentTimeMillis();
		EMTrainerIBM2Maps em = new EMTrainerIBM2Maps(baseFileName, foriegnFileName, iterations, iterations);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		System.out.println("Total time taken: "+elapsedTime/1000.0+"s" + " for "+iterations+" iterations");

		
		// generate alignments in test sets
		testSet = PreProcessor.read(dev_en, dev_es);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(out_file));
			int k = 1;
			for(Instance i : testSet) {
				int findex = 1;
				for(String f : i.foreign) {
					int l = i.base.size();
					int m = i.foreign.size();
					double tmax = em.qt(0, findex, l, m, f, EMTrainerIBM1._NULL);
					int imax = 0;
					int index = 1;
					for(String e : i.base) {
						double temp = em.qt(index, findex, l, m, f, e);
						if(temp > tmax) {
							tmax = temp;
							imax = index;
						}
						index++;
					}
					bw.write(k+ " " + imax + " " + findex);
					bw.newLine();
					findex++;
				}
				k++;
			}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
		
	}
	
}
