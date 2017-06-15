package com.fireduptech.cams.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import org.springframework.stereotype.Repository;

import com.fireduptech.cams.domain.User;


@Repository(value="jdbcTemplateUserDao")
public class JDBCTemplateUserDaoImpl implements JDBCTemplateUserDao {


	@Autowired
	private JdbcTemplate jdbcTemplate;


	public User findUserById( int id ) {

		final String sql = "SELECT * FROM users WHERE user_id = ?";

		return jdbcTemplate.queryForObject( 
			sql,
			new Object[] {id},
			new RowMapper<User>() {
				
				public User mapRow( ResultSet rs, int rowNum ) throws SQLException {
					User user = new User();
					user.setUserId( rs.getInt("user_id") );
					user.setEmail( rs.getString("email") );
					user.setToken( rs.getString("token") );
					return user;
				}

			} );

	}	// End of method findUserById()...

	
	public int insertUser( User user ) {

		final String sql = "INSERT INTO users( email, token ) VALUES ( ?, ? )";

		KeyHolder keyholder = new GeneratedKeyHolder();

		jdbcTemplate.update( new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement( sql, new String[] { "user_id" } );
				ps.setString( 1, user.getEmail() );
				ps.setString( 2, user.getToken() );
				return ps;
			}

		}, keyholder );

		return keyholder.getKey().intValue();

	}	// End of method insertUser()...


}