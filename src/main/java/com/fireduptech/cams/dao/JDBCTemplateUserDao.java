package com.fireduptech.cams.dao;

import com.fireduptech.cams.domain.User;

public interface JDBCTemplateUserDao {

	User findUserById( int id );

	int insertUser( User user );

}