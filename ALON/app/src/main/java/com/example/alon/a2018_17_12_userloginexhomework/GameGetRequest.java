package com.example.alon.a2018_17_12_userloginexhomework;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GameGetRequest {

    public static String getRequest(String spec) {
        URL url;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            url = new URL(spec);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("invalid response code");
            }
            inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            StringBuilder builder = new StringBuilder();
            int actuallyRead;
            while ((actuallyRead = inputStream.read(buffer)) != -1) {
                builder.append(new String(buffer, 0, actuallyRead));
            }
            return builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
