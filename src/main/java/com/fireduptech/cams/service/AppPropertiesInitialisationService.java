package com.fireduptech.cams.service;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import java.util.Properties;

import com.fireduptech.cams.Constants;

/*
    *** NOTE ***
    ------> CURRENTLY THIS CLASS IS NOT BEING USED <------
*/

public class AppPropertiesInitialisationService {  // NOTE: WILL NEED TO PUT IN THROWS EXCEPTION HERE...

	/*
	The constructor will be configured (XML) to automcatically load in the 
	properties file...here the values will all be checked.

	Can make a version of this where can manually run a check by calling a helper method
	to do the same work so farm out the logic out of the contructor quickly...
    */


    public AppPropertiesInitialisationService ( String configFile ) throws Exception {

    	ClassPathResource configProperties = new ClassPathResource( configFile );

    	// Reading the Properties from the config

    	if ( configProperties.exists() ) {

    		InputStream inputStream = configProperties.getInputStream();
    		Properties properties = new Properties();
    		properties.load( inputStream );

    		// Check if the properties exist

    		String authenticationToken = properties.getProperty( Constants.STRAVA_AUTHENTICATION_TOKEN_NAME );
    		String athleteId = properties.getProperty( Constants.STRAVA_ATHLETE_IDENTIFIER_NAME );

    		if ( authenticationToken != null ) {
                System.out.format( "%n%n" );
    			System.out.println( "SUCCESS: The configuration Token property is set..." );
                System.out.println( authenticationToken );
                System.out.format( "%n%n" );
    		} else {
                System.out.format( "%n%n" );
    			System.out.println( "FAILURE: The configuration Token property is NOT set..." );
                System.out.format( "%n%n" );
    		}

    		if ( athleteId != null && !athleteId.isEmpty() ) {
                System.out.format( "%n%n" );
    			System.out.println( "SUCCESS: The configuration AthleteId property is set..." );
                System.out.println( athleteId );
                System.out.format( "%n%n" );
    		} else {
                System.out.format( "%n%n" );
    			System.out.println( "FAILURE: The configuration AthleteId property is NOT set..." );
                System.out.format( "%n%n" );
    		}

    	}


    }





}