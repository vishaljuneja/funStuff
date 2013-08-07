package com.coursera.nlp.tagger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.coursera.nlp.clients.NLPClientUtils;
import com.coursera.nlp.utils.StringUtils;


public class Viterbi implements Tagger {
    
    private HashMap<String, Integer> ngramCounts = 
        new HashMap<String, Integer>();
    
    private static final String DELIMITER = "@@";
    private UnigramTagger ut;
    
    private static HashMap<String, PIE> invariants 
        = new HashMap<String, PIE>();    //PIE values
    private String[] tags;
    
    public Viterbi(String counts_file_name, UnigramTagger ut){
        this.ut= ut;
        this.tags = ut.allTags();
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(counts_file_name));
            String str = br.readLine();
            while(str!=null){
                String splits[] = StringUtils.split(str, " ");
                
                if(splits[1].equals("3-GRAM")){
                    String ngram = 
                        StringUtils.concatenate(this.DELIMITER,
                                     splits[2], splits[3], splits[4]);
                    ngramCounts.put(ngram, Integer.parseInt(splits[0]));
                } else if (splits[1].equals("2-GRAM")){
                    String ngram = 
                        StringUtils.concatenate(this.DELIMITER,
                                     splits[2], splits[3]);
                    ngramCounts.put(ngram, Integer.parseInt(splits[0]));
                }
                
                str = br.readLine(); 
            }                       
        } catch(IOException e){
            e.printStackTrace();
        }        
    }
    
    
    
    //return log value of maximum likelihood estimate
    public double get3gramEstimate(String s1, String s2, String s3){
        String thg = StringUtils.concatenate(this.DELIMITER,s1,s2,s3);
        String twg = StringUtils.concatenate(this.DELIMITER,s1,s2);
        
        if(!ngramCounts.containsKey(twg)) return Double.MIN_VALUE;
                
        double thgc = 0;
        if(ngramCounts.containsKey(thg)){
            thgc = ngramCounts.get(thg);
        }
                 
        double twgc = ngramCounts.get(twg);
        //System.out.println("get3gramEstimate: "+twgc+" "+thgc);
        return (Math.log(thgc)-Math.log(twgc));
    }
    
    public List<String> tag(List<String> sentence) { 
        if(sentence==null) throw new NullPointerException();
        
        ArrayList<String> result = new ArrayList<String>();
        int l = sentence.size();
        if(l == 0) return result;        
                
        // re-initialize static invariants
        this.invariants = new HashMap<String, PIE>();
        
        double max = -Double.MAX_VALUE;
        String tag1 = null;
        String tag2 = null;
        for(String s1:tags){
            if(l == 1) s1 = "*";
            for(String s2:tags){
               double val = pie(l, s1, s2, sentence)
                    + get3gramEstimate(s1, s2, "STOP") ;
               if(val>=max){
                   max = val;
                   tag1 = s1;
                   tag2 = s2;
               }
               //System.out.println("tagSentence:"+s1+" "+s2+" "+val+" "+max);
            }
        }
                        
        return traceResult(l, tag1 , tag2);
    }
    
    public String tag(String word) {
        if(word==null) throw new NullPointerException();
        List<String> sent = new ArrayList<String>();
        sent.add(word);
        return tag(sent).get(0);
    }
    
    private List<String> traceResult(int k, String u, String v){
        //System.out.println("traceResult:"+k+" "+u+" "+v);
        PIE bp = invariants.get(StringUtils.join(k,u,v));
        List<String> result = new ArrayList<String>();
        while(bp!=null){
            result.add(bp.ngrams[1]);
            bp = bp.backpointer;
        }
        result = StringUtils.reverse(result);
        return result;
    }
                                    
    private double pie(int k, String u, String v, List<String> sentence){
       if(k==0) return Math.log(1);
       
       if(invariants.containsKey(StringUtils.join(k,u,v))) 
           return invariants.get(StringUtils.join(k,u,v)).value;
       
       
       if(k==1){
           double val= pie(0, "*", "*", sentence) + 
               get3gramEstimate("*", "*", v) + 
               ut.logEmmission(sentence.get(0), v);
           //System.out.println(get3gramEstimate("*", "*", v)+" "+ut.logEmmission(sentence.get(0), v));
           // update table
           PIE p = new PIE(1, "*", v);
           invariants.put(StringUtils.join("1","*",v), p);
           p.value = val;
           p.backpointer = null;
           return val;
       }
       
       if(k==2){          
            double val = pie(1, "*", u, sentence) + 
                get3gramEstimate("*", u, v) + 
                ut.logEmmission(sentence.get(1), v);
           
            //update table
            PIE p = new PIE(2, u, v);
            invariants.put(StringUtils.join("2" ,u ,v), p);
            p.value = val;
            p.backpointer = invariants.get(StringUtils.join("1","*",u));
            return val;
       }
       
       double max = -Double.MAX_VALUE;
       String s = null;
       for(String s1:tags){
             double val = pie(k-1, s1, u, sentence) +
                 get3gramEstimate(s1, u, v) + 
                 ut.logEmmission(sentence.get(k-1), v);
             if(val>=max){
                 max = val;
                 s = s1;
             }
       }
       //update table
       if(s==null) s=tags[0];
       PIE p = new PIE(k, u, v);
       //System.out.println("PIE:"+" "+s+" "+k+" "+u+" "+v);
       //System.out.println("INVARIANTS:"+ invariants);
       invariants.put(StringUtils.join(k,u,v), p);
       p.value = max;
       p.backpointer = invariants.get(StringUtils.join(k-1,s,u));
       return max;
        
    }
    
    private class PIE {
        
        public int k;
        public double value;
        public String[] ngrams;        
        public PIE backpointer;
        
        public PIE(int k, String u, String v){
            this.k = k;
            this.ngrams = new String[2];
            this.ngrams[0] = u;
            this.ngrams[1] = v;
        }
        
        public String toString(){
            return k+" "+ngrams[0]+" "+ngrams[1]+" "+value;
        }
    }
    
    
    public static void main(String args[]) {
        String counts_file_name = "normalized_gene.counts";
        UnigramTagger ut = new UnigramTagger(counts_file_name);
        Viterbi v = new Viterbi(counts_file_name, ut);
        NLPClientUtils.tagFile(v, "gene.dev", "gene_dev.p3.out");        
    }
    
}