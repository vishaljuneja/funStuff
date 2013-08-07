package com.coursera.nlp.clients;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.coursera.nlp.translator.EMTrainerIBM2Maps;

public class GrowingAlignments {

	private static final String dev_en = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\test.en";
	private static final String dev_es = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\test.es";
	
	private static final String out_file = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\alignment_test.p3.out";
	
	public static void main(String args[]) {
		
		// initialize EMTrainer
		String baseFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.en";
		String foriegnFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.es";
		
		int iterations = 5;
		
		long startTime = System.currentTimeMillis();
		EMTrainerIBM2Maps em1 = new EMTrainerIBM2Maps(baseFileName, foriegnFileName, iterations, iterations);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		
		System.out.println("First Model: Total time taken: "+elapsedTime/1000.0+"s" + " for "+iterations+" iterations");

		// write object to file
		NLPClientUtils.serialize(em1, "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\em1.ser");
		
		
		
		startTime = System.currentTimeMillis();
		EMTrainerIBM2Maps em2 = new EMTrainerIBM2Maps(foriegnFileName, baseFileName, iterations, iterations);
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		
		System.out.println("Second Model: Total time taken: "+elapsedTime/1000.0+"s" + " for "+iterations+" iterations");
		
		// write object to file
		NLPClientUtils.serialize(em2, "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\em2.ser");
		
		// es to en alignments
		Set<String> es_en = NLPClientUtils.getAlignmentSet(dev_en, dev_es, em1, true);
		// en to es alignments
		Set<String> en_es = NLPClientUtils.getAlignmentSet(dev_es, dev_en, em2, false);
		
		Set<String> intersection = new HashSet<String>(es_en);
		intersection.retainAll(en_es);
		
		write(intersection, out_file);
	}
	
	private static void write(Set<String> intersection, String out_file) {
		BufferedWriter bw;
		
		try {
			bw = new BufferedWriter(new FileWriter(out_file));
			for(String s : intersection) {
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
