package com.tweet.jwt.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tweet.jwt.services.JwtUtilToken;
import com.tweet.jwt.services.MyUserDetailsService;
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
    private JwtUtilToken jwtUtilToken;
	
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
	final String authorizationHeader=request.getHeader("Authorization");
	
	String userName=null;
	String jwt=null;
	
	if(authorizationHeader!=null&&authorizationHeader.startsWith("Bearer ")) {
		jwt=authorizationHeader.substring(7);
		userName=jwtUtilToken.extractUserName(jwt);
	}
	
	if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
		UserDetails userDetails=myUserDetailsService.loadUserByUsername(userName);
		if(jwtUtilToken.validateToken(jwt, userDetails)) {
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
					new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
		
		usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	}
	}
	filterChain.doFilter(request, response);
	
}
}
