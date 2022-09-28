package com.tweet.services;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tweet.entities.Tweet;
import com.tweet.entities.TweetAppUser;
import com.tweet.entities.TweetMessage;
import com.tweet.exceptions.NeededException;
import com.tweet.kafka.KafkaTerminalMessage;
import com.tweet.repositories.IReplyRepository;
import com.tweet.repositories.ITweetRepository;
import com.tweet.repositories.IUserRepository;

@ExtendWith(MockitoExtension.class)
public class TweetServicesTests {
	
	@Mock
	private IUserRepository iUserRepository;
	
	@Mock
	private ITweetRepository iTweetRepository;
	
	@Mock
	private IReplyRepository iReplyRepository;
	
	@Mock
	private KafkaTemplate<String, KafkaTerminalMessage> kafkaTemplate;
	
	private TweetService tweetService;
	
	@BeforeEach
	void setUp() {
		tweetService=new TweetService(iTweetRepository, iUserRepository, kafkaTemplate,iReplyRepository);
	}
	
	@Test
	public void postANewTweet_UserIsAlreadyPresent_returnATweetIsCreatedAndSavedSuccessfullyWithPassedMessage() {
		
		//Details for creating testing entities
		String tweetMessage="Tweeted First Time";
		String userName="Ravi@";
		String firstName="Ravi";
		String lastName="Kumar";
		
		//Generating TweetAppuser for testing
		TweetAppUser userGeneratedForTesting=new TweetAppUser();
		userGeneratedForTesting.setUserName(userName);
		userGeneratedForTesting.setFirstName(firstName);
		userGeneratedForTesting.setLastName(lastName);
		
		//Generating Tweet for testing
		Tweet tweetGeneratedFortesting=new Tweet();
		tweetGeneratedFortesting.setUserName(userName);
		tweetGeneratedFortesting.setFullName(firstName+" "+lastName);
		tweetGeneratedFortesting.setTweetMessage(tweetMessage);
		
		
		
		//Defining the behaviour of  some  methods which is inside of method being tested
		when(iUserRepository.findByUserName(userName)).thenReturn(userGeneratedForTesting);
		
		//Invoking the method for testing
		tweetService.postANewTweet(userName, tweetGeneratedFortesting);
		
		//Argument captor for capturing arguments of entities being saved into the mocked repository
		ArgumentCaptor<Tweet> tweetArgumentCaptor=ArgumentCaptor.forClass(Tweet.class);
		verify(iTweetRepository).save(tweetArgumentCaptor.capture());
		
		//Fetching the captured values into a tweet object
		Tweet tweetWhichIsSavedTweet=tweetArgumentCaptor.getValue();
	
		//Comparing the fields of tweet object which is saved and tweet object generated for testing
		assertThat(tweetWhichIsSavedTweet).isEqualToIgnoringGivenFields(tweetGeneratedFortesting,"tweetTime");
	}
	
	@Test
	public void postANewTweet_UserIsNotPresent_ReturnNeededExceptionWithAMessage() {
		
		//Details for creating testing entities
		String tweetMessage="Tweeted First Time";
		String userName="Ravi";
		
		//Generating TweetAppuser for testing
		TweetAppUser appUserGeneratedFortesting=null;
		
		Tweet tweetGeneratedForTesting=new Tweet();
		tweetGeneratedForTesting.setTweetMessage(tweetMessage);
		
		//Defining the behaviour of  some  methods which is inside of method being tested
		when(iUserRepository.findByUserName(userName)).thenReturn(appUserGeneratedFortesting);
		
		//Expecting the method being tested to throw an exception with given exception
		assertThatThrownBy(()->tweetService.postANewTweet(userName, tweetGeneratedForTesting)).isInstanceOf(NeededException.class)
		.hasMessageContaining("User not present with this username");
	}
	
	@Test
	public void updateAParticularTweet_UserIsPresentWithParticularTweetId_ReturnUpdatedTweetMessage() {
		
		//Details for creating testing entities
		String oldTweetMessage="Old Tweet";
		TweetMessage tweetMessage=new TweetMessage();
		tweetMessage.setMessage("New Tweet");
		
		String userName="Ravi@";
		String id="1234";
		
		//Tweet object and TweetAppUser object being set for testing
		TweetAppUser userGeneratedForTesting=new TweetAppUser();
		Tweet tweetGeneratedForTesting=new Tweet();
		tweetGeneratedForTesting.setTweetMessage(oldTweetMessage);
		tweetGeneratedForTesting.setId(id);
		List<Tweet> tweetsList=userGeneratedForTesting.getTweets();
		tweetsList.add(tweetGeneratedForTesting);
		userGeneratedForTesting.setTweets(tweetsList);
		
		//Defining the behaviour of  some  methods which is inside of method being tested
		when(iUserRepository.findByUserName(userName)).thenReturn(userGeneratedForTesting);
		
		//Invoking the method for testing
		tweetService.updateAParticularTweet(userName, id, tweetMessage);
		
		//Argument captor for capturing arguments of entities being saved into the mocked repository
		ArgumentCaptor<Tweet> tweetArgumentCaptor=ArgumentCaptor.forClass(Tweet.class);
		verify(iTweetRepository).save(tweetArgumentCaptor.capture());
		
		//Fetching the captured values into an  object object
		Tweet tweetBeingSaved=tweetArgumentCaptor.getValue();
		
		//comparing the message of tweet being saved and message which is passed to testing method
		assertThat(tweetBeingSaved.getTweetMessage()).isEqualTo(tweetMessage.getMessage());
	}
	
	@Test
	public void updateAParticularTweet_UserIsNotPresent_ReturnNeededExceptionWithAMessage() {
		
		//Details for creating testing entities
		TweetMessage tweetMessage=new TweetMessage();
		tweetMessage.setMessage("New Tweet");
		String userName="Ravi@";
		String id="1234";
		
		// TweetAppUser object being set for testing
		TweetAppUser userGeneratedForTesting=null;
		
		//Defining the behaviour of  some  methods which is inside of method being tested
		when(iUserRepository.findByUserName(userName)).thenReturn(userGeneratedForTesting);
		
		//Expecting the method being tested to throw an exception with given exception
		assertThatThrownBy(()->tweetService.updateAParticularTweet(userName,id,tweetMessage)).isInstanceOf(NeededException.class)
		.hasMessageContaining("User does not exist");
		
	}
	
	@Test
	public void deleteATweet_UserIsPresentWithTwoTweets_ReturnUserWithOneTweetAfterDeleting() {
		
		//Details for creating testing entities
		String userName="Ravi@";
		String idOfTweetToBeDeleted="123";
		
		//Tweet and TweetAppUser object being set for testing
		Tweet newTweetforTesting=new Tweet();
		newTweetforTesting.setId(idOfTweetToBeDeleted);
		TweetAppUser newAppUserForTesting=new TweetAppUser();
		newAppUserForTesting.setUserName(userName);
		List<Tweet> tweetList=newAppUserForTesting.getTweets();
		tweetList.add(newTweetforTesting);
		newAppUserForTesting.setTweets(tweetList);
		
		//Defining the behaviour of  some  methods which is inside of method being tested
		when(iUserRepository.findByUserName(userName)).thenReturn(newAppUserForTesting);
		
		//Invoking the method for testing
		tweetService.deleteATweet(userName, idOfTweetToBeDeleted);
		
		//Argument captor for capturing arguments of entities being saved into the mocked repository
		ArgumentCaptor<String> idArgumentCaptor=ArgumentCaptor.forClass(String.class);
		verify(iTweetRepository).deleteById(idArgumentCaptor.capture());
		
		//capturing the values of id for which corresponding tweet is being deleted
		String tweetIdDeleted=idArgumentCaptor.getValue();
		
		//Comparing if id of tweetbeingdeleted matches to id which was passed
		assertThat(tweetIdDeleted).isEqualTo(idOfTweetToBeDeleted);
	}
	
	@Test
	public void deleteATweet_UserIsPresentButThatParticularTweetIdIsNotpresent_ReturnMessageLikeWrongIdEntered() {
		
		//Details for creating testing entities
		String userName="Ravi@";
		String idOfTweetToBeDeleted="123";
		String idWhichDoNotExist="456";
		String expectedErrorMessage="No tweet deleted.Wrong id entered";
		
		//Tweet object and TweetAppUser object being set for testing
		Tweet newTweetForTesting=new Tweet();
		newTweetForTesting.setId(idOfTweetToBeDeleted);
		TweetAppUser newAppUserForTesting=new TweetAppUser();
		newAppUserForTesting.setUserName(userName);
		List<Tweet> tweetList=newAppUserForTesting.getTweets();
		tweetList.add(newTweetForTesting);
		newAppUserForTesting.setTweets(tweetList);
		
		//Defining the behaviour of  some  methods which is inside of method being tested
		when(iUserRepository.findByUserName(userName)).thenReturn(newAppUserForTesting);
		
		//Comparing the message which the testing method throws with the message being expected to be thrown
		assertThat(tweetService.deleteATweet(userName, idWhichDoNotExist)).isEqualTo(expectedErrorMessage);
	}
	
	@Test
	public void deleteAParticularTweet_NoUserWithGivenUserNameIspresent_ReturnNeededExceptionWithAMessage() {
		//Details for creating testing entities
		String userName="Ravi@";
		String idOfTweetToBeDeleted="123";
		
		//Tweet object and TweetAppuser object being set for testing
		Tweet newTweetForTesting=new Tweet();
		newTweetForTesting.setId(idOfTweetToBeDeleted);
		TweetAppUser newAppUserForTesting=null;
		
		//Defining the behaviour of  some  methods which is inside of method being tested
		when(iUserRepository.findByUserName(userName)).thenReturn(newAppUserForTesting);
		
		//Expecting the method being tested to throw an exception with given exception
		assertThatThrownBy(()->tweetService.deleteATweet(userName, idOfTweetToBeDeleted))
				.isInstanceOf(NeededException.class).hasMessageContaining("User does not exist");
		
	}
	
	@Test
	public void getAllTweets_CheckingIfThisRunsFine_ReturnMockedTweetRepositoryRunsFineWithFindAllmethod() {
		
		//invoking the method for testing
		tweetService.getAllTweets();
		
		//Mocking the repository to perform testing
		verify(iTweetRepository).findAll();
	}
	
	
	
	

}
