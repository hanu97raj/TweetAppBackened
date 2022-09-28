package com.tweet.kafka;

import java.util.Date;

public class KafkaTerminalMessage {
	
	private String tweetMessage;
	private Date tweetTime;
	public KafkaTerminalMessage() {}
	public String getTweetMessage() {
		return tweetMessage;
	}
	public void setTweetMessage(String tweetMessage) {
		this.tweetMessage = tweetMessage;
	}
	public Date getTweetTime() {
		return tweetTime;
	}
	public void setTweetTime(Date tweetTime) {
		this.tweetTime = tweetTime;
	}
	public KafkaTerminalMessage(String tweetMessage, Date tweetTime) {
		this.tweetMessage = tweetMessage;
		this.tweetTime = tweetTime;
	}
	
	

}
