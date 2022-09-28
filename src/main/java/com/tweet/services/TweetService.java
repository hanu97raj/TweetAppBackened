package com.tweet.services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.tweet.entities.Reply;
import com.tweet.entities.Tweet;
import com.tweet.entities.TweetAppUser;
import com.tweet.entities.TweetMessage;
import com.tweet.exceptions.NeededException;
import com.tweet.kafka.KafkaTerminalMessage;
import com.tweet.repositories.IReplyRepository;
import com.tweet.repositories.ITweetRepository;
import com.tweet.repositories.IUserRepository;

@Service
public class TweetService {

	private ITweetRepository _iTweetRepository;

	private IUserRepository _iUserRepository;

	private IReplyRepository _iReplyRepository;

	private KafkaTemplate<String, KafkaTerminalMessage> _kafkaTemplate;

	public TweetService(ITweetRepository iTweetRepository, IUserRepository iUserRepository,
			KafkaTemplate<String, KafkaTerminalMessage> kafkaTemplate, IReplyRepository iReplyRepository) {
		_iTweetRepository = iTweetRepository;
		_iUserRepository = iUserRepository;
		_kafkaTemplate = kafkaTemplate;
		_iReplyRepository = iReplyRepository;
	}
	// Post a new tweet

//	public Tweet postANewTweet(String userName, String tweetMessage) {
	public Tweet postANewTweet(String userName, Tweet tweetModel) {

		TweetAppUser user = _iUserRepository.findByUserName(userName);
		if (user == null) {
			throw new NeededException("User not present with this username");
		}
		Tweet tweet = new Tweet();
		tweet.setFullName(user.getFirstName() + " " + user.getLastName());
		tweet.setTweetMessage(tweetModel.getTweetMessage());
		tweet.setHashTags(tweetModel.getHashTags());
		tweet.setTweetTime(new Date());
		tweet.setUserName(userName);
		tweet.setUserLikedHisOwnTweet(false);
		_iTweetRepository.save(tweet);
		KafkaTerminalMessage kafkaTerminalMessage = new KafkaTerminalMessage(tweetModel.getTweetMessage(), tweet.getTweetTime());

		/* The below line must be uncommented to consume tweets through kafka */
		// _kafkaTemplate.send("Tweet", kafkaTerminalMessage);

		List<Tweet> tweetList = user.getTweets();
		tweetList.add(tweet);
		user.setTweets(tweetList);
		_iUserRepository.save(user);
		return tweet;

	}

	// Update A Posted Tweet

	public Tweet updateAParticularTweet(String userName, String id, TweetMessage tweetMessage) {

		TweetAppUser userByUserName = _iUserRepository.findByUserName(userName);

		if (userByUserName == null) {
			throw new NeededException("User does not exist");
		}
		Tweet tweetupdated = null;
		if (userByUserName != null) {
			List<Tweet> getAllTweets = userByUserName.getTweets();
			for (int i = 0; i < getAllTweets.size(); i++) {
				Tweet currentTweet = getAllTweets.get(i);
				if (currentTweet.getId().equals(id)) {
					currentTweet.setTweetMessage(tweetMessage.getMessage());
					_iTweetRepository.save(currentTweet);
					tweetupdated = currentTweet;
				}
			}

			if (tweetupdated == null) {
				throw new NeededException("Particular tweet does not exist for this username");
			}

		}
		return tweetupdated;
	}

	// Delete A Tweet

//	public String deleteATweet(String userName, String id) {
//		TweetAppUser userByUserName = _iUserRepository.findByUserName(userName);
//
//		Tweet tweetToBeDeletedTweet = null;
//		if (userByUserName == null) {
//			throw new NeededException("User does not exist");
//		}
//		List<Tweet> getAllTweets = userByUserName.getTweets();
//		if (userByUserName != null) {
//			for (int i = 0; i < getAllTweets.size(); i++) {
//				Tweet currentTweet = getAllTweets.get(i);
//				if (currentTweet.getId().equals(id)) {
//					tweetToBeDeletedTweet = currentTweet;
//				}
//			}
//		}
//
//		if (tweetToBeDeletedTweet != null) {
//			getAllTweets.remove(tweetToBeDeletedTweet);
//			_iUserRepository.save(userByUserName);
//			_iTweetRepository.deleteById(id);
//
//			return new String("Tweet deleted successfully");
//		}
//		return new String("No tweet deleted.Wrong id entered");
//	}

	public String deleteATweet(String userName, String id) {
		TweetAppUser userByUserName = _iUserRepository.findByUserName(userName);
		String parentTweetId = null;
		try {
			Reply replyToBeDeleted = _iReplyRepository.findById(id).get();

			int sizeOfRepliesList = replyToBeDeleted.getRepliesList().size();
			if (sizeOfRepliesList != 0) {
				for (int i = 0; i < sizeOfRepliesList; i++) {
					_iReplyRepository.deleteById(replyToBeDeleted.getRepliesList().get(i));
				}
			}
			parentTweetId = replyToBeDeleted.getParentTweetId();
			_iReplyRepository.deleteById(id);
		} catch (Exception e) {
			System.out.println("Tweet will be deleted");
		}

		if (parentTweetId != null) {
			try {
				Tweet parentFoundInTweetdb = _iTweetRepository.findById(parentTweetId).get();
				List<String> repliesId = parentFoundInTweetdb.getRepliesList();
				repliesId.remove(parentTweetId);
				parentFoundInTweetdb.setRepliesList(repliesId);
				_iTweetRepository.save(parentFoundInTweetdb);
				return "ReplyId   deleted in tweet and reply deleted successfully";
			}

			catch (Exception e) {
				System.out.println("Parent found in reply db");
				return "Reply deleted successfully";
			}
		}

		Tweet tweetToBeDeletedTweet = null;
		if (userByUserName == null) {
			throw new NeededException("User does not exist");
		}
		List<Tweet> getAllTweets = userByUserName.getTweets();
		if (userByUserName != null) {
			for (int i = 0; i < getAllTweets.size(); i++) {
				Tweet currentTweet = getAllTweets.get(i);
				System.out.println("currentTweet " + currentTweet.getId());
				if (currentTweet.getId().equals(id)) {
					tweetToBeDeletedTweet = currentTweet;
					System.out.println("currentTweet " + currentTweet.getId());
				}
			}
		}

		if (tweetToBeDeletedTweet != null) {
			getAllTweets.remove(tweetToBeDeletedTweet);
			_iUserRepository.save(userByUserName);
			_iTweetRepository.deleteById(id);

			System.out.println("tweetToBeDeletedTweet " + tweetToBeDeletedTweet.getId());

			return new String("Tweet deleted successfully");
		}
		return new String("No tweet deleted.Wrong id entered");
	}

	// Get All Tweets Of Users

	public List<Tweet> getAllTweets() {
		return _iTweetRepository.findAll();
	}

//Get All Tweets Of A Particular User

	public List<Tweet> getAllTweetsOfParticularUser(String userName) {

		TweetAppUser user = _iUserRepository.findByUserName(userName);
		if (user == null) {
			throw new NeededException("username is invalid");
		}

		List<Tweet> allTweetsOfthisUser = user.getTweets();
		return allTweetsOfthisUser;
	}

	// Reply To A Tweet

//	public Tweet replyToATweet(String userName, String id, String message) {
//		Tweet tweetBeingReplied = _iTweetRepository.findById(id).get();
//		TweetAppUser infoOfReplyingUser = _iUserRepository.findByUserName(userName);
//		if (tweetBeingReplied == null) {
//			throw new NeededException("Id you passed is incorrect");
//		}
//		if (infoOfReplyingUser == null) {
//			throw new NeededException("UserName you passed is incorrect");
//		}
//		Tweet tweetToReply = new Tweet();
//		tweetToReply.setId(new ObjectId().toString());
//		tweetToReply.setFullName(infoOfReplyingUser.getFirstName() + " " + infoOfReplyingUser.getLastName());
//		tweetToReply.setTweetMessage(message);
//		tweetToReply.setTweetTime(new Date());
//		tweetToReply.setUserName(userName);
//		tweetToReply.setRepliesList(new ArrayList<>());
//		List<Tweet> allRepliesToThisId = tweetBeingReplied.getRepliesList();
//		allRepliesToThisId.add(tweetToReply);
//		tweetBeingReplied.setRepliesList(allRepliesToThisId);
//		_iTweetRepository.save(tweetBeingReplied);
////		_iReplyRepository.save(tweetToReply);
//		System.out.println("yes");
//		return tweetBeingReplied;
//	}

	public String replyToATweet(String userName, String id, Reply replyModel) {

		TweetAppUser infoOfReplyingUser = _iUserRepository.findByUserName(userName);

//		if (tweetBeingReplied == null) {
//			throw new NeededException("Id you passed is incorrect");
//		}
//		if (infoOfReplyingUser == null) {
//			throw new NeededException("UserName you passed is incorrect");
//		}
		Reply tweetToReply = new Reply();
		String newId = new ObjectId().toString();
		tweetToReply.setId(newId);
		tweetToReply.setFullName(infoOfReplyingUser.getFirstName() + " " + infoOfReplyingUser.getLastName());
		tweetToReply.setTweetMessage(replyModel.getTweetMessage());
		tweetToReply.setHashTags(replyModel.getHashTags());
		tweetToReply.setTweetTime(new Date());
		tweetToReply.setUserName(userName);
		tweetToReply.setRepliesList(new ArrayList<>());
		tweetToReply.setUserLikedHisOwnReply(false);
		tweetToReply.setParentTweetId(id);

		try {
			Reply replyBeingReplied = _iReplyRepository.findById(id).get();
			List<String> allRepliesToThisId = replyBeingReplied.getRepliesList();
			allRepliesToThisId.add(newId);
			replyBeingReplied.setRepliesList(allRepliesToThisId);
			_iReplyRepository.save(replyBeingReplied);
		}

		catch (NoSuchElementException e) {
			System.out.println("ReplyRepo Skipped");
		}

		try {
			Tweet tweetBeingReplied = _iTweetRepository.findById(id).get();
			List<String> allRepliesToThisId = tweetBeingReplied.getRepliesList();
			allRepliesToThisId.add(newId);
			tweetBeingReplied.setRepliesList(allRepliesToThisId);
			_iTweetRepository.save(tweetBeingReplied);
		} catch (NoSuchElementException e) {
			System.out.println("TweetRepo Skipped");
		}

		_iReplyRepository.save(tweetToReply);
		// System.out.println("yes");
		return "Saved in Reply Db";
	}

	// Like A Tweet

//	public String likeTweet(String userName, String id) {
//
//		Tweet tweetTobeLikedTweet = _iTweetRepository.findById(id).get();
//		List<TweetAppUser> getListOfUsersWhoLiked = null;
//		TweetAppUser userWhoLiked = _iUserRepository.findByUserName(userName);
//		// System.out.println(userWhoLiked.getId());
//		// if (tweetTobeLikedTweet.getUsersWhoLiked() == null) {
//		if (tweetTobeLikedTweet.getLikes() == 0) {
//			tweetTobeLikedTweet.setUsersWhoLiked(new ArrayList<TweetAppUser>());
//			getListOfUsersWhoLiked = new ArrayList<TweetAppUser>();
//		} else {
//			// tweetTobeLikedTweet.setUsersWhoLiked(tweetTobeLikedTweet.getUsersWhoLiked());
//			getListOfUsersWhoLiked = tweetTobeLikedTweet.getUsersWhoLiked();
//
//		}
//
//		// System.out.println(getListOfUsersWhoLiked);
//
//		// if((getListOfUsersWhoLiked.contains(userWhoLiked.get))==false) {
//
//		for (int i = 0; i < getListOfUsersWhoLiked.size(); i++) {
//			if (getListOfUsersWhoLiked.get(i).getUserName().equals(userName)) {
//				return "Already liked this tweet";
//			}
//		}
//
//		// tweetTobeLikedTweet.setLikes(getListOfUsersWhoLiked.size() + 1);
//		tweetTobeLikedTweet.setLikes(tweetTobeLikedTweet.getLikes() + 1);
//		tweetTobeLikedTweet.getUsersWhoLiked().add(userWhoLiked);
//		tweetTobeLikedTweet.setUsersWhoLiked(tweetTobeLikedTweet.getUsersWhoLiked());
//		// getListOfUsersWhoLiked.add(userWhoLiked);
//		// tweetTobeLikedTweet.setUsersWhoLiked(getListOfUsersWhoLiked);
//		_iTweetRepository.save(tweetTobeLikedTweet);
//
//		return userName + "liked this tweet";
//
//	}

	public String likeTweet(String userName, String id) {

		TweetAppUser userWhoLiked = _iUserRepository.findByUserName(userName);
		Tweet tweetToBeLiked = null;
		Reply replyToBeLiked = null;
		List<TweetAppUser> getListOfUsersWhoLiked = null;

		try {
			
			tweetToBeLiked = _iTweetRepository.findById(id).get();
			
			if (tweetToBeLiked.getUserName().equals(userName)) {
				if (tweetToBeLiked.getUserLikedHisOwnTweet() == true) {
					return "Already liked this tweet";
				} else {
					System.out.println("likes here");
					tweetToBeLiked.setUserLikedHisOwnTweet(true);
					TweetAppUser userGeneratedForRemovingJacksonError=new TweetAppUser();
					userGeneratedForRemovingJacksonError.setUserName(userName);
					if(tweetToBeLiked.getLikes()==0) {
						tweetToBeLiked.setUsersWhoLiked(new ArrayList<TweetAppUser>());
					}
					tweetToBeLiked.getUsersWhoLiked().add(userGeneratedForRemovingJacksonError);
					tweetToBeLiked.setUsersWhoLiked(tweetToBeLiked.getUsersWhoLiked());
					tweetToBeLiked.setLikes(tweetToBeLiked.getLikes() + 1);
					_iTweetRepository.save(tweetToBeLiked);
					
					return  " Liked this tweet itself";
				}
			}

			else {
			
				
				if (tweetToBeLiked.getLikes() == 0) {
					tweetToBeLiked.setUsersWhoLiked(new ArrayList<TweetAppUser>());
					
					getListOfUsersWhoLiked = new ArrayList<TweetAppUser>();
				} else {
					// tweetTobeLikedTweet.setUsersWhoLiked(tweetTobeLikedTweet.getUsersWhoLiked());
				
					getListOfUsersWhoLiked = tweetToBeLiked.getUsersWhoLiked();

				}

			}

		}

		catch (Exception e) {
			System.out.println("user liking reply");
		}

		try {
			
			replyToBeLiked = _iReplyRepository.findById(id).get();
			
			
			if (replyToBeLiked.getUserName().equals(userName)) {
				if (replyToBeLiked.getUserLikedHisOwnReply() == true) {
					return "Already liked this reply";
				} else {
					
					replyToBeLiked.setUserLikedHisOwnReply(true);
					
					TweetAppUser userGeneratedForRemovingJacksonError=new TweetAppUser();
					userGeneratedForRemovingJacksonError.setUserName(userName);
					//System.out.println(replyToBeLiked.getLikes());
					if(replyToBeLiked.getLikes()==0) {
						
						replyToBeLiked.setUsersWhoLiked(new ArrayList<TweetAppUser>());
					}
					replyToBeLiked.getUsersWhoLiked().add(userGeneratedForRemovingJacksonError);
					
					replyToBeLiked.setUsersWhoLiked(replyToBeLiked.getUsersWhoLiked());
					replyToBeLiked.setLikes(replyToBeLiked.getLikes() + 1);
					
					_iReplyRepository.save(replyToBeLiked);
					
					return userName + " liked this Reply";
				}
			} else {
				if (replyToBeLiked.getLikes() == 0) {
					replyToBeLiked.setUsersWhoLiked(new ArrayList<TweetAppUser>());
					getListOfUsersWhoLiked = new ArrayList<TweetAppUser>();
				} else {
					// tweetTobeLikedTweet.setUsersWhoLiked(tweetTobeLikedTweet.getUsersWhoLiked());
					getListOfUsersWhoLiked = replyToBeLiked.getUsersWhoLiked();

				}
			}

		}

		catch (Exception e) {
			System.out.println("user liking tweet ");
		}

		for (int i = 0; i < getListOfUsersWhoLiked.size(); i++) {
			if (getListOfUsersWhoLiked.get(i).getUserName().equals(userName)) {
				return "Already liked this tweet";
			}
		}

		if (replyToBeLiked == null) {
			tweetToBeLiked.setLikes(tweetToBeLiked.getLikes() + 1);
			tweetToBeLiked.getUsersWhoLiked().add(userWhoLiked);
			tweetToBeLiked.setUsersWhoLiked(tweetToBeLiked.getUsersWhoLiked());
			_iTweetRepository.save(tweetToBeLiked);
		} else {
			replyToBeLiked.setLikes(replyToBeLiked.getLikes() + 1);
			replyToBeLiked.getUsersWhoLiked().add(userWhoLiked);
			replyToBeLiked.setUsersWhoLiked(replyToBeLiked.getUsersWhoLiked());
			_iReplyRepository.save(replyToBeLiked);
		}

		return userName + "liked this tweet";

	}

	// Unlike A Tweet

//	public String unLikeTweet(String userName, String id) {
//		Tweet tweetTobeUnlikedTweet = _iTweetRepository.findById(id).get();
//		List<TweetAppUser> getListOfUsersWhoLiked = null;
//		TweetAppUser userWhoUnliked = _iUserRepository.findByUserName(userName);
//		getListOfUsersWhoLiked = tweetTobeUnlikedTweet.getUsersWhoLiked();
//
//		tweetTobeUnlikedTweet.setLikes(tweetTobeUnlikedTweet.getLikes() - 1);
//		for (int i = 0; i < getListOfUsersWhoLiked.size(); i++) {
//			if (getListOfUsersWhoLiked.get(i).getUserName().equals(userName)) {
//				tweetTobeUnlikedTweet.getUsersWhoLiked().remove(getListOfUsersWhoLiked.get(i));
//			}
//		}
//
//		tweetTobeUnlikedTweet.setUsersWhoLiked(tweetTobeUnlikedTweet.getUsersWhoLiked());
//
//		// System.out.println(tweetTobeUnlikedTweet.getUsersWhoLiked());
//
//		_iTweetRepository.save(tweetTobeUnlikedTweet);
//		return userName + " unliked this tweet";
//
//	}

	public String unLikeTweet(String userName, String id) {
		List<TweetAppUser> getListOfUsersWhoLiked = null;
		Tweet tweetToBeUnliked = null;
		Reply replyToBeUnliked = null;

		TweetAppUser userWhoUnliked = _iUserRepository.findByUserName(userName);

		try {
			tweetToBeUnliked = _iTweetRepository.findById(id).get();
			
			if (tweetToBeUnliked.getUserName().equals(userName)) {
				
				tweetToBeUnliked.setUserLikedHisOwnTweet(false);
				System.out.println("unliked here");
				_iTweetRepository.save(tweetToBeUnliked);
				
			}
			
			getListOfUsersWhoLiked = tweetToBeUnliked.getUsersWhoLiked();
			tweetToBeUnliked.setLikes(tweetToBeUnliked.getLikes() - 1);
			for (int i = 0; i < getListOfUsersWhoLiked.size(); i++) {
				if (getListOfUsersWhoLiked.get(i).getUserName().equals(userName)) {
					tweetToBeUnliked.getUsersWhoLiked().remove(getListOfUsersWhoLiked.get(i));
				}
			}
			//tweetToBeUnliked.getUsersWhoLiked().remove(userWhoUnliked);
			tweetToBeUnliked.setUsersWhoLiked(tweetToBeUnliked.getUsersWhoLiked());
			_iTweetRepository.save(tweetToBeUnliked);

		} catch (Exception e) {
System.out.println("reply unliked "+ e);
		}

		try {
			replyToBeUnliked = _iReplyRepository.findById(id).get();
			if (replyToBeUnliked.getUserName().equals(userName)) {
				
				replyToBeUnliked.setUserLikedHisOwnReply(false);
				_iReplyRepository.save(replyToBeUnliked);
				
			}
			getListOfUsersWhoLiked = replyToBeUnliked.getUsersWhoLiked();
			replyToBeUnliked.setLikes(replyToBeUnliked.getLikes() - 1);
			for (int i = 0; i < getListOfUsersWhoLiked.size(); i++) {
				if (getListOfUsersWhoLiked.get(i).getUserName().equals(userName)) {
					replyToBeUnliked.getUsersWhoLiked().remove(getListOfUsersWhoLiked.get(i));
				}
			}
			
			//replyToBeUnliked.getUsersWhoLiked().remove(userWhoUnliked);
			replyToBeUnliked.setUsersWhoLiked(replyToBeUnliked.getUsersWhoLiked());
			_iReplyRepository.save(replyToBeUnliked);
			
		} catch (Exception e) {
 System.out.println("tweet unliked");
		}

		
		
		


		// System.out.println(tweetTobeUnlikedTweet.getUsersWhoLiked());

		
		return userName + " unliked this tweet";

	}

//	public List<Reply> getDetailsOfRepliesOfAParticularTweet(String id) {
//		List<Reply> allRepliesToThisTweet=new ArrayList<>();
//		
//		Tweet tweetDetails = _iTweetRepository.fin dById(id).get();
//		List<String> repliesIdList=tweetDetails.getRepliesList();
//		
//		for(int i=0;i<repliesIdList.size();i++) {
//			allRepliesToThisTweet.add(_iReplyRepository.findById(repliesIdList.get(i)).get());
//		}
//		
//		return allRepliesToThisTweet;
//	}

	public List<Reply> getDetailsOfRepliesOfAParticularTweet(String id) {
		List<Reply> allRepliesToThisTweet = new ArrayList<>();
		List<String> repliesIdList = null;

		try {
			Tweet tweetDetails = _iTweetRepository.findById(id).get();
			repliesIdList = tweetDetails.getRepliesList();
		} catch (Exception e) {

		}

		try {
			Reply replyDetails = _iReplyRepository.findById(id).get();
			repliesIdList = replyDetails.getRepliesList();
		} catch (Exception e) {
		}

		if (repliesIdList == null) {
			return null;
		}

		for (int i = 0; i < repliesIdList.size(); i++) {
			allRepliesToThisTweet.add(_iReplyRepository.findById(repliesIdList.get(i)).get());
		}

		return allRepliesToThisTweet;
	}

	public Tweet getDetailsOfParticularTweet(String id) {
		Tweet tweetDetails = null;
		try {
			tweetDetails = _iTweetRepository.findById(id).get();
		} catch (Exception e) {

		}

		return tweetDetails;
	}

	public List<Tweet> getListOfTweetsOfParticularHashTags(String hashtags) {
		
		return _iTweetRepository.findByHashTagsIsLikeIgnoreCase(hashtags);
		
	}

}
