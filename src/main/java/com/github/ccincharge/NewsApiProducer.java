package com.github.ccincharge;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Properties;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

/** Kafka producer that sends data consumed from the NewsApi API.
 * @author Charles Chen
 */
class NewsApiProducer {
    private String BOOTSTRAP_SERVERS;
    private String TOPIC;

    NewsApiProducer(String bootstrapServers, String topic) {
        this.BOOTSTRAP_SERVERS = bootstrapServers;
        this.TOPIC = topic;
    }

    private Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "NewsApiProducer");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    /**
     * Compiles list of NewsApi endpoints, consumes them, and sends results to
     * Kafka.
     */
    void runProducer() {
        ArrayList<NewsApiEndpoint> newsApiEndpoints = new ArrayList<>();
        newsApiEndpoints.add(new NewsApiEndpoint("the-new-york-times", "top"));
        newsApiEndpoints.add(new NewsApiEndpoint("bbc-news", "top"));
        newsApiEndpoints.add(new NewsApiEndpoint("the-new-york-times", "top"));
        newsApiEndpoints.add(new NewsApiEndpoint("associated-press", "top"));
        newsApiEndpoints.add(new NewsApiEndpoint("cnn", "top"));
        newsApiEndpoints.add(new NewsApiEndpoint("google-news", "top"));
        newsApiEndpoints.add(new NewsApiEndpoint("the-guardian-uk", "top"));
        newsApiEndpoints.add(new NewsApiEndpoint("the-washington-post", "top"));
        newsApiEndpoints.add(new NewsApiEndpoint("usa-today", "top"));
        
        Queue<NewsArticle> curNewsArticles;
        Queue<NewsArticle> allNewsArticles = new ArrayDeque<>();
        for (NewsApiEndpoint endpoint : newsApiEndpoints) {
            curNewsArticles = endpoint.getResponse();
            while (!curNewsArticles.isEmpty()) {
                allNewsArticles.add(curNewsArticles.poll());
            }
        }

        final Producer<String, String> producer = createProducer();
        try {
            for (NewsArticle article : allNewsArticles) {
                final ProducerRecord<String, String> record =
                        new ProducerRecord<>(TOPIC, article.URL, article.toString());
                producer.send(record);
            }
        } finally {
            producer.flush();
            producer.close();
        }
    }
}
