package com.github.ccincharge;

import com.github.shyiko.dotenv.DotEnv;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.net.URISyntaxException;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class TwitterStreamClass {
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
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
                            // TODO: Send to Kafka
                            break;
                        }
                    }
                }
                catch (Exception e){e.printStackTrace();}
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
