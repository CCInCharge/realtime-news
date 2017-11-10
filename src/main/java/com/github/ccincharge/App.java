package com.github.ccincharge;

public class App {
    public static void main( String[] args ) {
        /*
        NewsApiProducer newsApiProducer = new NewsApiProducer("localhost:9092", "news-topic");
        newsApiProducer.runProducer();
        */

        TwitterStreamClass stream = new TwitterStreamClass();
        stream.run();
    }
}
