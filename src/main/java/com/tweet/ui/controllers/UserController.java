package com.tweet.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweet.entities.PublicUserDetails;
import com.tweet.entities.TweetAppUser;
import com.tweet.entities.UserRegistration;
import com.tweet.exceptions.NeededException;
import com.tweet.jwt.authenticate.AuthenticateRequest;
import com.tweet.jwt.authenticate.AuthenticateResponse;
import com.tweet.services.AppUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1.0/tweets/")
@Api(value = "/api/v1.0/tweets/", tags = "User Management")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	@Autowired
	private AppUserService appUserService;

	/*
	 * METHOD-POST, PURPOSE-Register User,
	 * URL-http://localhost:8090/api/v1.0/tweets/register,
	 * INPUT-[@RequestBody-UserRegistration Object], OUTPUT-[TweetAppUser or Needed Exception]
	 */
	@PostMapping("register")
	@ApiOperation(value="Register user", notes="Register a user.Return app user info on successful registration or throws Needed Exception")
	public TweetAppUser registerNewUser(@RequestBody UserRegistration userRegistration) {
		return appUserService.registerNewUser(userRegistration);
	}

	/*
	 * METHOD-POST, PURPOSE-Login User,
	 * URL-http://localhost:8090/api/v1.0/tweets/login,
	 * INPUT-[@RequestBody-AuthenticateRequest Object], OUTPUT-[AuthenticateResponse or Needed Exception]
	 * 
	 */
	@PostMapping("login")
	@ApiOperation(value="User Login",notes="User login. Returns jwt token on successful login or return Needed Exception")
	public AuthenticateResponse userLogin(@RequestBody AuthenticateRequest request) {
		return appUserService.userLogin(request);
	}

	/*
	 * METHOD-GET, PURPOSE-Get All Users,
	 * URL-http://localhost:8090/api/v1.0/tweets/users/all, INPUT-[],
	 * OUTPUT-[List<TweetAppUser>] 
	 */
	@GetMapping("users/all")
	@ApiOperation(value="Get All Users",notes="Get all users.Returns list of all users who are registered")
	public List<PublicUserDetails> getAllUsers() {
		return appUserService.getAllUsers();
	}
	
	/*
	 * METHOD-GET, PURPOSE-Search By Partial Or Full Name,
	 * URL-http://localhost:8090/api/v1.0/tweets/users/search/{username}, INPUT-[@PathVariable-username(String)],
	 * OUTPUT-[List<String> of userNames]
	 */
    @GetMapping("users/search/{username}")
    @ApiOperation(value="Search User By Partial Or Full User Name",notes="Search user by their partial or full name. Returns list of app users whose username are alike")
	public List<PublicUserDetails> searchByPartialOrFullNameUser(@PathVariable String username) {
    	if(username.equals("")) {
    		return getAllUsers();
    	}
		return appUserService.searchBypartialOrFullName(username);
	}
	
	/*
	 * METHOD-POST, PURPOSE-Updating Forget Password,
	 * URL-http://localhost:8090/api/v1.0/tweets/{username}/forget, INPUT-[@PathVariable-username(String),@RequesParam-newPassword(String)],
	 * OUTPUT-[updated successfully message]
	 */

	@PostMapping("{username}/forget")
	@ApiOperation(value="Forget Password",notes="Used to reset password.Returns password updated succesfully message")
	public String forgetPassword(@PathVariable String username, @RequestParam String newPassword) {
		return appUserService.forgetPassword(username, newPassword);
	}
	
	/*
	 * METHOD-GET, PURPOSE-Get User By UserName,
	 * URL-http://localhost:8090/api/v1.0/tweets/user/{username}, INPUT-[@PathVariable-username(String)],
	 * OUTPUT-[ User Details of that UserName]
	 */
	
	@GetMapping("user/{username}")
	@ApiOperation(value="Get User By UserName",notes="Used to retrieve details of user by their username")
	public PublicUserDetails getUserByUsername(@PathVariable String username) {
		return appUserService.getUserByUsername(username);
	}
	

}
