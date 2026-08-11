package com.nicico.internal.sales.config.rabbit;

import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitPMSBinding {
	private final RabbitConfigPMSProperties properties;

	@Bean
	public DirectExchange pmsExchange() {
		log.info("Creating pms exchange: {}", properties.getExchange());
		return new DirectExchange(properties.getExchange());
	}

	@Bean
	public Queue responsePreFactorQueue() {
		return QueueBuilder.durable(properties.getQueues().getPreFactor().getResponseQueue()).build();
	}


	@Bean
	public Queue responseLcQueue() {
		return QueueBuilder.durable(properties.getQueues().getLc().getResponseQueue()).build();
	}

	@Bean
	public Queue responseUpdateLcQueue() {
		return QueueBuilder.durable(properties.getQueues().getLc().getUpdateResponseQueue()).build();
	}


	@Bean
	public Queue responseHavalehQueue() {
		return QueueBuilder.durable(properties.getQueues().getHavaleh().getResponseQueue()).build();
	}

	@Bean
	public Queue responseUpdateHavalehQueue() {
		return QueueBuilder.durable(properties.getQueues().getHavaleh().getUpdateResponseQueue()).build();
	}


	@Bean
	public Binding responsePreFactorBinding() {
		return BindingBuilder.bind(responsePreFactorQueue()).to(pmsExchange()).with(properties.getQueues().getPreFactor().getResponseRoutingKey());
	}


	@Bean
	public Binding responseLcBinding() {
		return BindingBuilder.bind(responseLcQueue()).to(pmsExchange()).with(properties.getQueues().getLc().getResponseRoutingKey());
	}

	@Bean
	public Binding responseUpdateLcBinding() {
		return BindingBuilder.bind(responseUpdateLcQueue()).to(pmsExchange()).with(properties.getQueues().getLc().getUpdateResponseRoutingKey());
	}


	@Bean
	public Binding responseHavalehBinding() {
		return BindingBuilder.bind(responseHavalehQueue()).to(pmsExchange()).with(properties.getQueues().getHavaleh().getResponseRoutingKey());
	}

	@Bean
	public Binding responseUpdateHavalehBinding() {
		return BindingBuilder.bind(responseUpdateHavalehQueue())
				.to(pmsExchange()).with(properties.getQueues().getHavaleh().getUpdateResponseRoutingKey());
	}


	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}


	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}


	@Bean
	public Queue responseCustomerQueue() {
		return QueueBuilder.durable(properties.getQueues().getCustomer().getResponseQueue()).build();
	}


	@Bean
	public Binding responseCustomerBinding() {
		return BindingBuilder.bind(responseCustomerQueue())
				.to(pmsExchange()).with(properties.getQueues().getCustomer().getResponseRoutingKey());
	}


}
