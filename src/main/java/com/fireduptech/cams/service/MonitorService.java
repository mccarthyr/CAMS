package com.fireduptech.cams.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.ObjectMessage;


import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


import java.util.concurrent.Future;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;



@Service(value="monitorService")
public class MonitorService {


	@Autowired
	private JmsTemplate jmsTemplate;


	@Transactional("jmsTxManager")
	public void createQueueEntryForWatchlistData( String totalDistance ) throws Exception {

		jmsTemplate.send( "watchlistDataDestination", new MessageCreator() {

			public Message createMessage( Session session ) throws JMSException {

				TextMessage textMessage = session.createTextMessage();
				textMessage.setText( totalDistance );
				return textMessage;

			}
		} );


	}	// End of method createQueueEntryForWatchlistData()...	


	@Async
	public Future<String> testFuture() throws InterruptedException {

		Thread.sleep(5000);
		return new AsyncResult<String>("---> Hello from the Future Method...");

	}




}