package com.tweet.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class PublicUserDetails {

	private String firstName;

	private String lastName;

	private String email;

	private Date joinedDate;

	private String userName;

	private List<Tweet> tweets = new ArrayList<Tweet>();
	
	public PublicUserDetails() {}

	public PublicUserDetails(String firstName, String lastName, String email,  Date joinedDate,
			String userName, List<Tweet> tweets) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		
		this.joinedDate = joinedDate;
		this.userName = userName;
		this.tweets = tweets;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getJoinedDate() {
		return joinedDate;
	}

	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
	
	
	
	
}
