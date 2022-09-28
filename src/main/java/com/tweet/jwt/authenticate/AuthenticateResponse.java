package com.tweet.jwt.authenticate;

import com.tweet.entities.TweetAppUser;

public class AuthenticateResponse {
	
	private String jwt;
	private TweetAppUser appUser;

	public AuthenticateResponse(String jwt,TweetAppUser appUser) {
		
		this.jwt = jwt;
		this.appUser=appUser;
	}

	public TweetAppUser getAppUser() {
		return appUser;
	}

	

	public String getJwt() {
		return jwt;
	}
	
	

}
