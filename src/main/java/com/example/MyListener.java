package com.example;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
@Configuration
public class MyListener {

	@RabbitListener(queues = "customer")
	public void messageProcessing(Customer c) {
		System.out.println(c);
	}

}
