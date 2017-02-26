package com.example;

import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ListenerContainerIdleEvent;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class MyListener {

	@Autowired
	ConnectionFactory connectionFactory;

	boolean containerIsIdle;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@RabbitListener(queues = "Metadata", containerFactory = "simpleListenerContainerFactory")
	public void messageProcessing(byte[] c) throws Exception {
		String s = new String(c);
		System.out.println(c);
		final CountDownLatch startedLatch = new CountDownLatch(1);
		final CountDownLatch finishedLatch = new CountDownLatch(1);

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(s);
		container.setMessageListener(new MessageListenerAdapter(new DataHandler(startedLatch)));
		container.setApplicationEventPublisher(new ApplicationEventPublisher() {

			@Override
			public void publishEvent(Object event) {
			}

			@Override
			public void publishEvent(ApplicationEvent event) {
				if (event instanceof ListenerContainerIdleEvent) {
					finishedLatch.countDown();
				}

			};
		});
		container.setIdleEventInterval(500);
		containerIsIdle = false;
		container.start();

		// how to get container idle event here

		// so we can call container.stop();

		startedLatch.await();
		finishedLatch.await();

		System.out.println("Finished processing " + s);
		container.stop();

	}

	public class DataHandler {

		CountDownLatch countDownLatch;

		public DataHandler(CountDownLatch countDownLatch) {
			this.countDownLatch = countDownLatch;
		}

		public void handleMessage(String text) {

			countDownLatch.countDown();

			if (text.equalsIgnoreCase("data q message3")) {
				String s = null;
				s.length();
			}

			String s = new String(text);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Data Received: " + s);
		}
	}

	@EventListener
	public void onApplicationEvent(ListenerContainerIdleEvent event) {
		// I am getting idle event here
		containerIsIdle = true;
		System.out.println(event.getSource());
	}

}
