package com.coursera.nlp.translator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreProcessor {
	
	/**
	 * 
	 * @param base native file name
	 * @param foreign foreign file name
	 * @return list of instances
	 */
	public static List<Instance> read(String base, 
			String foreign) {
		BufferedReader brBase;
		BufferedReader brForiegn;
		List<Instance> data = new ArrayList<Instance>();
		try {
			brBase = new BufferedReader(new FileReader(base));
			brForiegn = new BufferedReader(new FileReader(foreign));
			String s1 = brBase.readLine();;
			String s2 = brForiegn.readLine();
			while(s1!=null || s2!=null) {
				data.add(new Instance(s1, s2));
				s1 = brBase.readLine();
				s2 = brForiegn.readLine();
			}
		} catch(IOException e) {
			System.out.println("Error reading files " + base + " and " + foreign);
			e.printStackTrace();
		}	
		
		System.out.println(data.size() + " data items found ...");
		return data;
	}
	
	public static void main(String args[]) {
		String baseFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.en";
		String foriegnFileName = "C:\\Users\\vishal\\Videos\\nlp_columbia\\PA3\\corpus.es";
		
		PreProcessor.read(baseFileName, foriegnFileName);
	}
}
