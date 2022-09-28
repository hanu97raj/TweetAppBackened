package com.tweet.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockingDetails;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tweet.entities.TweetAppUser;
import com.tweet.entities.UserRegistration;
import com.tweet.exceptions.NeededException;
import com.tweet.jwt.services.JwtUtilToken;
import com.tweet.jwt.services.MyUserDetailsService;
import com.tweet.repositories.IUserRepository;

@ExtendWith(MockitoExtension.class)
public class AppUserServicesTests {

	@Mock
	private IUserRepository userRepository;
	
	@Mock 
	private JwtUtilToken jwtUtilToken;
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private MyUserDetailsService myUserDetailsService;

	private AppUserService appUserService;

	@BeforeEach
	void setUp() {
		appUserService = new AppUserService(userRepository,authenticationManager,jwtUtilToken,myUserDetailsService);
	}

	@Test
	public void getAllUsers_CheckingIfThisRunsFine_ReturnsMockedUserRepositoryRunsFineWithFindAllMethod() {

		// Invoking the method for testing
		appUserService.getAllUsers();

		// Mocking the repository for testing
		verify(userRepository).findAll();
	}

	@Test
	public void registerNewUser_AllArgumentsAreCorrect_ReturnsSavedAppUserWithAllFieldsSameAsNewPassedAppUser() {

		// User Registration details created for testing
		UserRegistration userRegistrationDetailsForTesting = new UserRegistration("Rahul", "Raj", "Rahul@gmail.com",
				"@Rahul_Raj0", "pass", "pass", "98765432");

		// Setting the TweetAppUser For testing
		TweetAppUser tweetAppUserGeneratedForTesting = new TweetAppUser();
		tweetAppUserGeneratedForTesting.setFirstName(userRegistrationDetailsForTesting.getFirstName());
		tweetAppUserGeneratedForTesting.setLastName(userRegistrationDetailsForTesting.getLastName());
		tweetAppUserGeneratedForTesting.setEmail(userRegistrationDetailsForTesting.getEmail());
		tweetAppUserGeneratedForTesting.setLoginId(userRegistrationDetailsForTesting.getLoginId());
		tweetAppUserGeneratedForTesting.setContactNumber(userRegistrationDetailsForTesting.getContactNumber());
		tweetAppUserGeneratedForTesting.setPassword(userRegistrationDetailsForTesting.getPassword());
		tweetAppUserGeneratedForTesting.setUserName("@" + userRegistrationDetailsForTesting.getLastName() + "_"
				+ userRegistrationDetailsForTesting.getFirstName() + "0");

		// Invoking the methods for testing
		appUserService.registerNewUser(userRegistrationDetailsForTesting);

		// Argument captor for capturing arguments of entities being saved into the
		// mocked repository
		ArgumentCaptor<TweetAppUser> userArgumentCaptor = ArgumentCaptor.forClass(TweetAppUser.class);
		verify(userRepository).save(userArgumentCaptor.capture());
		
		//Capturing the values of TweetAppUser which was saved
		TweetAppUser userWhichIsSaved = userArgumentCaptor.getValue();

		// Asserting that passed user and created user has same field value
		assertThat(userWhichIsSaved).isEqualToComparingFieldByField(tweetAppUserGeneratedForTesting);

	}

	@Test
	public void registerNewUser_PassingDifferentValuesOfPasswordAndConfirmPassword_ReturnNeededExceptionWithAMessage() {

		// User Registration details created for testing
		UserRegistration userRegistrationDetailsForTesting = new UserRegistration("Rahul", "Raj", "Rahul@gmail.com",
				"@Rahul_Raj0", "pas", "pass", "98765432");
		
		//Expecting method to throw the given message with given exception
		assertThatThrownBy(() -> appUserService.registerNewUser(userRegistrationDetailsForTesting))
				.isInstanceOf(NeededException.class).hasMessageContaining("Password do not match");
	}

	@Test
	public void registerNewUser_WhenASameLoginIdIsAlreadyPresentInDatabase_ReturnNeededExceptionWithAMessage() {

		// User Registration details created for testing
		UserRegistration userRegistrationDetailsForTesting = new UserRegistration("Rahul", "Raj", "Rahul@gmail.com",
				"@Rahul_Raj0", "pass", "pass", "98765432");

		//TweetAppUser object being set for testing
		TweetAppUser appUserGeneratedForTesting = new TweetAppUser();
  
		//Defining the behaviour of  some  methods which is inside of method being tested
		when(userRepository.findByLoginId(userRegistrationDetailsForTesting.getLoginId()))
				.thenReturn(appUserGeneratedForTesting);

		//Expecting method to throw the given message with given exception
		assertThatThrownBy(() -> appUserService.registerNewUser(userRegistrationDetailsForTesting))
				.isInstanceOf(NeededException.class)
				.hasMessageContaining("This login id has already been used.Please use another login id");
	}

	@Test
	public void registerNewUser_WhenASameEmailIsAlreadyPresentInDatabase_ReturnNeededExceptionWithAMessage() {

		// User Registration details created for testing
		UserRegistration userRegistrationDetailsForTesting = new UserRegistration("Rahul", "Raj", "Rahul@gmail.com",
				"@Rahul_Raj0", "pass", "pass", "98765432");

		//TweetAppUser object being set for testing
		TweetAppUser appUserGeneratedForTesting = new TweetAppUser();

		//Defining the behaviour of  some  methods which is inside of method being tested
		when(userRepository.findByEmail(userRegistrationDetailsForTesting.getEmail()))
				.thenReturn(appUserGeneratedForTesting);

		//Expecting method to throw the given message with given exception
		assertThatThrownBy(() -> appUserService.registerNewUser(userRegistrationDetailsForTesting))
				.isInstanceOf(NeededException.class)
				.hasMessageContaining("This email id has already been used.Please use another email id");
	}

	@Test
	public void searchByPartialOrFullUserName_SomeUsersIsPresentWithSameTypeOfUserName_ReturnFindByUserNameIsLikeRunsFine() {
		
		//Details generated for testing
		String username = "Ravi";
		
		//Invoking the method for testing
		appUserService.searchBypartialOrFullName(username);
		
		//Mocking the repository for testing
		verify(userRepository).findByUserNameIsLikeIgnoreCase(username);
	}

	@Test
	public void forgetPassword_CheckingIfPassedNewPasswordIsUpdatedSuccessfully_ReturnUpdatedPassword() {
		
		//Details generated for testing
		String userName = "Ravi@";
		String newPassword = "pass";

		//TweetAppuser object being generated for testing
		TweetAppUser appUserGeneratedFortesting = new TweetAppUser();
		
		//Defining the behaviour of  some  methods which is inside of method being tested
		when(userRepository.findByUserName(userName)).thenReturn(appUserGeneratedFortesting);

		//Invoking the method for testing
		appUserService.forgetPassword(userName, newPassword);

		// Argument captor for capturing arguments of entities being saved into the
		// mocked repository
		ArgumentCaptor<TweetAppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(TweetAppUser.class);
		verify(userRepository).save(appUserArgumentCaptor.capture());
		TweetAppUser appUserWhichWasSaved = appUserArgumentCaptor.getValue();

		//Asserting that password which was updated in the repository was password which was was passed into the method
		assertThat(appUserWhichWasSaved.getPassword()).isEqualTo(newPassword);

	}

}
