package com.coursera.nlp.clients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coursera.nlp.parser.Parser;
import com.coursera.nlp.tagger.Tagger;
import com.coursera.nlp.translator.EMTrainerIBM1;
import com.coursera.nlp.translator.EMTrainerIBM2Maps;
import com.coursera.nlp.translator.Instance;
import com.coursera.nlp.translator.PreProcessor;
import com.coursera.nlp.utils.Normalizer;
import com.coursera.nlp.utils.StringUtils;

public class NLPClientUtils {
    

	// scans an input_file to replace all occurances of words in HashSet 
	// with a replacement string
	public static void replaceWords(String input_file,
			String output_file){

		BufferedWriter bw = null;
		BufferedReader br = null;

		try{
			br = new BufferedReader(new FileReader(input_file));
			bw = new BufferedWriter(new FileWriter(output_file));
			String str = br.readLine();
			while(str!=null){
				for(String s: StringUtils.split(str, " ")){
					String replacement = Normalizer.getReplacement(s);                   
					if(replacement!=null){                        
						str = StringUtils.replace(str, s, replacement);
						//System.out.println(s+" "+replacement+" "+str);
					}
				}
				bw.write(str);
				bw.newLine();
				str = br.readLine();
			}
			bw.close();
		} catch (IOException e){
			e.printStackTrace();
		}

	}

	public static void tagFile(Tagger tagger,
			String input_file,
			String output_file) {

		BufferedWriter bw = null;
		BufferedReader br = null;

		try{
			br = new BufferedReader(new FileReader(input_file));
			bw = new BufferedWriter(new FileWriter(output_file));
			String str = br.readLine();
			List<String> sentence = new ArrayList<String>();
			List<String> tags = new ArrayList<String>();
			while(str!=null){
				if(StringUtils.isBlank(str)){
					tags = tagger.tag(sentence);
					for(int i=0; i<sentence.size(); i++){
						bw.write(sentence.get(i)+" "+tags.get(i));
						bw.newLine();
					}
					bw.write("");
					bw.newLine();
					sentence = new ArrayList<String>();
					str = br.readLine();
					tags = new ArrayList<String>();
					continue;
				}
				sentence.add(str);
				str = br.readLine();
			}
			bw.close();
		} catch (IOException e){
			e.printStackTrace();
		} 

	}
	
	public static void parseFile(Parser parser,
			String inputfile,
			String outputfile) {

		BufferedWriter bw = null;
		BufferedReader br = null;

		try{
			br = new BufferedReader(new FileReader(inputfile));
			bw = new BufferedWriter(new FileWriter(outputfile));
			int count = 1;
			String str = br.readLine();
			List<String> sentence = new ArrayList<String>();
			while(str!=null){
				System.out.println("Processing Sentence "+count);
				sentence = Arrays.asList(StringUtils.split(str, " "));
				bw.write(parser.parse(sentence).toString());
				bw.newLine();
				str = br.readLine();
				count++;
			}
			bw.close();
		} catch (IOException e){
			e.printStackTrace();
		} 
	}
	
	public static Set<String> getAlignmentSet(String test_base, String test_foreign, 
			EMTrainerIBM2Maps em, boolean order){
		// generate alignments in test sets
		List<Instance> testData = PreProcessor.read(test_base, test_foreign);
		HashSet<String> result = new HashSet<String>();
		int k = 1;
		for(Instance i : testData) {
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
				if(order) {
					result.add(k+ " " + imax + " " + findex);
				} else {
					result.add(k+ " " + findex + " " + imax);
				}
				findex++;
			}
			k++;
		}
		return result;		
	}
	
	
	public static void serialize(Object o, String file){
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(file));
			out.writeObject(o);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
     
}