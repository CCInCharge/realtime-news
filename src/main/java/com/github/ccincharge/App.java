package com.github.ccincharge;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.github.shyiko.dotenv.DotEnv;

import java.util.Map;
import java.util.Queue;

public class App {
    public static void main( String[] args ) {
        /*
        Map<String, String> dotEnv = DotEnv.load();
        String API_KEY = dotEnv.get("NEWSAPI_API_KEY");

        Client client = ClientBuilder.newClient();
        String targetURL = "https://newsapi.org/v1/articles?source=the-new-york-times&sortBy=top&apiKey=";
        WebTarget target = client.target(targetURL + API_KEY);
        System.out.println(target.request(MediaType.APPLICATION_JSON).get(String.class));
        */
        NewsApiProducer newsApiProducer = new NewsApiProducer("localhost:9092", "news-topic");
        // Queue<NewsArticle> articles = newsApiProducer.runProducer();
        newsApiProducer.runProducer();
        /*
        for (NewsArticle article : articles) {
            System.out.println(article.dataSource);
            System.out.println(article.URL);
        }
        */
    }
}
