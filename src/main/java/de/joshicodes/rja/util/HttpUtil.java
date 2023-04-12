package de.joshicodes.rja.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class HttpUtil {

    public static JsonObject readJson(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new java.net.URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();
            InputStream is = connection.getInputStream();
            return JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
