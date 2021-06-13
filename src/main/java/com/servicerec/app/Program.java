package com.servicerec.app;

import com.servicerec.vknews.VkNews;

import java.util.*;

public class Program {
    public static void main(String[] args) throws Exception {
        VkNews news = new VkNews();
        List<String> newsList = news.getNews();
        Scanner in = new Scanner(System.in);
        String userInput = "";
        System.out.println("Введите ключевое слово по которому будет вестись поиск:");
        while ((userInput = in.nextLine()) != null) {
            if (userInput.length() > 4) {
                String toTrim = userInput.substring(userInput.length() - 2);
                userInput = userInput.replace(toTrim, "");
            }
            if (userInput == "") {
                for (int i = 0; i < newsList.size(); i++)
                    if (newsList.get(i).contains("июн") || newsList.get(i).contains("Июн")) {
                        System.out.println("--------------------------------------------------------");
                        System.out.println(newsList.get(i));
                    }
            } else {
                for (int i = 0; i < newsList.size(); i++)
                    if (newsList.get(i).toUpperCase().contains(userInput.toUpperCase())) {
                        System.out.println("--------------------------------------------------------");
                        System.out.println(newsList.get(i));
                    }
            }
            System.out.println("Введите ключевое слово по которому будет вестись поиск:");
        }
    }
}