package com.tweet.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweet.entities.Tweet;
import com.tweet.entities.TweetAppUser;

@Repository
public interface ITweetRepository extends MongoRepository<Tweet, String> {

	List<Tweet> findByUserName(String userName);
	TweetAppUser findByUsersWhoLikedUserName(String userName);
	List<Tweet> findByHashTagsIsLikeIgnoreCase(String hashTags);

}
