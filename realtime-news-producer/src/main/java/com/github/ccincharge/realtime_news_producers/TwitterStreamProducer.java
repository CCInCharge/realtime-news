package com.github.ccincharge.realtime_news_producers;

import com.github.shyiko.dotenv.DotEnv;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.net.URISyntaxException;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class TwitterStreamProducer {
    private String BOOTSTRAP_SERVERS;
    private String TOPIC;

    TwitterStreamProducer(String bootstrapServers, String topic) {
        this.BOOTSTRAP_SERVERS = bootstrapServers;
        this.TOPIC = topic;
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    private Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "TwitterStreamProducer");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    void run() {
        Map<String, String> dotEnv = DotEnv.load();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(dotEnv.get("twitter4j.oauth.consumerKey"))
                .setOAuthConsumerSecret(dotEnv.get("twitter4j.oauth.consumerSecret"))
                .setOAuthAccessToken(dotEnv.get("twitter4j.oauth.accessToken"))
                .setOAuthAccessTokenSecret(dotEnv.get("twitter4j.oauth.accessTokenSecret"));
        TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());

        StatusListener listener = new StatusListener(){
            public void onStatus(Status status) {
                if (status.isPossiblySensitive()) {
                    return;
                }

                URLEntity[] urls = status.getURLEntities();
                if (urls.length == 0) {
                    return;
                }
                final Producer<String, String> producer = createProducer();
                try {
                    for (URLEntity url : urls) {
                        String longUrl = url.getExpandedURL();
                        String domain = getDomainName(longUrl);
                        if (Objects.equals(domain, "twitter.com")) {
                            continue;
                        }
                        // TODO: Lengthen this URL with bit.ly API
                        else if (Objects.equals(domain, "bit.ly")) {
                            continue;
                        }
                        // TODO: Refactor this into a list or a method that checks to see if URL is
                        // permitted or not
                        else if (Objects.equals(domain, "ift.tt")) {
                            continue;
                        }
                        else if (Objects.equals(domain, "fb.me")) {
                            continue;
                        }
                        else {
                            System.out.println(status.getUser().getName() + " : " + status.getText());
                            System.out.println(longUrl);
                            String tweeter = status.getUser().getScreenName();
                            String tweetText = status.getText();
                            Boolean isRetweet = status.isRetweet();
                            Date tweetedAt = status.getCreatedAt();
                            TweetFromStream tweet;
                            tweet = new TweetFromStream(tweeter, longUrl,
                                    tweetText, tweetedAt, isRetweet);

                            final ProducerRecord<String, String> record =
                                    new ProducerRecord<>(TOPIC, tweet.toString());
                            producer.send(record);
                            break;
                        }
                    }
                }
                catch (Exception e){e.printStackTrace();}
                finally {
                    producer.flush();
                    producer.close();
                }
            }
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
            public void onScrubGeo(long userId, long upToStatusId) {}
            public void onStallWarning(StallWarning warning) {}
        };
        TwitterStream twitterStream = tf.getInstance();
        twitterStream.addListener(listener);

        FilterQuery fq = new FilterQuery();
        fq.language("en");
        fq.count(0);
        fq.track("http breaking,http news");

        twitterStream.filter(fq);
    }
}
