package com.fireduptech.cams.service;


import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


import java.io.IOException;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.BufferedReader;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.HttpResponse;

import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;


import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import com.fireduptech.cams.service.HttpClientService;

import com.fireduptech.cams.domain.StravaAuthenticationResponse;

import com.fireduptech.cams.domain.StravaAthlete;

import com.fireduptech.cams.Constants;


public class AuthenticateClientService {

    private HttpClientService httpClientService;

    //private static final int clientId = 11430;
    //private static final String clientSecret = "0dcc40d724e4ec25f0417b290a3410bf1b1d09fa";

    // FiredUpTech Account App ID
    private static final int clientId = 11511;
    private static final String clientSecret = "5642c456cc8feccf9026a27ed860273eb5fa4ade";


    private static final String tokenUrl = "https://www.strava.com/oauth/token";


    private int athleteId = 0;  // Explicit default zero initialisation

    private int activityId = 0;

    private static String authenticationToken = null;

    private boolean authenticated = false;

    private StravaAthlete stravaAthlete = null;


    public void setHttpClientService( HttpClientService httpClientService ) {
        this.httpClientService = httpClientService;
    }


    public int getAthleteId() {
        return this.athleteId;
    }


    public void setAthleteId( int athleteId ) {
        this.athleteId = athleteId;
    }


    /**
     * Sends a request with the authorisation code to get a response.
     * From the response the Token and AthleteId are extracted and set.
     */
    public boolean authenticateClient( String authorisationCode )  throws IOException {

        String response = this.httpClientService.httpPost( tokenUrl, clientId, clientSecret, authorisationCode );

System.out.println("THE RESPONSE IS: -> " + response);
//YOU CAN GET A RESPONSE LIKE THIS WHEN I SEND THE WRONG CODE OVER FOR THE WRONG CLIENT ID:
//{"message":"Bad Request","errors":[{"resource":"RequestToken","field":"code","code":"invalid"}]}

        if ( response != null && !response.trim().isEmpty() ) {
            extractAuthenticationTokenAndAthleteId( response );
        }

        // Check that the values have been set
        if ( this.athleteId > 0 && authenticationToken != null ) {
            authenticated = true;
        }

        return authenticated;

    }   // End of method authenticateClient()...


    /*
    {"access_token":"05463ab3dd1be0c435f8f32bbb6037d3fecc246e","token_type":"Bearer",
    "athlete":{"id":7209970,"username":"richardmccarthy","resource_state":3,"firstname":"Richard","lastname":"McCarthy",
    "profile_medium":"https://dgalywyr863hv.cloudfront.net/pictures/athletes/7209970/3988522/2/medium.jpg",
    "profile":"https://dgalywyr863hv.cloudfront.net/pictures/athletes/7209970/3988522/2/large.jpg","city":"Tralee","state":"Kerry",
    "country":"Ireland","sex":"M","premium":false,"created_at":"2014-12-01T16:19:51Z","updated_at":"2016-05-18T14:46:20Z",
    "badge_type_id":0,"friend":null,"follower":null,"follower_count":7,"friend_count":9,"mutual_friend_count":0,"athlete_type":0,
    "date_preference":"%m/%d/%Y","measurement_preference":"meters","email":"mccarthy.richard@gmail.com","ftp":null,"weight":83.2,
    "clubs":[],"bikes":[{"id":"b2696123","primary":true,"name":"Prestigio","resource_state":2,"distance":1229785.0}],"shoes":[]}}
    */
    private void extractAuthenticationTokenAndAthleteId( String response ) {

        Gson gson = new Gson();

        StravaAuthenticationResponse stravaAuthenticationResponse = new StravaAuthenticationResponse();
        stravaAuthenticationResponse = gson.fromJson( response, StravaAuthenticationResponse.class );

        this.athleteId = stravaAuthenticationResponse.getStravaAthlete().getId();
        this.authenticationToken = stravaAuthenticationResponse.getAccessToken();

    }




    // NOTE *** Currently values hardcoded in here for demo ***
    public boolean createBikeEntryInStrava() throws IOException {

        // URI for POST addition of an activity
        String apiRequest = "https://www.strava.com/api/v3/activities";

        if ( this.authenticated ) {

            String response = this.httpClientService.httpPostManualBikeRide( apiRequest, this.authenticationToken );

            if ( response != null && !response.trim().isEmpty() ) {

                // Once response is returned true then for a POST entry that is all user needs
                return true;
            }
            // @TODO - Add in Exception management here

        }

        return false;
    }





    /**
     * @param  stravaRequestMenuOption
     * @param  stravaRequestAPIEndpoint
     * @param  requestURIVariableSubstitution  A boolean representing if substitution in the API endpoint with a variable is required.
     * 
     * NOTE    *** Currently this deals with ATHLETE API and ONLY for GET requests ***
     */
    public boolean requestDataFromStravaAPI( String stravaRequestMenuOption, String stravaRequestAPIEndpoint, boolean requestURIVariableSubstitution  ) throws IOException {

        String apiRequest = "";

        // NOTE: Currently this will only be Athlete ID Substitution...
        // SEPARATE THIS OUT TO A "PREPARE REQUEST FOR STRAVA" METHOD WHEN HAVE MULTIPLE SUBSTITUTION VARIABLE TYPES...
        if ( requestURIVariableSubstitution ) {

            String modifiedStravaRequestAPIEndpoint = String.format( stravaRequestAPIEndpoint, this.athleteId );
            apiRequest = Constants.STRAVA_API_BASE_URL + modifiedStravaRequestAPIEndpoint;

        } else {

            apiRequest = Constants.STRAVA_API_BASE_URL + stravaRequestAPIEndpoint;
        }

        if ( this.authenticated ) {

            String response = this.httpClientService.httpGet( apiRequest, authenticationToken );

            if ( response != null && !response.trim().isEmpty() ) {
                switch ( stravaRequestMenuOption ) {

                    case Constants.ATHLETE_SUMMARY_MENU_OPTION:
                        extractAthleteSummaryData( response );
                        break;

                    case Constants.ATHLETE_HEART_RATE_ZONES_MENU_OPTION:
                        extractAthleteHeartRateZonesData( response );
                        break;

                    case Constants.ATHLETE_TOTALS_MENU_OPTION:
                        extractAthleteTotalsData( response );
                        break;

                    default:
                        // Something gone wrong...
                        break;
                }
            }

            return true;
        } else {
            // @TODO: THROW AN EXCEPTION HERE THAT USER IS NOT AUTHENTICATED...
            return false;
        }

    }   // End of method requestDataFromStravaAPI()...



    /* --- Example of the Athelete section within the JSON response...
    "athlete":{
        "id":7209970,"username":"richardmccarthy","resource_state":3,"firstname":"Richard","lastname":"McCarthy",
            "profile_medium":"https://dgalywyr863hv.cloudfront.net/pictures/athletes/7209970/3988522/2/medium.jpg",
            "profile":"https://dgalywyr863hv.cloudfront.net/pictures/athletes/7209970/3988522/2/large.jpg","city":"Tralee","state":"Kerry",
            "country":"Ireland","sex":"M","premium":false,"created_at":"2014-12-01T16:19:51Z","updated_at":"2016-05-18T14:46:20Z",
            "badge_type_id":0,"friend":null,"follower":null,"follower_count":7,"friend_count":9,"mutual_friend_count":0,"athlete_type":0,
            "date_preference":"%m/%d/%Y","measurement_preference":"meters","email":"mccarthy.richard@gmail.com","ftp":null,"weight":83.2,
            "clubs":[],"bikes":[{"id":"b2696123","primary":true,"name":"Prestigio","resource_state":2,"distance":1229785.0}],"shoes":[]
    }
    */
    // Summary Data to return: username, firstname, lastname, weight, city, country, sex, premium, email
    private void extractAthleteSummaryData( String response ) {

        Gson gson = new Gson();
        this.stravaAthlete = new StravaAthlete();
        this.stravaAthlete = gson.fromJson( response, StravaAthlete.class );

    }   // End of method extractAthleteSummaryData()...


    // StravaAthlete implements toString() so printing it will provide the summary
    public StravaAthlete getAthleteSummary() {
        return this.stravaAthlete;
    }


    private void extractAthleteHeartRateZonesData( String response ) {

    }   // End of method extractAthleteHeartRateZonesData()...



    private void extractAthleteTotalsData( String response ) {

    }   // End of method extractAthleteTotalsData()...



}   // End of public class AuthenticateClientService{}...



/*
==============================================================================
        THE SUBSTITUTION NEEDS TO BE CHECKED FIRST AS THIS NEEDS TO BE DONE BEFORE ANY REQUEST IS SENT TO STRAVA...
 
        String apiRequest = Constants.STRAVA_API_BASE_URL + stravaRequestAPIEndpoint;

        if ( requestURIVariableSubstitution ) {

            // Check what variable needs to be substituted
            USE THE MENU OPTION PASS IN FOR THIS ... PERHAPS THE MAP IS DONE IN THE CONSTRUCTOR AT THE START
            OR IS PASSED IN AS A UTIL SCHEMA MAP FROM THE XML ???
            => HashMap( Menu_Name, Variable_Name )

NOTE******PLAN TO CODE NEXT: BEFORE INJECTING THE MAP IN VIA XML CONSTRUCTOR, CREATE A LOCAL MAP HERE WITH THE VALUES TO GET 
                             THE MAP CODE DEFINITION WORKING AND USE IT HERE FIRST... ******

            THE MENU NAMES MIGHT HAVE TO BE REPLACED BY A STRING INTEGER AS THAT IS WHAT IS PASSED THROUGH 
            IN THE METHOD TO HERE . E.G. "1" OR "2" ETC...
            <util:map id="requestURLVariableSubstitutionMap" map-class="java.util.Map" >
             <entry key="1" value="athleteId"/>
             <entry key="3" value="activityId"/>
            </util:map>

            IN THE XML ALSO PUT:
            <constructor-arg name="mapType" ref="requestURLVariableSubstitutionMap" />


public static final String ATHLETE_TOTALS_MENU_OPTION               = "3";
public static final String ATHLETE_TOTALS_API_ENDPOINT              = "/api/v3/athletes/%d/stats";



------------------------

Map<Integer,String> hmap = new HashMap<Integer,String>();
hmap.put( 1, "athleteId"  );
hmap.put( 2, "activityId" );

System.out.println( hmap.get(2) );
Set set = hmap.entrySet();
Iterator iterator = set.iterator();
while( iterator.hasNext() ) {
    Map.Entry mentry = (Map.Entry)iterator.next();
    System.out.println( mentry.getValue() );
}

System.out.format( "%n%n" );
String modified = String.format( "/api/v3/athletes/%d/stats", 12 );
System.out.println( modified );

System.out.format( "%n%n" );
System.exit(0);

------------------------



        }


        if ( authenticated ) {

            
            String response = this.httpClientService.httpGet( apiRequest, authenticationToken );

            extractAndMapResponseFromStravaAPI( stravaRequestMenuOption );

            return true;
        } else {
            return false;
        }

==============================================================================





    ****** @TODO ===> need another extract method here for the ATHLETE TOTALS RETURNED ... AND ANY OTHER MENU OPTION THAT IS USED...








//****** BELOW IS ALL THE ORIGINAL ATHELETE STATS CONTROLLER CLASS CODE ******



public class AthleteStatsController {


     
    {"biggest_ride_distance":101503.0,"biggest_climb_elevation_gain":397.0,
         "recent_ride_totals":{"count":18,"distance":763052.1918945312,"moving_time":97395,"elapsed_time":107154,"elevation_gain":5690.0,"achievement_count":122},
         "recent_run_totals":{"count":0,"distance":0.0,"moving_time":0,"elapsed_time":0,"elevation_gain":0.0,"achievement_count":0},
         "recent_swim_totals":{"count":5,"distance":7375.0,"moving_time":9171,"elapsed_time":10924,"elevation_gain":0.0,"achievement_count":0},
         "ytd_ride_totals":{"count":49,"distance":1448975,"moving_time":192646,"elapsed_time":279140,"elevation_gain":10775},
         "ytd_run_totals":{"count":0,"distance":0,"moving_time":0,"elapsed_time":0,"elevation_gain":0},
         "ytd_swim_totals":{"count":5,"distance":7375,"moving_time":9171,"elapsed_time":10924,"elevation_gain":0},
         "all_ride_totals":{"count":49,"distance":1448975,"moving_time":192646,"elapsed_time":279140,"elevation_gain":10775},
         "all_run_totals":{"count":0,"distance":0,"moving_time":0,"elapsed_time":0,"elevation_gain":0},
         "all_swim_totals":{"count":5,"distance":7375,"moving_time":9171,"elapsed_time":10924,"elevation_gain":0}
     }
    
     
        @TODO - PUT IN SOME RESPONSE CODE HANDING USING SWITCH STATEMENT AND DEALING WITH 403, 200 ETC...
        Version 2  => will also have Custom Field Deserialisation to that can keep proper field naming in the mapped Java response objects...
        Version 2  => HIBERNATE: Map the extracted Java objects to a database using Hibernate...
        Version 3  => Put Java Docs in place and some basic unit tests as well as any code refactoring required
    
    public float getTotalDistanceCycled() throws IOException {


        float totalDistanceCycled = 0.0f;

        if ( authenticated ) { 


            String apiRequest = "https://www.strava.com/api/v3/athletes/" + athleteId + "/stats";

            String response = this.httpClientService.httpGet( apiRequest, authenticationToken );


            // EXTRACTING THE JSON - THIS PART TO BE SEPARATED INTO ITS OWN AREAS AND CREATE THE MAP FOR A LOOK-UP AS MENTIONED ABOVE...

            Gson gson = new Gson();
            StravaAthleteStats stravaAthleteStats = new StravaAthleteStats();
            stravaAthleteStats = gson.fromJson( response, StravaAthleteStats.class );

            //System.out.println( stravaAthleteStats.getBiggestRideDistance() / 1000 + "km" );
            totalDistanceCycled =  (float) stravaAthleteStats.getAllRideTotals().getDistance();

        } else {
            // put in ELSE condition logic here...
        }

        return totalDistanceCycled;

    }

//    ++++++ PUT IN ANOTHER PARAMETER INDICATING THAT A VARIABLE SUBSTITUTION NEEDS TO BE DONE ++++++
//        PASS THROUGH THE MENU OPTION ALSO AND USE THAT TO DECIDE WHICH GSON EXTRACTOR AND OBJECT MAPPING PROCESS TO CALL...
    public void requestDataFromStravaAPI( String stravaRequestMenuOption, String stravaRequestAPIEndpoint, boolean requestURIVariableSubstitution ) {

        THE SUBSTITUTION NEEDS TO BE CHECKED FIRST AS THIS NEEDS TO BE DONE BEFORE ANY REQUEST IS SENT TO STRAVA...
 
        String apiRequest = Constants.STRAVA_API_BASE_URL + stravaRequestAPIEndpoint;

        if ( requestURIVariableSubstitution ) {

            // Check what variable needs to be substituted
            USE THE MENU OPTION PASS IN FOR THIS ... PERHAPS THE MAP IS DONE IN THE CONSTRUCTOR AT THE START
            OR IS PASSED IN AS A UTIL SCHEMA MAP FROM THE XML ???
            => HashMap( Menu_Name, Variable_Name )

NOTE******PLAN TO CODE NEXT: BEFORE INJECTING THE MAP IN VIA XML CONSTRUCTOR, CREATE A LOCAL MAP HERE WITH THE VALUES TO GET 
                             THE MAP CODE DEFINITION WORKING AND USE IT HERE FIRST... ******

            THE MENU NAMES MIGHT HAVE TO BE REPLACED BY A STRING INTEGER AS THAT IS WHAT IS PASSED THROUGH 
            IN THE METHOD TO HERE . E.G. "1" OR "2" ETC...
            <util:map id="requestURLVariableSubstitutionMap" map-class="java.util.Map" >
             <entry key="1" value="athleteId"/>
             <entry key="3" value="activityId"/>
            </util:map>

            IN THE XML ALSO PUT:
            <constructor-arg name="mapType" ref="requestURLVariableSubstitutionMap" />


public static final String ATHLETE_TOTALS_MENU_OPTION               = "3";
public static final String ATHLETE_TOTALS_API_ENDPOINT              = "/api/v3/athletes/%d/stats";



------------------------

Map<Integer,String> hmap = new HashMap<Integer,String>();
hmap.put( 1, "athleteId"  );
hmap.put( 2, "activityId" );

System.out.println( hmap.get(2) );
Set set = hmap.entrySet();
Iterator iterator = set.iterator();
while( iterator.hasNext() ) {
    Map.Entry mentry = (Map.Entry)iterator.next();
    System.out.println( mentry.getValue() );
}

System.out.format( "%n%n" );
String modified = String.format( "/api/v3/athletes/%d/stats", 12 );
System.out.println( modified );

System.out.format( "%n%n" );
System.exit(0);

------------------------



        }


        if ( authenticated ) {

            
            String response = this.httpClientService.httpGet( apiRequest, authenticationToken );

            extractAndMapResponseFromStravaAPI( stravaRequestMenuOption );

            return true;
        } else {
            return false;
        }


    }   // End of method requestDataFromStravaAPI()...


    private void extractAndMapResponseFromStravaAPI( String stravaRequestMenuOption ) {


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

                //message = Constants.ATHLETE_TOTALS_API_ENDPOINT;
                //message = String.format( message, aId );  

                break;

            case Constants.ATHLETE_SUMMARY_ACTIVITIES_MENU_OPTION:
                System.out.println( Constants.ATHLETE_SUMMARY_ACTIVITIES_API_ENDPOINT );
                stravaRequestAPIEndpoint = Constants.ATHLETE_SUMMARY_ACTIVITIES_API_ENDPOINT;
                break;

            case Constants.ATHLETE_ACTIVITY_DETAILS_MENU_OPTION:
                System.out.println( Constants.ATHLETE_ACTIVITY_DETAILS_API_ENDPOINT );
                stravaRequestAPIEndpoint = Constants.ATHLETE_ACTIVITY_DETAILS_API_ENDPOINT;
                
                //System.out.println( String.format( Constants.ATHLETE_ACTIVITY_DETAILS_API_ENDPOINT, actId ) );
                break;

            default:
                System.out.println( "Please enter a value from the menu options" );   // ??? REPLACES THIS WITH A THROW EXCEPTION ???
                break;
        } 



    }   // End of method extractAndMapResponseFromStravaAPI()...

}
*/



