package com.tweet.entities;

public class UserRegistration {

	private String firstName;

	private String lastName;

	private String email;

	private String loginId;

	private String password;

	private String confirmPassword;

	private String contactNumber;

	public UserRegistration() {
	}

	public UserRegistration(String firstName, String lastName, String email, String loginId, String password,
			String confirmPassword, String contactNumber) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.loginId = loginId;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.contactNumber = contactNumber;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
