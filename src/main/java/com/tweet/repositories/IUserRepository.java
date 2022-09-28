package com.tweet.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweet.entities.TweetAppUser;

@Repository
public interface IUserRepository extends MongoRepository<TweetAppUser, String> {
	
	TweetAppUser findByLoginId(String loginId);
	TweetAppUser findByEmail(String email);
	List<TweetAppUser> findByFirstNameAndLastName(String firstName, String lastName);
	TweetAppUser findByUserName(String userName);
	List<TweetAppUser> findByUserNameIsLikeIgnoreCase(String userName);
	

}
