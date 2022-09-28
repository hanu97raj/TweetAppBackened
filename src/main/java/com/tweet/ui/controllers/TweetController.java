package com.tweet.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.event.PublicInvocationEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweet.entities.Reply;
import com.tweet.entities.Tweet;
import com.tweet.entities.TweetAppUser;
import com.tweet.entities.TweetMessage;
import com.tweet.exceptions.NeededException;
import com.tweet.services.TweetService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1.0/tweets/")
@Api(value = "/api/v1.0/tweets/", tags = "Tweet Management")
@CrossOrigin(origins = "http://localhost:4200")
public class TweetController {

	@Autowired
	private TweetService tweetService;

	/*
	 * METHOD-POST, PURPOSE-Post A New Tweet,
	 * URL-http://localhost:8090/api/v1.0/tweets/{username}/add,
	 * INPUT-[@PathVariable-username(String),@RequesParam-message(String)], OUTPUT-
	 * [Posted Tweet or Needed Exception]
	 */
	@PostMapping("{username}/add")
	@ApiOperation(value="Post A New Tweet",notes="Posts a new tweet.Return either the tweet which was posted on successful post or throws Needed Exception")
//	public Tweet PostANewTweet(@PathVariable String username, @RequestParam String message) {
	public Tweet PostANewTweet(@PathVariable String username, @RequestBody Tweet tweetModel) {
		return tweetService.postANewTweet(username, tweetModel);
	}

	/*
	 * METHOD-PUT, PURPOSE-Update A Particular Tweet,
	 * URL-http://localhost:8090/api/v1.0/tweets/{username}/update/{id},
	 * INPUT-[@PathVariable-username(String),@PathVariable id(String),@RequestParam
	 * message(String)], OUTPUT- [Updated Tweet or Needed Exception]
	 */
	@PutMapping("{username}/update/{id}")
	@ApiOperation(value="Update A  Particular Tweet",notes="Update a tweet . Returns the tweet which was updated on successful updation or throws Needed Exception ")
	public Tweet updateAParticularTweet(@PathVariable String username, @PathVariable String id,
			@RequestBody TweetMessage message) {
		return tweetService.updateAParticularTweet(username, id, message);
	}

	/*
	 * METHOD-DELETE, PURPOSE-Delete A Particular Tweet,
	 * URL-http://localhost:8090/api/v1.0/tweets/{username}/delete/{id},
	 * INPUT-[@PathVariable-username(String),@PathVariable id(String)],
	 * OUTPUT-[Deleted Message or Needed Exception]
	 */
	@DeleteMapping("{username}/delete/{id}")
	@ApiOperation(value="Delete A Tweet" , notes="Delete a tweet. Returns successful deletion message or throws Needed Exception")
	public String deleteATweet(@PathVariable String username, @PathVariable String id) {

		return tweetService.deleteATweet(username, id);
	}

	/*
	 * METHOD-GET, PURPOSE-Get All Tweets,
	 * URL-http://localhost:8090/api/v1.0/tweets/all, INPUT-[], OUTPUT-List<Tweet>
	 */
	@GetMapping("all")
	@ApiOperation(value="Get All Tweets",notes="Retrieve all tweets posted by all users sorted by time.Returns list of tweet posted")
	public List<Tweet> getAllTweets() {
		return tweetService.getAllTweets();
	}

	/*
	 * METHOD-GET, PURPOSE-Get All Tweets Of A Particular User,
	 * URL-http://localhost:8090/api/v1.0/tweets/{username},
	 * INPUT-[@PathVariable-username(String)], OUTPUT-[List<Tweet> or Needed
	 * Exception]
	 */
	@GetMapping("{username}")
	@ApiOperation(value="Get All Tweets Of A Particular User",notes="Returns all tweets of a particular user posted till date sorted by time")
	public List<Tweet> getAllTweetsOfParticularUser(@PathVariable String username) {
		return tweetService.getAllTweetsOfParticularUser(username);
	}

	/*
	 * METHOD-POST, PURPOSE-Replying To A Tweet,
	 * URL-http://localhost:8090/api/v1.0/tweets/{username}/reply/{id},
	 * INPUT-[@PathVariable-username(String), @PathVariable-id(String)
	 * , @RequestParam-message(String)], OUTPUT-[Replied Tweet or Needed Exception]
	 */
	@PostMapping("{username}/reply/{id}")
	@ApiOperation(value="Reply To A Tweet",notes="Reply to a Tweet. Returns the tweet which was replied on successful post or throws Needed Exception")
	public String replyToATweet(@PathVariable String username, @PathVariable String id, @RequestBody Reply replyModel) {
		return tweetService.replyToATweet(username, id, replyModel);
	}

	/*
	 * METHOD-POST, PURPOSE-Like A Tweet,
	 * URL-http://localhost:8090/api/v1.0/tweets/{username}/like/{id},
	 * INPUT-[@PathVariable-username(String), @PathVariable-id(String)], OUTPUT-[ Liked Message or Already liked Message]
	 */
	@PostMapping("{username}/like/{id}")
	@ApiOperation(value="Like A Tweet",notes="Like a tweet. Return message either it was liked or already liked")
	public String likeTweet(@PathVariable String username, @PathVariable String id) {
		return tweetService.likeTweet(username, id);
	}
	
	/*
	 * METHOD-POST, PURPOSE-Unlike A Tweet,
	 * URL-http://localhost:8090/api/v1.0/tweets/{username}/unlike/{id},
	 * INPUT-[@PathVariable-username(String), @PathVariable-id(String)], OUTPUT-[ Unliked Message]
	 */
	@PostMapping("{username}/unlike/{id}")
	@ApiOperation(value="Unlike A Tweet",notes="unLike a tweet. Return message it is Unliked")
	public String unLikeTweet(@PathVariable String username, @PathVariable String id) {
		return tweetService.unLikeTweet(username, id);
	}
	
	/*
	 * METHOD-GET, PURPOSE-Get Details Of A Tweet By Id,
	 * URL-http://localhost:8090/api/v1.0/tweets/tweet/{id},
	 * INPUT-[@PathVariable-id(String)], OUTPUT-[ Tweet which was retrieved for details]
	 */
	
	@GetMapping("tweet/{id}")
	@ApiOperation(value="Get Details Of A Tweet",notes="Return the tweet for which details was retrieved")
	public Tweet getDetailsOfParticulatTweet(@PathVariable String id) {
		return tweetService.getDetailsOfParticularTweet(id);
	}
	
	/*
	 * METHOD-GET, PURPOSE-Get Details Of Replies Of A Particular Tweet,
	 * URL-http://localhost:8090/api/v1.0/tweets/replies/{id},
	 * INPUT-[@PathVariable-id(String)], OUTPUT-[ List<Reply>]
	 */
	
	@GetMapping("replies/{id}")
	@ApiOperation(value="Get Details Of Replies Of A Tweet",notes="Return List Of Replies for a Tweet")
	public List<Reply> getDetailsOfRepliesOfAParticularTweet(@PathVariable String id){
		return tweetService.getDetailsOfRepliesOfAParticularTweet(id);
	}
	
	/*
	 * METHOD-GET, PURPOSE-Get List Of Tweets By Particular HashTags,
	 * URL-http://localhost:8090/api/v1.0/tweets/hashtags/{hashtags},
	 * INPUT-[@PathVariable-hashTags(String)], OUTPUT-[ List<Tweet>]
	 */
	
	@GetMapping("hashTags/{hashtags}")
	@ApiOperation(value="Get List Of Tweets Of Requested HashTags",notes="Return List Of Tweets With Particular HashTags")
	public List<Tweet> getListOfTweetsOfParticularHashTags(@PathVariable String hashtags){
		System.out.println("hashtags "+hashtags);
		return tweetService.getListOfTweetsOfParticularHashTags(hashtags);
	}

}
