package com.coursera.posa.SampleReactor;

import java.util.Stack;

public class TaskQueue {

	private Stack<Task> _taskqueue;
	
	public TaskQueue() {
		_taskqueue = new Stack<Task>();
	}
	
	public Task getTask() {
		return _taskqueue.pop();
	}

	public void addTask(Task task) {
		_taskqueue.add(task);
	}

	public boolean isEmpty() {
		return _taskqueue.isEmpty();
	}

}
