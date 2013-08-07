package com.coursera.posa.SampleReactor;

import java.io.IOException;

public class TaskFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public TaskFailedException(String string, IOException io) {
		super(string);
	}

}
