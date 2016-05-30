package com.goeuro;



import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * Created by kemaleser on 25.05.2016.
 * This project is for Job Application to GoEuro
 * I used maven assembly plugin to create jar file
 */
public class GoEuroTest {

    public static void main(String[] args) {

        //Check the number of arguments
        if(args.length != 1) {
            System.out.println("Invalid number of arguments");
        }

        else {

            URL url;
            String cityName = args[0];
            String jsonResponse;
            HttpURLConnection urlConnection;
            String csv;

            try {
                // Open connection to API url, used HttpURLConnection to check response code
                url = new URL("http://api.goeuro.com/api/v2/position/suggest/en/" + cityName);
                urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode() != 200) {
                    System.out.println(urlConnection.getResponseMessage() + " Response Code: "
                            + urlConnection.getResponseCode());
                }
                else {
                    //Used json library for json parsing
                    jsonResponse = convertStreamToString(urlConnection.getInputStream());
                    //check the response
                    if(jsonResponse == null || jsonResponse.equals("[]")) {
                        System.out.println("No Information about this city");
                    }
                    else {
                        JSONArray jsonArray = new JSONArray(jsonResponse);
                        JSONArray jsonArrayForCSV = parseJsonArray(jsonArray);

                        //Convert to csv and write it to file
                        csv = CDL.toString(jsonArrayForCSV);
                        File file = new File("GoEuroTestOutput.csv");
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(csv);
                        bufferedWriter.close();
                        System.out.println("Conversion to CSV is successful!");
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Method found on web for converting InputStream to String */
    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /*Function for parsing original JSON array and converting it to the desired json */
    public static JSONArray parseJsonArray (JSONArray jsonArray) {
        JSONArray jsonArrayForCSV = new JSONArray();

        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject jsonObjectForCSV = new JSONObject();
            jsonObjectForCSV.put("_id", jsonObject.getInt("_id"));
            jsonObjectForCSV.put("name", jsonObject.getString("name"));
            jsonObjectForCSV.put("type", jsonObject.getString("type"));
            jsonObjectForCSV.put("latitude", jsonObject.getJSONObject("geo_position").getDouble("latitude"));
            jsonObjectForCSV.put("longitude", jsonObject.getJSONObject("geo_position").getDouble("longitude"));
            jsonArrayForCSV.put(jsonObjectForCSV);

        }
        return jsonArrayForCSV;
    }


}
