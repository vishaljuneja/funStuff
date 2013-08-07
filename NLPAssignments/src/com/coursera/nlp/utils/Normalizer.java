package com.coursera.nlp.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.coursera.nlp.json.JSONArray;

public class Normalizer {

    //rare word classes used
    public static final boolean NUMERIC = true;
    public static final boolean ALL_CAPITAL = true;
    public static final boolean LAST_CAPITAL = true;
    public static final boolean RARE = true;
    
    //rare word replacement strings
    public static final String _NUMERIC_ = "_NUMERIC_";
    public static final String _ALL_CAPITAL_ = "_ALL_CAPITAL_";
    public static final String _LAST_CAPITAL_ = "_LAST_CAPITAL_";
    public static final String _RARE_ = "_RARE_";
    
    private static final String COUNT_FILE = "gene.counts";
              
    // rare words map
    private static HashMap<String, HashSet<String>> rare;
    
    static{
        rareWordsClassifier();
    }
    
    private Normalizer(){}
    
    // read the counts file and returns list of rare words
    public static void rareWordsClassifier(){
        BufferedReader br = null;
        rare = new HashMap<String, HashSet<String>>();
        HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
        try{
            br = new BufferedReader(new FileReader(COUNT_FILE));
            String str = br.readLine();
            while(str!=null){
                if(StringUtils.isBlank(str)){
                    str = br.readLine();
                    continue;
                }
                String splits[] = StringUtils.split(str, " ");
                if(splits[1].equals("WORDTAG")){
                    Integer count = Integer.parseInt(splits[0]);
                    String word = splits[3];
                    if(wordCount.containsKey(word)){
                        count = count + wordCount.get(word);
                    }
                    wordCount.put(word, count);
                }
                str = br.readLine();
            }            
        } catch (IOException e){
            e.printStackTrace();
        }
        
        // fill rare
        for(Map.Entry<String, Integer> e: wordCount.entrySet()){
            if(e.getValue()<5){
                String word = e.getKey();
                String c = getClassName(word);
                HashSet<String> s;
                if(rare.containsKey(c)){
                    s = rare.get(c);
                }else {
                    s = new HashSet<String>();
                }
                s.add(word);
                rare.put(c, s);
            }
        }
        
        for(Map.Entry<String, HashSet<String>> e: rare.entrySet()){
            System.out.println(e.getValue().size()+" rare words found in class "+e.getKey());        
        }
    }
    
    public static String getClassName(String word){
        
        if(NUMERIC && StringUtils.containsDigit(word)){
            return _NUMERIC_;
        } else if(ALL_CAPITAL && StringUtils.allCaps(word)){
            return _ALL_CAPITAL_;
        } else if(LAST_CAPITAL && StringUtils.lastCapital(word)){
            return _LAST_CAPITAL_;
        } else{
            return _RARE_;
        }           
    }
    
    public static String getReplacement(String s){
        for(Map.Entry<String, HashSet<String>> e: rare.entrySet()){
            if(e.getValue().contains(s)) return e.getKey();
        }
        return null;
    }
    
    public static boolean isRare(String s){
        if(getReplacement(s) == null) return false;
        return true;
    }
    
    public static void main(String args[]){      
        
        //NLPUtils.replaceWords(INPUT_FILE_NAME, NEW_FILE_NAME);
    	JSONArray output = new JSONArray("[s, [np, there], [s, [vp,[verb, is], [noun, asbestos] ],[., .]]]");
        System.out.println(output.getJSONArray(2));
    }
}