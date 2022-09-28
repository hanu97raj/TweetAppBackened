package com.tweet.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweet.entities.Reply;
import com.tweet.entities.Tweet;
import com.tweet.entities.TweetAppUser;

@Repository
public interface IReplyRepository extends MongoRepository<Reply, String> {

	

}
