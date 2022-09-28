package com.tweet.exceptions;

public class NeededException extends RuntimeException {

	private String message;

	public NeededException(String message) {
		super(message);
		this.message = message;
	}
	
	public String toString() {
		return message;
	}
	
	
}
