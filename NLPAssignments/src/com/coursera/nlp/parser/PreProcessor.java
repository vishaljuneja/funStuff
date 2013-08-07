package com.coursera.nlp.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.coursera.nlp.json.JSONArray;

public class PreProcessor {
	
	private static HashMap<String, Integer> wordCounts = 
			new HashMap<String, Integer>();
	
	
	public static HashSet<String> getRareWords(String inputfile){
		HashSet<String> words = new HashSet<String>();
		fillWordCounts(inputfile);
		
		for(Map.Entry<String, Integer> e : wordCounts.entrySet()){
			if(e.getValue()<5) words.add(e.getKey());
		}
		return words;
	}

	private static void fillWordCounts(String inputfile) {
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(inputfile));
			String str = br.readLine();
			while(str!=null){
				JSONArray output = new JSONArray(str);
				countWords(output);
				str = br.readLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}		
	}

	public static Set<String> getAllWords(String inputfile){
		if(!wordCounts.isEmpty()) return wordCounts.keySet();
		
		fillWordCounts(inputfile);
		return wordCounts.keySet();
	}
	
	public static void replaceRareWords(String inputfile, String outputfile){
		HashSet<String> rareWords = getRareWords(inputfile);
		System.out.println(rareWords.size() + " rare words found!");
		BufferedReader br;
		BufferedWriter bw;
		try {
			br = new BufferedReader(new FileReader(inputfile));
			bw = new BufferedWriter(new FileWriter(outputfile));
			String str = br.readLine();
			while(str!=null){
				JSONArray output = new JSONArray(str);
				output = replace(output, rareWords);
				bw.write(output.toString());
				bw.newLine();
				str = br.readLine();
			}
			bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static JSONArray replace(JSONArray output, HashSet<String> rareWords) {		
		try{
			replace(output.getJSONArray(1), rareWords);
			replace(output.getJSONArray(2), rareWords);
		} catch (Exception e){
			String word = output.getString(1);
			if(rareWords.contains(word)){
				output.remove(1);
				output.put("_RARE_");
			}
		}
		return output;
	}

	private static void countWords(JSONArray output){
		try{
			countWords(output.getJSONArray(1));
			countWords(output.getJSONArray(2));
		} catch (Exception e){
			String word = output.getString(1);
			int count = 1;
			if(wordCounts.containsKey(word)){
				count = wordCounts.get(word) + 1;
			}
			wordCounts.put(word, count);
		}				
	}
	
    public static void main(String args[]){
    	String input = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train.dat";
    	String output = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA2\\assignment\\parse_train_rare.dat";
    	replaceRareWords(input, output);
    }
	
}
