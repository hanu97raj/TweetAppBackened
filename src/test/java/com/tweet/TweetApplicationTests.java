package com.tweet;

import org.junit.jupiter.api.Test;
import  static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tweet.entities.TweetAppUser;
import com.tweet.repositories.IUserRepository;



@SpringBootTest(classes=TweetApplicationTests.class)
class TweetApplicationTests {

	@Test
	void contextLoads() {
	assertThat(1).isEqualTo(1);
	}
	
	

}

