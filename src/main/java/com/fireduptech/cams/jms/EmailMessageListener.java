package com.fireduptech.cams.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;


// NOTE  ***-> CURRENTLY THIS CLASS IS NOT IN USE <-***
public class EmailMessageListener {
//implements MessageListener {

	@Autowired
	private transient MailSender mailSender;

	@Autowired
	@Qualifier("athleteDataThresholdReachedTemplate")
	private transient SimpleMailMessage simpleMailMessage;


	public void sendEmail() {
		mailSender.send( simpleMailMessage );
	}

	//@Override
	public void onMessage( Message message ) {

		TextMessage textMessage = (TextMessage)message;

		try {

			simpleMailMessage.setTo(textMessage.getText()); // 	WHERE THE QUEUE MESSAGE IS THE EMAIL ADDRESS

		} catch( JMSException jmse ) {
			jmse.printStackTrace();
		}

		//sendEmail();

	}


}

