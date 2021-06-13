package com.servicerec.vknews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VkNews {
    private String urlMainBody;
    private List<String> parameters;
    private String accessToken;
    private String apiVersion;
    public VkNews() {
        urlMainBody = "https://api.vk.com/method/wall.get?";
        parameters = new ArrayList<>();
        parameters.add("domain=planetarium.kazan");
        parameters.add("domain=kassir_kzn");
        parameters.add("domain=bilet_on");
        parameters.add("domain=riviera_kazan");
        parameters.add("domain=kirlai");
        parameters.add("domain=kitaphane_tatarstan");
        parameters.add("domain=libkgmu");
        parameters.add("domain=expokazangroop");
        parameters.add("domain=kazangbkz");
        parameters.add("domain=cinema5kazan");
        parameters.add("domain=planetarium.kazan");
        parameters.add("domain=almazcinemakzn");
        parameters.add("domain=mirkazan");
        parameters.add("domain=karo");
        parameters.add("domain=kazantuz");
        parameters.add("domain=kazan_kremlin");
        parameters.add("domain=kazancircus");
        parameters.add("domain=tatfilarmonia");
        parameters.add("domain=kudagokzn");
        parameters.add("domain=freedom_kazanskaya7");
        parameters.add("domain=kazan_instructions");
        parameters.add("domain=teatrnabulake");
        parameters.add("domain=almetteatr");
        parameters.add("domain=tinchurinteatr");
        parameters.add("domain=kinomaxdiscount");
        parameters.add("domain=enter.media");
        parameters.add("domain=tatteatr");
        parameters.add("domain=tatfayzi");
        parameters.add("domain=kazan_opera");
        parameters.add("domain=kamalteatr");
        parameters.add("domain=ekiyat");
        parameters.add("domain=kzn");
        parameters.add("domain=moygorodkazan");
        parameters.add("domain=kznlife");
        parameters.add("domain=kzngot");
        parameters.add("domain=kzngo");
        accessToken = "&access_token=d891230831d51ed256bc9ccf6602db2eb0292ca928697dedfa43bcd7870c081659a97da523a732ad89214";
        apiVersion = "&v=5.131";
    }
    public void addGroupDomainName(List<String> domain)
    {
        parameters.addAll(domain);
        String filePath = "/VkGroupDomainNamesList";
        for (int i = 0; i < domain.size(); i++) {
            String newDomain = "domain=" + domain.get(i);
            parameters.add(newDomain);
            try {
                FileWriter writer = new FileWriter(filePath, true);
                BufferedWriter bufferWriter = new BufferedWriter(writer);
                bufferWriter.write(newDomain);
                bufferWriter.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
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
