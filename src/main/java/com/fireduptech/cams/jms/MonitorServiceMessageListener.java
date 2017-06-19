package com.fireduptech.cams.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


// *** NOTE ***
// -> FOR NOW ALL THIS CLASS HAS TO DO IS GET THE MESSAGE AND THEN RETURN IT AND PRINT TO THE COMMAND LINE

public class MonitorServiceMessageListener implements MessageListener {


	private String queueMessage = null;


	@Override
	public void onMessage( Message message ) {

		TextMessage textMessage = (TextMessage)message;

		try {

			this.queueMessage = textMessage.getText();

			System.out.format( "%n%n" );
			System.out.println( "CURRENTLY IN THE ONMESSAGE LISTENER METHOD: The Queue Message is: " );
			System.out.println( this.queueMessage );
			System.out.format( "%n%n" );

		} catch ( JMSException je ) {
			je.printStackTrace();
		}

	}



}