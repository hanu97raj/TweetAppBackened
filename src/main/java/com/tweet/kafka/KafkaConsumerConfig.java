package com.tweet.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.tweet.entities.Tweet;

@Configuration
public class KafkaConsumerConfig {
	@Value("localhost:9092")
	private String bootstrapServer;
	
	public Map<String, Object> consumerConfig(){
		Map<String, Object> props=new HashMap<String,Object>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServer);
		return props;
	}
	
	@Bean
	public ConsumerFactory<String, KafkaTerminalMessage> consumerFactory(){
		return new DefaultKafkaConsumerFactory<>(consumerConfig(),new StringDeserializer(),new JsonDeserializer<>());
	}
	
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, KafkaTerminalMessage>> factory(
			
			ConsumerFactory<String, KafkaTerminalMessage> consumerFactory){
		
		ConcurrentKafkaListenerContainerFactory<String, KafkaTerminalMessage> factory=new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		return factory;
		
	}
}


