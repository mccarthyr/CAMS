package com.fireduptech.cams.repository;

import org.springframework.data.repository.Repository;

import com.fireduptech.cams.domain.User;


public interface UserRepository extends Repository<User, Integer> {

	User save( User entity );

	User findOne( Integer id );

}