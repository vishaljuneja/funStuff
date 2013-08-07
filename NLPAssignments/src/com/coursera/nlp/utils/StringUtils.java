package com.coursera.nlp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

public class StringUtils {
    
    public static String[] split(String input, String regex){
        if(input==null) throw new NullPointerException();
        return input.split(regex);
    }
    
    public static boolean isBlank(String input){
        if(input==null) throw new NullPointerException();
        if(input.trim().isEmpty()) return true;
        return false;
    }
    
    public static String replace(String input, String toReplace, 
                               String replacement){
        if(input==null) throw new NullPointerException();
        //input = input.replace(" "+toReplace+" ", " "+replacement+" ");
        //input = input.replaceAll(Pattern.quote("^"+toReplace+" "), replacement+" ");
        //input = input.replaceAll(Pattern.quote(" "+toReplace+"$"), " "+replacement);
        input = input.replaceAll("\\b" + Pattern.quote(toReplace) + "\\b", replacement);
        //System.out.println("from replace: " + input);
        return input;
    }
    
    public static String concatenate(String delim, String... args){
        if(args.length==0) throw new NullPointerException();
        if(args.length==1) return args[0];
        
        if(delim==null) delim="";
        
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<args.length-1; i++){
            sb.append(args[i]+delim);
        }
        sb.append(args[args.length-1]);
        return sb.toString();
    }
    
    public static String join(Object... args){
        if(args.length==0) throw new NullPointerException();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<args.length; i++){
            if(args[i] instanceof String) sb.append((String)args[i]);
            else sb.append(args[i].toString());
            
        }
        return sb.toString();
    }
    
    public static boolean containsDigit(String word){
        if(word==null) throw new NullPointerException();
        if (Pattern.compile("[0-9]").matcher(word).find()) {
            return true;
        }
        return false;
    }
    
    public static boolean allCaps(String word){
        if(word==null) throw new NullPointerException();
        if (Pattern.compile("[a-z]").matcher(word).find()) {
            return false;
        }
        return true;
    }
    
    public static boolean lastCapital(String word){
        if(word==null) throw new NullPointerException();
        if(Character.isUpperCase(word.charAt(word.length()-1))) return true;
        return false;
    }
    
    
    public static List reverse(List l){
        if(l==null) throw new NullPointerException();
        ListIterator it = l.listIterator(l.size());
        List n = new ArrayList();
        while(it.hasPrevious()){
            n.add(it.previous());
        }
        return n;
    }
    
    public static void main(String args[]){
        System.out.println(StringUtils.lastCapital("AAA aaA"));
        
    }
    
}