package com.fireduptech.cams;

import java.awt.Desktop;

import java.net.URI;
import java.net.URISyntaxException;

import java.io.Console;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/*
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
*/


import org.springframework.core.io.ClassPathResource;

import org.springframework.beans.factory.annotation.Autowired;


import java.io.InputStream;

import java.util.Properties;


import com.fireduptech.cams.Constants;

//import com.fireduptech.cams.service.AppPropertiesInitialisationService;
import com.fireduptech.cams.service.AppPropertiesChecker;
import com.fireduptech.cams.service.AuthenticateClientService;

import com.fireduptech.cams.domain.StravaAthlete;


import com.fireduptech.cams.domain.User;
import com.fireduptech.cams.dao.JDBCTemplateUserDaoImpl;
import com.fireduptech.cams.dao.JDBCTemplateUserDao;

import com.fireduptech.cams.repository.UserRepository;
import com.fireduptech.cams.service.UserService;



/**
 *  ---> APPLICATION PROTOTYPE FEATURE TO-DO ITEMS <---
 *
 *  @TODO: Go through Interface Design options once find common ground between classes and as design progresses
 */


/**
 * Entry point class into CAMS.
 * This is just a Demo learning application for
 * accessing and reporting on data from STRAVA via
 * their REST API.
 */
public class App 
{

	private static String stravaRequestMenuOption = null;
	private static String stravaRequestAPIEndpoint = null;
	private static boolean requestURIVariableSubstitution = false;


    @Autowired
    private UserRepository userRepository;

    public static void main( String[] args ) throws IOException, Exception
    {

        /**
         * Call to application Initialiser Service here which 
         * checks the setting of the Properties file.
         * @NOTE - For now there are dummy values set in Properties
         *         file to get the reading and writing to it in place.
         */
        /*
        put the property names in the Constants file - not the values but the names...
        create a service which will be both a reader and a writer to this file...
        maybe the reader will be re-used later by the application when loading Properties 
        or else the properties will just be loaded from here...
        */


        ApplicationContext context = new ClassPathXmlApplicationContext(
            "classpath:META-INF/spring/applicationContext.xml");

        //AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
          //  "classpath:META-INF/spring/applicationContext.xml");


        //ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
          //  "classpath:META-INF/spring/applicationContext.xml");

        
        // *** TEMP SECTION TESTING OUT NEW JDBC-TEMPLATE FUNCTIONALITY ***
        
        // JDBCTemplateUserDao jdbcTemplate = context.getBean(JDBCTemplateUserDao.class);  - You can call it this way without needing a cast
        JDBCTemplateUserDao jdbcTemplate = (JDBCTemplateUserDao) context.getBean("jdbcTemplateUserDao");
        
        User user =  jdbcTemplate.findUserById( 1 );

        System.out.println( user );

        System.out.format( "%n%n" );

        
        System.out.println("Adding a new User to the database...%n%n");

        User newUser = new User();
        newUser.setEmail( "richard@newuser.com" );
        newUser.setToken( "1234abcdefg" );

        int newUserId = jdbcTemplate.insertUser( newUser );
        
        System.out.println("The New User ID is: " + newUserId);        
        System.out.format( "%n%n" );
        

        // @TODO - FOR LATER ON COULD LOOK TO USE TRANSACTION MANAGEMENT BUT THIS IS NOT NECESSARY IN THE FIRST VERSION PROTOTYPE...


        // JPA Example 1 - Get an Existing User  ->@@@ NOTE @@@<- Normally would call the REPOSITORY through A SERVICE class...

        //User existingUser = userRepository.findOne( 2 );
        UserService userService = (UserService)context.getBean("userService");
        User existingUser = userService.getUser( 2 );

        System.out.println( "---> The JPA retrieved Existing User is: " );
        System.out.println( existingUser );
        System.out.format( "%n%n" );


        // JPA Example 2 - Insert a New User
        User newJpaUser = new User();
        newJpaUser.setEmail( "richard@hotmail.com" );
        newJpaUser.setToken( "abcdefg987654321" );

        int newJpaUserId = userService.createUser( newJpaUser );
        System.out.println("---> The New JPA User ID is: " + newJpaUserId);
        System.out.format( "%n%n" );


        System.exit(0);


        //AppPropertiesInitialisationService appProp = (AppPropertiesInitialisationService) context.getBean("appPropsInitService");

        AppPropertiesChecker appPropChecker = (AppPropertiesChecker) context.getBean("appPropertiesChecker");

        boolean result = appPropChecker.checkAppPropertiesAreSet(); // CHANGE TO CHECK DATABASE FOR TOKEN...

        System.out.format( "%n%n" );
        System.out.println("The Checker Result is: " + result);
        System.out.format( "%n%n" );



        

        // The Properties Checker Failed - This means need to Re-Authenticate and then reset them...
        if ( !result ) {  // ***-- THIS COULD ALSO BE THE RESULT FROM CHECKING IF AN OPTIONAL PROPERTY TYPE WAS SET...

            /*
            *** NOTE *** WILL BE FORCING THIS ROUTE ENTRY FOR THE PROTOTYPE AS WANT THE USER TO AUTHENTICATE...
            EITHER WILL BE A DATABASE CHECK INITIALY OR ELSE FOR PROTOTYPE THE USER HAS TO ALWAYS AUTHENTICATE...
            */

            
            String stravaAuthorisationCode = retrieveStravaAuthorisationCode();

            System.out.format( "%n%n" );
            System.out.println( "The return authorisation code is: " + stravaAuthorisationCode );
            System.out.format( "%n%n" );


            AuthenticateClientService acs = (AuthenticateClientService) context.getBean("authenticateClientService");

            boolean authenticated = acs.authenticateClient( stravaAuthorisationCode );
            
            //boolean authenticated = true;  // *** TEMP CODE ***
            if ( authenticated ) {

                System.out.println( "You have been AUTHENTICATED..." );
                System.out.format( "%n%n" );
                //System.exit(0);


                // Provide Application Menu Options now...FOR NOW KEEP THESE ARE THE ORIGINAL MENU OPTIONS ???
                displayApplicationFeatureOptions();

                boolean dataRetrievedFromStrava = acs.requestDataFromStravaAPI( stravaRequestMenuOption, stravaRequestAPIEndpoint, requestURIVariableSubstitution );

                if ( dataRetrievedFromStrava ) {

                    // ACCESS A SPECIFIC METHOD IN ACS TO GET THE DATA - PREDEFINED FORMAT...

                    System.out.format( "%n%n" );
                    System.out.println( "The data has been retrieved from STRAVA" );
                    System.out.format( "%n%n" );

                    StravaAthlete athleteSummary = acs.getAthleteSummary();
                    System.out.println( athleteSummary ); // The StravaAthlete class implements a custom toString()



                } else {
                    System.out.format( "%n%n" );
                    System.out.println( "NO data retrieved from STRAVA" );
                    System.out.format( "%n%n" );
                }

            
                // *** NOTE - THE CURRENT MENU OPTIONS ARE ALL ---GET--- REQUESTS : FOR PROTOTYPE TYPE WILL BE SIMPLE [GET] REQUESTS SO DONT NEED TO TRACK THAT YET...

                System.exit(0);

                // NOTE: Currently it will end within that method as it just prints out the chosen method...

            } else {

                // Either ask user to Re-Authenticate or exit the application...FOR PROTOTYPE V1 JUST EXIT THE APPLICATION

                System.out.println( "You have NOT BEEN AUTHENTICATED..." );
                System.exit(0);

            }
            


        }


//context.registerShutdownHook();




        System.exit(0);


        //System.out.println( "Hello World updated!" );

		System.out.format( "%n%n" );
        System.out.println( "-----------------------------------------------------" );
        System.out.println( "Welcome to the Coach Athlete Monitoring System (CAMS)" );
        System.out.println( "-----------------------------------------------------" );
		System.out.format( "%n%n" );


    	// Method call to provde the user with the current application options
    	displayApplicationFeatureOptions();

        System.out.format( "%n%n" );



    	//System.exit(0);

    }	// End of the static void main() method...





    private static String retrieveStravaAuthorisationCode() {

        /**
         * ------ Authorisation Code ------
         * Firstly provide the user with a URL to put in to the browser to get the returned \
         * authorisation code which will be copied into this app then
         */

        Console c = System.console();
        if ( c == null ) {
            System.err.println("There is no console available on this operating system.");
            System.exit(1);
        }

        System.out.println( "In order to gain access to your Strava details, CAMS requires you to grant access to it via Strava." );
        System.out.println( "Please copy the following Strava Authorisation URL into your web browser." );
        System.out.println( "It will require you to log into your Strava account and then it will redirect to the Authorsation page.");
        System.out.println( "The Authorisation code will appear in the URL after the &code= section in the URL." ); 
        System.out.println( "Please copy that code and provide it into command prompt that follows." );

        System.out.println( "https://www.strava.com/oauth/authorize?client_id=11430&response_type=code&redirect_uri=http://strava.com/dashboard&approval_prompt=force" );

        String authorisationCode = c.readLine( "Please enter the Authorisation Code from the browser URL that Strava provided:" );

        //*** NOTE **** - HARDCODED IN THE CODE RETURNED FROM STRAVA - 546e1762ab07cc14532af26485903f6f5cfc6653
        authorisationCode = "546e1762ab07cc14532af26485903f6f5cfc6653";

        System.out.println( "The code you entered is: " + authorisationCode );

        return authorisationCode;

    }   // End of method retrieveStravaAuthorisationCode()...




    /*
     1. Get current Athlete details.

     2. Get heart rate zones.

     3. Get Athlete Totals & Stats.

     4. List all activities - from this get a summation of the Activity Ids & Activity Names along with a full count.

     5. Get a specific Activity and show some stats from it.
    */
    private static void displayApplicationFeatureOptions() {

    	Console console = System.console();
    	if ( console == null ) {
    		System.err.println("There is no console available on this operating system.");
    		System.exit(1);
    	}

       	System.out.println( "Please enter the number from one of the following options:" );
        System.out.println( "----------------------------------------------------------" );

        stravaRequestMenuOption = console.readLine( "1 - Athlete Summary %n2 - Athlete Heart Rate Zones %n3 - Athlete Totals%n" );

        System.out.format( "You chose option %s%n", stravaRequestMenuOption );

        switch ( stravaRequestMenuOption ) {
            case Constants.ATHLETE_SUMMARY_MENU_OPTION:
                System.out.println( Constants.ATHLETE_SUMMARY_API_ENDPOINT );
                stravaRequestAPIEndpoint = Constants.ATHLETE_SUMMARY_API_ENDPOINT;
                break;

            case Constants.ATHLETE_HEART_RATE_ZONES_MENU_OPTION:
                System.out.println( Constants.ATHLETE_HEART_RATE_ZONES_API_ENDPOINT );
                stravaRequestAPIEndpoint = Constants.ATHLETE_HEART_RATE_ZONES_API_ENDPOINT;
                break;

            case Constants.ATHLETE_TOTALS_MENU_OPTION:
                System.out.println( Constants.ATHLETE_TOTALS_API_ENDPOINT );
                stravaRequestAPIEndpoint = Constants.ATHLETE_TOTALS_API_ENDPOINT;

                requestURIVariableSubstitution = true;  

                break;
            default:
                System.out.println( "Please enter a value from the menu options" );   // ??? REPLACES THIS WITH A THROW EXCEPTION ???
                break;
        } 


    }	// End of method displayApplicationFeatureOptions()...



}	// End of the App class...

