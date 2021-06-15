package com.servicerec.vknews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class VkNews {
    private String urlMainBody;
    private List<String> parameters;
    private String accessToken;
    private String apiVersion;
    public VkNews() {
        urlMainBody = "https://api.vk.com/method/wall.get?";
        parameters = new ArrayList<>();
        accessToken = "&access_token=7ff037dd7ff037dd7ff037dd127f88006577ff07ff037dd1f352731fb860f5b3d3f8704";
        apiVersion = "&v=5.131";
        try
        {
            readDomains();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    public void showDomains()
    {
        for (int i = 0; i < parameters.size(); i++)
            System.out.println(parameters.get(i));
    }
    private void readDomains() throws IOException
    {
        FileReader reader = new FileReader("./DomainsList.conf");
        BufferedReader bufReader = new BufferedReader(reader);
        String line;
        while ((line = bufReader.readLine()) != null)
        {
            parameters.add("domain=" + line);
        }
        reader.close();
    }
    public void setNewAccessToken(String token) {
        accessToken = token;
    }
    public List<String> getNews() {
        String jsonString = "";
        List<String> parsedText = new ArrayList<>();
        Long currentDate = Calendar.getInstance().getTimeInMillis() - 1296000000;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < parameters.size(); i++) {
            if (i % 5 == 0) {
                try {
                    Thread.sleep(2000);
                }
                catch (Exception ignored) {}
            }
            try {
                jsonString = getHTML(urlMainBody + parameters.get(i) + accessToken + apiVersion);
                System.out.println("Retrieving news from " + parameters.get(i).split("=")[1] + "...");
                try {
                    JSONObject obj = new JSONObject(jsonString);
                    JSONObject response = obj.getJSONObject("response");
                    JSONArray arr = response.getJSONArray("items");
                    for (int j = 0; j < arr.length(); j++) {
                        Long postDate = arr.getJSONObject(j).getLong("date") * 1000;
                        if (postDate <= currentDate || j > 15)
                            break;
                        try {
                            String postDescription = arr.getJSONObject(j).getString("text");
                            if (postDescription != "") {
                                parsedText.add(postDescription + "\nКогда вышла новость: " + formatter.format(new Date(postDate)));
                            }
                        }
                        catch (JSONException skipped) {}
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println("Error occurred. Continue to get news from other sources");
                }
            } catch (Exception e) {
                System.err.println("Wrong parameters for domain name: "
                        + parameters.get(i).split("=")[1] + " or access token is expired");
            }
        }

        return parsedText;
    }

    private static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        for (String line; (line = reader.readLine()) != null; ) {
            result.append(line);
        }
        return result.toString();
    }
}
