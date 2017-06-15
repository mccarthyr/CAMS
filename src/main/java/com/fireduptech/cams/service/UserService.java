package com.fireduptech.cams.service;

import java.util.List;

import com.fireduptech.cams.domain.User;


public interface UserService {

	int createUser( User user ) throws Exception;

	User getUser( int userId );

}