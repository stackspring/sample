package com.example;

import java.util.Properties;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MyService {

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	RabbitTemplate rabbitTemplate;

	// Notice @async in below method

	@Async
	public void submitData(String data) {
		// Imagine that this dataQname is based on my payload, for this example
		// our payload is simple string, so just using it.
		String dataQName = data + "queueName";
		Properties properties = amqpAdmin.getQueueProperties(dataQName);
		boolean sendMessageToHandleQueue = false;

		// This code need to determine if we need to put data queue name in
		// handle queue, as listener will listen to that queue and if finds
		// message and it will pull message from that queue. If we already have
		// messages in that queue, meaning handle message is there and queue
		// probably being processed. (There are reasons do this as messages
		// needs to be processed in order for particular data queue and there
		// can be multiple instances of listener app, we want only one of that
		// to keep processing from data queue as sequence of data processing
		// matters. so whichever instance gets handle message it will pull data
		// queue name and it will keep processing data until queue is empty and
		// will delete queue)
		if (properties == null || properties.get("QUEUE_MESSAGE_COUNT") == null) {
			// Queue doesnt exist
			sendMessageToHandleQueue = true;
		} else if (Integer.parseInt(properties.get("QUEUE_MESSAGE_COUNT").toString()) == 0) {
			sendMessageToHandleQueue = true;
		}

		// Send data
		amqpAdmin.declareQueue(new Queue(dataQName));
		rabbitTemplate.convertAndSend(dataQName, data);

		if (sendMessageToHandleQueue) {
			amqpAdmin.declareQueue(new Queue("handleQueue"));
			rabbitTemplate.convertAndSend("handleQueue", dataQName);
		}

	}

}
