package com.tweet.entities;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("TweetsDb")
public class Tweet {

	@Id
	private String id;

	private String fullName;

	private String userName;

	private Date tweetTime;
	
	private String tweetMessage;

	private int likes;
	
	private Boolean userLikedHisOwnTweet;

	private List<TweetAppUser> usersWhoLiked;

	//private List<Tweet> repliesList = new ArrayList<Tweet>();
	private List<String> repliesList = new ArrayList<String>();
	
	private String hashTags;
	
	

	

	public String getHashTags() {
		return hashTags;
	}

	public void setHashTags(String hashTags) {
		this.hashTags = hashTags;
	}

	public Date getTweetTime() {
		return tweetTime;
	}

	public void setTweetTime(Date tweetTime) {
		this.tweetTime = tweetTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

	public Boolean getUserLikedHisOwnTweet() {
		return userLikedHisOwnTweet;
	}

	public void setUserLikedHisOwnTweet(Boolean userLikedHisOwnTweet) {
		this.userLikedHisOwnTweet = userLikedHisOwnTweet;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	

	public String getTweetMessage() {
		return tweetMessage;
	}

	public void setTweetMessage(String tweetMessage) {
		this.tweetMessage = tweetMessage;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public List<TweetAppUser> getUsersWhoLiked() {
		return usersWhoLiked;
	}

	public void setUsersWhoLiked(List<TweetAppUser> usersWhoLiked) {
		this.usersWhoLiked = usersWhoLiked;
	}

	public List<String> getRepliesList() {
		return repliesList;
	}

	public void setRepliesList(List<String> repliesList) {
		this.repliesList = repliesList;
	}

}
