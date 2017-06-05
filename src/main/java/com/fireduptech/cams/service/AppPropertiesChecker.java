package com.fireduptech.cams.service;


import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import java.util.Properties;

import com.fireduptech.cams.Constants;


// @TODO - THIS WILL CHANGE TO CHECKING THE DATABASE FOR THE TOKEN FROM USERS ACCOUNT IN LATER VERSION...
public class AppPropertiesChecker {


    private String authenticationToken;
    private String athleteId;


    public AppPropertiesChecker ( String configFile ) throws Exception {

    	ClassPathResource configProperties = new ClassPathResource( configFile );

    	// Reading the Properties from the config

    	if ( configProperties.exists() ) {

    		InputStream inputStream = configProperties.getInputStream();
    		Properties properties = new Properties();
    		properties.load( inputStream );

    		// Check if the properties exist

    		this.authenticationToken = properties.getProperty( Constants.STRAVA_AUTHENTICATION_TOKEN_NAME );
    		this.athleteId = properties.getProperty( Constants.STRAVA_ATHLETE_IDENTIFIER_NAME );

            System.out.println("After getting the properties...");

    	} else {
            // @TODO - *** THE PROPERTY FILE DOES NOT EXIST SO RETURN ERROR AS THIS WILL STOP THE APPLICATION ***
        }
    }




    // @TODO - THIS WILL CHANGE TO CHECKING THE DATABASE FOR THE TOKEN FROM USERS ACCOUNT IN LATER VERSION...
    public boolean checkAppPropertiesAreSet() {

        boolean result = false;        

        if ( this.authenticationToken != null ) {
            System.out.format( "%n%n" );
            System.out.println( "SUCCESS: The configuration Token property is set..." );
            System.out.println( authenticationToken );
            System.out.format( "%n%n" );

            result = true;
        } else {
            System.out.format( "%n%n" );
            System.out.println( "FAILURE: The configuration Token property is NOT set..." );
            System.out.format( "%n%n" );

            return result;
        }

        if ( this.athleteId != null && !athleteId.isEmpty() ) {
            System.out.format( "%n%n" );
            System.out.println( "SUCCESS: The configuration AthleteId property is set..." );
            System.out.println( athleteId );
            System.out.format( "%n%n" );

            result = true;
        } else {
            System.out.format( "%n%n" );
            System.out.println( "FAILURE: The configuration AthleteId property is NOT set..." );
            System.out.format( "%n%n" );

            result = false;
            return result;
        }

        return result;

    }   // End of method checkAppPropertiesAreSet()...
    


}