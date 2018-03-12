package com.mobile.captainmoha.thoughts.Network;

/**
 * Created by captainmoha on 3/22/18.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpRest{

    private HttpURLConnection httpURLConnection;
    JSONObject jsonObject;
    BufferedReader reader;


    public String[] sendGet(String requestURL) throws IOException {
        URL url = new URL(requestURL);

        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true); // true if we want to read server's response
        httpURLConnection.setDoOutput(false); // false indicates this is a GET request

        String[] response = readRespone();

        httpURLConnection.disconnect();

        return response;
    }

    public JSONObject JObject_Get (String URL) {

        URL url;
        JSONObject jsonObject = null;

        try {
            url = new URL(URL);
            StringBuilder sb = new StringBuilder();
            httpURLConnection = (HttpURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {

                sb.append(line+"\n");

            }
            jsonObject= new JSONObject(sb.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }
    public JSONArray JArray_Get (String URL) {
        URL url;
        JSONArray jsonArray = null;

        try {
            url = new URL(URL);

            StringBuilder sb = new StringBuilder();
            httpURLConnection = (HttpURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {

                sb.append(line+"\n");
            }
            jsonArray= new JSONArray(sb.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;

    }

    public void GET (String Url) {
        URL url;
        try {
            url = new URL(Url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            int responsecode = httpURLConnection.getResponseCode();
            if(responsecode == HttpURLConnection.HTTP_OK) {
                System.out.println("-----REQUEST SENT");
            }
            else {
                System.out.println("-----REQUEST FAILED");

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String[] sendPost(String requestURL,Map<String, String> params) throws IOException {

        URL url = new URL(requestURL);

        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true); // true indicates the server returns response

        StringBuffer requestParams = new StringBuffer();

        if (params != null && params.size() > 0) {

            httpURLConnection.setDoOutput(true); // true indicates POST request

            // creates the params string, encode them using URLEncoder
            Iterator<String> paramIterator = params.keySet().iterator();
            while (paramIterator.hasNext()) {
                String key = paramIterator.next();
                String value = params.get(key);
                requestParams.append(URLEncoder.encode(key, "UTF-8"));
                requestParams.append("=").append(
                        URLEncoder.encode(value, "UTF-8"));
                requestParams.append("&");
            }

            // sends POST data
            OutputStreamWriter writer = new OutputStreamWriter(
                    httpURLConnection.getOutputStream());
            writer.write(requestParams.toString());
            writer.flush();
        }

        String[] response = readRespone();

        httpURLConnection.disconnect();

        return response;
    }

    public JSONObject Post(String req_Url, String parameters) throws IOException {
        URL url= new URL(req_Url);
        httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");

        // parameters = "?from=2016-08-08&to=2016-08-10";
        httpURLConnection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

        wr.writeBytes(parameters);
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        try {
            jsonObject= new JSONObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String[] readRespone() throws IOException {

        InputStream inputStream;

        if (httpURLConnection != null) {
            inputStream = httpURLConnection.getInputStream();
        } else {
            throw new IOException("Connection is not established.");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> response = new ArrayList<>();

        String line = "";

        while ((line = reader.readLine()) != null) {
            response.add(line);
        }
        reader.close();

        return response.toArray(new String[0]);
    }
}
