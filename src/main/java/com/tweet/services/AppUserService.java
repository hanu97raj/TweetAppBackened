package com.tweet.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tweet.entities.PublicUserDetails;
import com.tweet.entities.Tweet;
import com.tweet.entities.TweetAppUser;
import com.tweet.entities.UserRegistration;
import com.tweet.exceptions.NeededException;
import com.tweet.jwt.authenticate.AuthenticateRequest;
import com.tweet.jwt.authenticate.AuthenticateResponse;
import com.tweet.jwt.services.JwtUtilToken;
import com.tweet.jwt.services.MyUserDetailsService;
import com.tweet.repositories.IUserRepository;
@Service
public class AppUserService {
	
	private IUserRepository _iUserRepository;
	
	
	 private AuthenticationManager _authenticationManager;
	

	 private JwtUtilToken _jwtUtilToken;
	
	
	 private MyUserDetailsService _myUserDetailsService;
	
	public AppUserService(IUserRepository userRepository, AuthenticationManager authenticationManager,JwtUtilToken jwtUtilToken,MyUserDetailsService myUserDetailsService) {
		_iUserRepository=userRepository;
		_authenticationManager=authenticationManager;
		_jwtUtilToken=jwtUtilToken;
		_myUserDetailsService=myUserDetailsService;
	}
	
	//Register User
	
	public TweetAppUser registerNewUser(UserRegistration userDetails){
		TweetAppUser user=new TweetAppUser();
		
		if(!userDetails.getConfirmPassword().equals(userDetails.getPassword())) {
			throw new NeededException("Password do not match");
		}
		
		if(_iUserRepository.findByLoginId(userDetails.getLoginId())!=null) {
			throw new NeededException("This login id has already been used.Please use another login id");
		}
		if(_iUserRepository.findByEmail(userDetails.getEmail()) !=null) {
			throw new NeededException("This email id has already been used.Please use another email id");
		}
		
		String presentCount=String.valueOf(_iUserRepository.
				findByFirstNameAndLastName(userDetails.getFirstName(), userDetails.getLastName()).size());
		
		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setEmail(userDetails.getEmail());
		user.setLoginId(userDetails.getLoginId());
		user.setContactNumber(userDetails.getContactNumber());
		user.setPassword(userDetails.getPassword());
		user.setUserName("@"+(userDetails.getLastName()+"_"+userDetails.getFirstName())+presentCount);
		user.setJoinedDate(new Date());
		return _iUserRepository.save(user);
	}
	
	//User Login
	
	public AuthenticateResponse userLogin(AuthenticateRequest request)  {
		try {
			_authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLoginId(),request.getPassword()));
		}
		catch (BadCredentialsException e) {
			throw new NeededException("Invalid loginId or password");
		}
		final UserDetails userDetails=_myUserDetailsService.loadUserByUsername(request.getLoginId());
		final String token=_jwtUtilToken.generateToken(userDetails);
		
		TweetAppUser appUser=_iUserRepository.findByLoginId(request.getLoginId());
		return new AuthenticateResponse(token,appUser);
	}
	
	//Get All Users
	
	public List<PublicUserDetails> getAllUsers(){
		List<TweetAppUser> allUsers=_iUserRepository.findAll();
		List<PublicUserDetails> publicUserDetailsList=new ArrayList<PublicUserDetails>();
		for(int i=0;i<allUsers.size();i++) {
			String emailFound=allUsers.get(i).getEmail();
			String userNameFound=allUsers.get(i).getUserName();
			String firstNameFound=allUsers.get(i).getFirstName();
			String lastNameFound=allUsers.get(i).getLastName();
			Date joinedDateFound=allUsers.get(i).getJoinedDate();
			List<Tweet> tweetsFound=allUsers.get(i).getTweets();
			publicUserDetailsList.add(new PublicUserDetails(firstNameFound,lastNameFound,emailFound,joinedDateFound,userNameFound,tweetsFound));
		}
		return publicUserDetailsList;
	}
	
	//Search By Partial Or Full Name
	
		public List<PublicUserDetails> searchBypartialOrFullName(String userName){
			
			List<TweetAppUser> usersHavingSameTypesOfUserName=_iUserRepository.findByUserNameIsLikeIgnoreCase(userName);
			List<PublicUserDetails> publicUserDetailsList=new ArrayList<PublicUserDetails>();
			for(int i=0;i<usersHavingSameTypesOfUserName.size();i++) {
				String emailFound=usersHavingSameTypesOfUserName.get(i).getEmail();
				String userNameFound=usersHavingSameTypesOfUserName.get(i).getUserName();
				String firstNameFound=usersHavingSameTypesOfUserName.get(i).getFirstName();
				String lastNameFound=usersHavingSameTypesOfUserName.get(i).getLastName();
				Date joinedDateFound=usersHavingSameTypesOfUserName.get(i).getJoinedDate();
				List<Tweet> tweetsFound=usersHavingSameTypesOfUserName.get(i).getTweets();
				publicUserDetailsList.add(new PublicUserDetails(firstNameFound,lastNameFound,emailFound,joinedDateFound,userNameFound,tweetsFound));
			}
			
			
			return publicUserDetailsList;
		}
		
		// Forget Password

		public String forgetPassword(String userName, String password) {
			TweetAppUser user = _iUserRepository.findByUserName(userName);
			user.setPassword(password);
			_iUserRepository.save(user);
			return "Password set Successfully";
		}

		public PublicUserDetails getUserByUsername(String username) {
		TweetAppUser appUser=_iUserRepository.findByUserName(username);
		PublicUserDetails publicUserDetails=new PublicUserDetails(appUser.getFirstName(),appUser.getLastName(),
				appUser.getEmail(),appUser.getJoinedDate(),appUser.getUserName(),appUser.getTweets());
			return publicUserDetails;
		}


}
