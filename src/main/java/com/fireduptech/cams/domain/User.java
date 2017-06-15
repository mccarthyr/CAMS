package com.fireduptech.cams.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity(name = "User")
@Table(name = "users", uniqueConstraints = {
	@UniqueConstraint( columnNames = "user_id" )
} )
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id", unique = true, nullable = false)
	private int user_id;

	@Column(name = "email", nullable = false, length = 50)
	private String email;

	@Column(name = "token", nullable = true, length = 200)
	private String token;


	public int getUserId() {
		return this.user_id;
	}

	public String getEmail() {
		return this.email;
	}

	public String getToken() {
		return this.token;
	}

	public void setUserId(int id) {
		this.user_id = id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setToken(String token) {
		this.token = token;
	}


	@Override
	public String toString() {
		return "User ID: " + this.user_id +
					" Email: " + this.email +
					" Token: " + this.token;
	}


}