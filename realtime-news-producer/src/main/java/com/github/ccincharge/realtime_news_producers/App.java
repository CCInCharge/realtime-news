package com.github.ccincharge.realtime_news_producers;

public class App {
    public static void main( String[] args ) {
        /*
        NewsApiProducer newsApiProducer = new NewsApiProducer("localhost:9092", "news-topic");
        newsApiProducer.runProducer();
        */

        TwitterStreamProducer stream = new TwitterStreamProducer("localhost:9092", "twitter-topic");
        stream.run();
    }
}
