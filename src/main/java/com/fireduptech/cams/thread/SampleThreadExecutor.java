package com.fireduptech.cams.thread;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Component(value="sampleThreadExecutor")
public class SampleThreadExecutor {

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	//private TaskExecutor myTaskExecutor;
	

	public void executeTask( Runnable task ) {
		taskExecutor.execute( task );
	}

	public int getNumberOfActiveThreads() {
		return taskExecutor.getActiveCount();
	}

	public void shutDownTaskExecutor() {
		taskExecutor.shutdown();
	}

}