package com.coursera.nlp.clients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.coursera.nlp.parser.CKYParser;
import com.coursera.nlp.utils.StringUtils;

public class ParserClientMultiThreaded {
	
	private static Integer nthreads = 10;
	
	
	private static Map<String, List<String>> threadParse;
	
    public static void main(String args[]){
    	long startTime = System.currentTimeMillis();
    	
    	// initialize parser (training data)
		String input = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train.dat";
		String counts = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train.counts.out";
		CKYParser parser = new CKYParser(input, counts);
		
		// development set and output(parsed) file
		String inputFile = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_dev.dat";
		String outputFile = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_dev.p3.out";
		
		threadParse = Collections.synchronizedMap(new HashMap<String, List<String>>());

		System.out.println("Time to initialize: "+(System.currentTimeMillis()-startTime)/1000.0+"s");

		BufferedWriter bw = null;
		BufferedReader br = null;

		try{
			br = new BufferedReader(new FileReader(inputFile));
			String str = br.readLine();
			List<String> sentences = new ArrayList<String>();
			while(str!=null){
				sentences.add(str);
				str = br.readLine();
			}
			
			//spawn threads
			int count = 1;
			int  batchSize = (int) Math.ceil(sentences.size()*1.0/nthreads);
			int batchNumber = 0;
			ExecutorService executor = Executors.newFixedThreadPool(nthreads);
			List<String> batchSentences = new ArrayList<String>();
			for(String s : sentences) {
				batchSentences.add(s);
				if(count % batchSize == 0){
					batchNumber++;
					spawnJob(executor, batchNumber, batchSentences);
					batchSentences = new ArrayList<String>();
				}
				count++;
			}
			
			// process last batch
			if( (count-1) % batchSize != 0 )  {
				batchNumber++;
				spawnJob(executor, batchNumber, batchSentences);
			}
			
			executor.shutdown();
			while(!executor.isTerminated()){}
					
			System.out.println("All parsing done: "+(System.currentTimeMillis()-startTime)/1000.0+"s");
			
			// write to file
			bw = new BufferedWriter(new FileWriter(outputFile));
			for(int i=1; i<=batchNumber; i++) {
				for(String s: threadParse.get(new Integer(i).toString())){
					bw.write(s);
					bw.newLine();
				}
			}			
			bw.close();
			
		} catch (IOException e){
			e.printStackTrace();
		} 
		
		
		//NLPClientUtils.parseFile(parser, inputFile, outputFile);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		System.out.println("Total time taken: "+elapsedTime/1000.0+"s");
    }
    
    
    private static void spawnJob(ExecutorService executor, int batchNumber, List<String> batchSentences) {
		Thread pj = new ParserJob(batchSentences);
		pj.setName(new Integer(batchNumber).toString());
		executor.execute(pj);		
	}


	private static class ParserJob extends Thread {
		
		private List<String> sentences;
		private CKYParser parser;
		
		public ParserJob(List<String> batchSentences) {
			this.sentences = batchSentences;
			this.parser = new CKYParser();
		}    	
		
		public void run(){
			List<String> sentence = new ArrayList<String>();
			List<String> parses = new ArrayList<String>();
			for(String str: sentences){
				sentence = Arrays.asList(StringUtils.split(str, " "));				
				parses.add(parser.parse(sentence).toString());				
			}
			threadParse.put(this.getName(), parses);
		}
    	
    }
	
}
