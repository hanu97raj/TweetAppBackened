package com.tweet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.tweet.error.response.ui.ErrorMessage;

@ControllerAdvice
public class AppExceptionHandler {

	@ExceptionHandler(value= {NeededException.class})
	public ResponseEntity<Object> handleNeededException(NeededException ex,WebRequest request){
		
		String errorMessageDescription=ex.getLocalizedMessage();
		if(errorMessageDescription==null) {
			errorMessageDescription=ex.toString();
		}
		ErrorMessage errorMessage=new ErrorMessage(errorMessageDescription);
		return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
		
		
	}
}
