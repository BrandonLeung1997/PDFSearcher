package com.fyp.searcher.API;

import com.fyp.searcher.model.Collocation;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class API {
    public static CompletableFuture<HttpResponse<String>> getCollocation(String keyword) throws IOException, InterruptedException {
        String url = "https://linguatools-english-collocations.p.rapidapi.com/bolls/?lang=en&query="+keyword;
        //String url = "https://linguatools-english-collocations.p.rapidapi.com/bolls/?lang=en&query="+keyword+"&max_results=10";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-rapidapi-host", "linguatools-english-collocations.p.rapidapi.com")
                .header("x-rapidapi-key", "")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
//        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println(keyword);
//        System.out.println(response.body());
//        JSONArray jsonArray = new JSONArray(response.body());
//        //JSONObject jsonObject= new JSONObject(response.body());
//        System.out.println(jsonArray);
//        return jsonArray;
    }

    public static ArrayList<Collocation> getCollocationByPOS(String keyword, ArrayList<String> partOfSpeeches) throws IOException, InterruptedException {
        ArrayList<Collocation> collocations = new ArrayList<>();
        for (String pos :partOfSpeeches) {
            String url = "https://linguatools-english-collocations.p.rapidapi.com/bolls/?lang=en&query=" + keyword + "&relation=" + pos;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-host", "linguatools-english-collocations.p.rapidapi.com")
                    .header("x-rapidapi-key", "")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            String body = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
            collocations.addAll(Arrays.asList(new Gson().fromJson(body, Collocation[].class)));

        }
        return collocations;

    }
}
