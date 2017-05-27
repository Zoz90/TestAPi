package com.example.zoz.testapi;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServiceHandler {

    static String ip = "http://174.138.54.52";
    static String response = null;
    static String token;
    public final static int GET = 1;
    public final static int POST = 2;


    public String serverColl (String url,int metod){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            if (metod == POST) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.addHeader("Authorization", "Token "+ token);
                httpResponse = httpClient.execute(httpPost);
                httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
                Log.d("Response: ", "> " + response);
            } else if (metod == GET) {

                URL getUrl = new URL(String.format(""+url));
                HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Token "+ token);
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer responsed = new StringBuffer();
                while ((inputLine = in.readLine()) != null)
                    responsed.append(inputLine);
                in.close();
                response = responsed.toString();
                return responsed.toString();

            }

        }catch (UnsupportedEncodingException e) {
            Log.e("ServiceHandler", e.getMessage());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e("ServiceHandler", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ServiceHandler", e.getMessage());
        }
        return response;
    }

    public String serverColl (String url, int metod, String name, String password)
    {
        return this.serverColl(url,  metod,  name,  password, null);
    }

    public String serverColl (String url, int metod, String name, String password, String email){

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            if (metod == POST) {
                HttpPost httpPost = new HttpPost(url);
                if(email==null) {
                    StringEntity input = new StringEntity("{\"username\":\"" + name + "\",\"password\":\"" + password + "\",\"email\":\"" + " " + "\"}", "UTF-8");
                    input.setContentType("application/json");
                    httpPost.setEntity(input);
                    httpResponse = httpClient.execute(httpPost);
                    httpEntity = httpResponse.getEntity();
                    response = EntityUtils.toString(httpEntity);
                    Log.d("Response: ", "> " + response);
                }
                else {
                    StringEntity input = new StringEntity("{\"username\":\"" + name + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\"}", "UTF-8");
                    input.setContentType("application/json");
                    httpPost.setEntity(input);
                    httpResponse = httpClient.execute(httpPost);
                    httpEntity = httpResponse.getEntity();
                    response = EntityUtils.toString(httpEntity);
                    Log.d("Response: ", "> " + response);
                }
            }
            else if (metod == GET) {
                HttpGet httpGet = new HttpGet(url);
                httpGet.addHeader("Authorization", "Token "+ token);
                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
                Log.d("Response: ", "> " + response);
            }
        }catch (UnsupportedEncodingException e) {
            Log.e("ServiceHandler", e.getMessage());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e("ServiceHandler", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ServiceHandler", e.getMessage());
        }
        return response;
    }
}

