package com.fireduptech.cams.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fireduptech.cams.domain.User;
import com.fireduptech.cams.repository.UserRepository;


@Service( value="userService" )
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public int createUser( User user ) throws Exception {
		return userRepository.save( user ).getUserId();
	}

	@Override
	public User getUser( int userId ) {
		return userRepository.findOne( userId );
	}

}
