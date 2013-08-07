package com.coursera.nlp.translator;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.coursera.nlp.utils.StringUtils;

public class Instance implements Serializable {
	
	private static final long serialVersionUID = -1578688286655509826L;
	public List<String> base;		// sentence to be translated into
	public List<String> foreign;	// sentence to be translated
	
	public Instance(String b, String f) {
		base = Arrays.asList(StringUtils.split(b, " "));
		foreign = Arrays.asList(StringUtils.split(f, " "));
	}
}
