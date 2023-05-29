package de.joshicodes.rja.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class HttpUtil {

    public static final String AUTH_HEADER_BOT = "X-Bot-Token";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 OPR/77.0.4054.203";

    public static JsonElement sendRequest(String url, String method, @Nullable String authHeader, @Nullable String auth, JsonElement body) throws IOException, InterruptedException {
        return sendRequest(url, method, authHeader, auth, body, null);
    }

    public static JsonElement sendRequest(String url, String method, @Nullable String authHeader, @Nullable String auth, JsonElement body, @Nullable HashMap<String, String> headers) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(URI.create(url));
        if(body != null) {
            request.method(method, HttpRequest.BodyPublishers.ofString(body.toString()));
        } else {
            request.method(method, HttpRequest.BodyPublishers.noBody());
        }
        request.header("User-Agent", USER_AGENT);
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        if (authHeader != null && auth != null) {
            request.header(authHeader, auth);
        }
        if(headers != null && !headers.isEmpty()) {
            for(String key : headers.keySet()) {
                request.header(key, headers.get(key));
            }
        }

        HttpRequest req = request.build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        String res = response.body();
        if(res == null || res.isEmpty()) {
            return null;
        }
        if(!res.startsWith("{") && !res.startsWith("[")) {
            return null;
        }
        return JsonParser.parseString(res);

    }

    public static JsonObject readJson(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new java.net.URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();
            InputStream is = connection.getInputStream();
            JsonObject object = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            connection.disconnect();
            return object;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
