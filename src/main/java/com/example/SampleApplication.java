package com.example;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableRabbit
public class SampleApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context=SpringApplication.run(SampleApplication.class, args);
		
		
        RabbitTemplate template = context.getBean(RabbitTemplate.class);
        template.convertAndSend("dataQ", "data q message1");
        template.convertAndSend("dataQ", "data q message2");
        template.convertAndSend("dataQ", "data q message3");
      
        template.convertAndSend("testQ", "testQ msg 1");
        template.convertAndSend("testQ", "testQ msg 2");
        template.convertAndSend("Metadata", "dataQ");
        template.convertAndSend("Metadata", "testQ");
        
	}
	
	
	
	    
}
