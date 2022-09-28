package com.tweet.jwt.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tweet.entities.TweetAppUser;
import com.tweet.repositories.IUserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {
	
	@Autowired
	 private IUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		TweetAppUser user=userRepository.findByLoginId(loginId);
		if(user==null) {
			throw new UsernameNotFoundException("Invalid login Id");
		}
		
		return new User(loginId,user.getPassword(),new ArrayList<>());
	}
}
