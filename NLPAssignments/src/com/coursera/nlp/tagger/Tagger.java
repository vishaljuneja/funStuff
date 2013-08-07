package com.coursera.nlp.tagger;

import java.util.List;

public interface Tagger {
    
    public String tag(String word);
    
    public List<String> tag(List<String> sentence);
    
}