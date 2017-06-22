package com.fireduptech.cams.thread;

public class PrintTask implements Runnable {

	private String name;

	public PrintTask( String name ) {
		this.name = name;
	}


	@Override
	public void run() {

		System.out.println( name + " is running" );
		try {
			Thread.sleep(3000);
		} catch ( InterruptedException ie  ) {
			ie.printStackTrace();
		}
		System.out.println( name + " running" );

	}

}