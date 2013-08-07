package com.coursera.nlp.clients;

import com.coursera.nlp.parser.PreProcessor;

public class ParserPreProcessorClient {
	
    public static void main(String args[]){
    	String input = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train_vert.dat";
    	String output = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train_vert_rare.dat";
    	PreProcessor.replaceRareWords(input, output);
    }
}
