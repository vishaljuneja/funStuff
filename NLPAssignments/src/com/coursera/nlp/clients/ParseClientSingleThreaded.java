package com.coursera.nlp.clients;

import com.coursera.nlp.parser.CKYParser;

public class ParseClientSingleThreaded {

	
	public static void main(String args[]){
		
    	long startTime = System.currentTimeMillis();
    	
    	// initialize parser (training data)
		String input = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train.dat";
		String counts = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train.counts.out";
		CKYParser parser = new CKYParser(input, counts);
		
		// development set and output(parsed) file
		String inputFile = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_dev.dat";
		String outputFile = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_dev.p3.out";
		
		
		NLPClientUtils.parseFile(parser, inputFile, outputFile);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		System.out.println("Total time taken: "+elapsedTime/1000.0+"s");
		
	}
	
	
}
