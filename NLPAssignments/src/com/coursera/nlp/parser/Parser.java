package com.coursera.nlp.parser;

import java.util.List;

import com.coursera.nlp.json.JSONArray;

public interface Parser {

	public JSONArray parse(List<String> sentence);
	
}
