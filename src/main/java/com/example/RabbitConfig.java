package com.example;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;

@Configuration
public class RabbitConfig {
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		connectionFactory.setUsername("test");
		connectionFactory.setPassword("test1234");
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}

	@Bean
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		// rabbitTemplate.setMessageConverter(new
		// Jackson2JsonMessageConverter());
		return rabbitTemplate;
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
		return rabbitAdmin;
	}

	@Bean
	public Jackson2JsonMessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean(name = "simpleListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setMessageConverter(new Jackson2JsonMessageConverter());
		factory.setConcurrentConsumers(2);
		factory.setMaxConcurrentConsumers(2);
		// factory.setDefaultRequeueRejected(false);
		factory.setMissingQueuesFatal(false);

		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(2000);

		factory.setAdviceChain(new Advice[] { org.springframework.amqp.rabbit.config.RetryInterceptorBuilder.stateless()
				.maxAttempts(5).backOffPolicy(backOffPolicy)
				// .backOffOptions(1000, 2, 5000)
				.build() });
		return factory;
	}
}
