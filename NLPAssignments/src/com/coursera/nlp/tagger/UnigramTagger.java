package com.coursera.nlp.tagger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coursera.nlp.clients.NLPClientUtils;
import com.coursera.nlp.utils.Normalizer;
import com.coursera.nlp.utils.StringUtils;

public class UnigramTagger implements Tagger {
    
    private HashMap<String, HashMap<String, Integer>> word_tag_map
        = new HashMap<String, HashMap<String, Integer>>();
    
    private HashMap<String, Integer> tag_count
        = new HashMap<String, Integer>();
    
    private HashMap<String, String> word_tag_cache
        = new HashMap<String, String>();
    
    public UnigramTagger(String counts_file_name){
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(counts_file_name));
            String str = br.readLine();
            while(str!=null){
                String splits[] = StringUtils.split(str, " ");
                if(splits[1].equals("WORDTAG")){
                    Integer count = Integer.parseInt(splits[0]);
                    String word = splits[3];
                    String tag = splits[2];
                    update(word, tag, count);
                }
                str = br.readLine();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public String tag(String word){
        if(word==null) throw new NullPointerException();
        if(isUnseen(word))
            word = Normalizer.getClassName(word);        
        else if (Normalizer.isRare(word))
               word = Normalizer.getReplacement(word);       
        
        if(word_tag_cache.containsKey(word)) 
            return word_tag_cache.get(word);
        
        String result = getUnigramTag(word);
        word_tag_cache.put(word, result);
        return result;
    }
    
    public List<String> tag(List<String> sentence){
        if(sentence==null) throw new NullPointerException();
        
        List<String> tags = new ArrayList<String>();        
        for(String s: sentence){
            tags.add(tag(s));
        }
        return tags;
    }
    
    public double logEmmission(String word, String tag){
        if(isUnseen(word))
            word = Normalizer.getClassName(word);        
        else if (Normalizer.isRare(word))
               word = Normalizer.getReplacement(word); 
        
        double emmission;
        if(word_tag_map.get(word).containsKey(tag)){
            emmission = 
                (word_tag_map.get(word).get(tag)*1.0)/tag_count.get(tag);
        } else {
            emmission = 0;
        }
        return Math.log(emmission);
    }
    
    public String[] allTags(){
        return this.tag_count.keySet().toArray(new String[0]);
    }
    
    private void update(String word, String tag, Integer count){
        Integer tagCount = count;
        if(tag_count.containsKey(tag)){
            tagCount = tagCount + tag_count.get(tag);            
        }
        tag_count.put(tag, tagCount);
        
        HashMap<String, Integer> tag_map;
        if(word_tag_map.containsKey(word)){
            tag_map = word_tag_map.get(word);
        } else {
            tag_map = new HashMap<String, Integer>();
        }
        tag_map.put(tag, count);
        word_tag_map.put(word, tag_map);
    }
    
    private String getUnigramTag(String word){
        double maximum = -1;
        String result = null;
        for(Map.Entry<String, Integer> e : word_tag_map.get(word).entrySet()){
            double emmission = (e.getValue()*1.0)/tag_count.get(e.getKey());
            if(emmission > maximum){
                maximum = emmission;
                result = e.getKey();
            }
        }
        return result;
    }
    
    private boolean isUnseen(String word){
        if(word_tag_map.containsKey(word)) return false;
        return true;
    }
    
    public static void main(String args[]){
        String counts_file_name = "normalized_gene.counts";
        UnigramTagger tagger = new UnigramTagger(counts_file_name);

        NLPClientUtils.tagFile(tagger, "gene.dev", "gene_dev.p3.out");
    }

}