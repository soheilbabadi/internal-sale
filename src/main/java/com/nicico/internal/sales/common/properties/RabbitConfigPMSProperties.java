package com.nicico.internal.sales.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "rabbitmq.config.pms")
public class RabbitConfigPMSProperties {
	private String exchange;
	private Queues queues;

	@Data
	public static class Queues {
		private Queue preFactor;
		private Queue lc;
		private Queue havaleh;
		private Queue bank;
		private Queue customer;
	}

	@Data
	public static class Queue {
		private String routingKey;
		private String responseQueue;
		private String responseRoutingKey;
		private String updateQueue;
		private String updateRoutingKey;
		private String updateResponseQueue;
		private String updateResponseRoutingKey;
	}


}
