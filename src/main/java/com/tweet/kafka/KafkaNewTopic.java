package com.tweet.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaNewTopic {

	@Bean
	public NewTopic newKafkaTopic() {
		return TopicBuilder.name("Tweet").build();
	}
}
