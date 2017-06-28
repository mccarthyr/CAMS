package com.fireduptech.cams.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.HttpResponse;

import java.time.*;
import java.time.format.DateTimeFormatter;


public class HttpClientService {


  /*
    STRAVA MANUAL ADD ENTRY
    POST https://www.strava.com/api/v3/activities
    $ curl -X POST https://www.strava.com/api/v3/activities \
        -H "Authorization: Bearer 83ebeabdec09f6670863766f792ead24d61fe3f9" \
        -d name="Most Epic Ride EVER!!!" \
        -d elapsed_time=18373 \
        -d distance=1557840
        -d start_date_local="2013-10-23T10:02:13Z" \
        -d type="Ride"
    */
    public String httpPostManualBikeRide( String apiRequest, String authenticationToken ) throws IOException {

      String name = "CAMS Demo Ride";
      int elapsedTime = 18000;
      float distance = 100000f;
      String type = "Ride";

      LocalDateTime startLocalDate = LocalDateTime.now();
      String ISOStartLocalDate = startLocalDate.format( DateTimeFormatter.ISO_LOCAL_DATE_TIME );


      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpPost postRequest = new HttpPost( apiRequest );

      List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
      urlParameters.add( new BasicNameValuePair( "name", name ) );
      urlParameters.add( new BasicNameValuePair( "elapsed_time", Integer.toString( elapsedTime ) ) );
      urlParameters.add( new BasicNameValuePair( "distance", Float.toString( distance ) ) );
      urlParameters.add( new BasicNameValuePair( "start_date_local", ISOStartLocalDate ) );
      urlParameters.add( new BasicNameValuePair( "type", type ) );


      //String authToken = "55721546d6124474331d9e36adfd292cfe3375a5";
      postRequest.addHeader( "Authorization", "Bearer " + authenticationToken );
      //postRequest.addHeader( "Authorization", "Bearer " + authToken );

      // {"message":"Authorization Error","errors":[{"resource":"AccessToken","field":"write_permission","code":"missing"}]}

//*** IT IS MISSING WRITE PERMISSION WHICH IS SET USING 'SCOPE' BY SENDING AUTHORIZE URI :  https://www.strava.com/oauth/authorize

// NEW URL TO SEND MANUALLY TO STRAVA TO GET AUTHORISATION CODE WITH WRITE PERMISSION SET TO CAN UPLOAD STUFF...
//https://www.strava.com/oauth/authorize?client_id=11511&response_type=code&scope=write&redirect_uri=http://strava.com/dashboard&approval_prompt=force

      //System.out.println("===> THE ACCESS TOKEN IS: " + authenticationToken);


      postRequest.setEntity( new UrlEncodedFormEntity( urlParameters ) );

      CloseableHttpResponse response = httpClient.execute( postRequest );

      System.out.println( "Response Code: " + response.getStatusLine().getStatusCode() );

      StringBuilder result = new StringBuilder();
      try {

        BufferedReader rd = new BufferedReader(
            new InputStreamReader( response.getEntity().getContent() ));

        String line = "";

        while ( ( line = rd.readLine() ) != null ) {
          result.append( line );
        }

        System.out.println( "The returned response is: \n" + result );

      } finally {
        response.close();
      }

      return result.toString();
    }






	public String httpPost( String tokenUrl, int clientId, String clientSecret, String authorisationCode  ) throws IOException {

      CloseableHttpClient httpclient = HttpClients.createDefault();
      HttpPost postRequest = new HttpPost( tokenUrl );

      List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
      urlParameters.add( new BasicNameValuePair( "client_id", Integer.toString( clientId ) ) );
      urlParameters.add( new BasicNameValuePair( "client_secret", clientSecret ) );
      urlParameters.add( new BasicNameValuePair( "code", authorisationCode ) );

      postRequest.setEntity( new UrlEncodedFormEntity( urlParameters ) );

      CloseableHttpResponse response = httpclient.execute( postRequest );


			System.out.println( "Response Code: "  + response.getStatusLine().getStatusCode() );


      StringBuffer result = new StringBuffer();
      try {

          BufferedReader rd = new BufferedReader(
              new InputStreamReader( response.getEntity().getContent() ));

          String line = "";
          
          while ( ( line = rd.readLine() ) != null ) {
              result.append(line);
          }

          System.out.println( "The returned response is: \n" + result );


      } finally {
          response.close();
      }

      return result.toString();
	}



  // ****** --- API ACCESS --- ******

  // GET https://www.strava.com/api/v3/athletes/:id/stats
  /*
  $ curl -G https://www.strava.com/api/v3/athletes/227615/stats \
          -H "Authorization: Bearer 83ebeabdec09f6670863766f792ead24d61fe3f9"
  id:     integer required
      must match the authenticated athlete
  */
  // apiRequest = "https://www.strava.com/api/v3/athletes/" + athleteId +"/stats";
	public String httpGet( String apiRequest, String authorisationCode ) throws IOException {


				CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet request = new HttpGet( apiRequest );


        request.addHeader( "Authorization", "Bearer " + authorisationCode );

        CloseableHttpResponse response = httpclient.execute( request );

        System.out.println( "The Response Code is: " + response.getStatusLine().getStatusCode() );


        StringBuilder result = new StringBuilder();
        try {

            BufferedReader rd = new BufferedReader(
                new InputStreamReader( response.getEntity().getContent() ));

            String line = "";
            
            while ( ( line = rd.readLine() ) != null ) {
                result.append(line);
            }

            System.out.println( "The returned response is: \n" + result );

        } finally {
            response.close();
        }

        return result.toString();
	}




}





