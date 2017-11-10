package com.github.ccincharge.realtime_news_producers;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

class TweetFromStream {
    String tweeter;
    String urlInTweet;
    String tweetText;
    Date tweetedAt;
    Boolean isRetweet;

    TweetFromStream(String tweeter, String urlInTweet, String tweetText,
                    Date tweetedAt, Boolean isRetweet) {
        this.tweeter = tweeter;
        this.urlInTweet = urlInTweet;
        this.tweetText = tweetText;
        this.tweetedAt = tweetedAt;
        this.isRetweet = isRetweet;
    }

    public String toString() {
        String timeFormat;
        if (tweetedAt == null) {
            timeFormat = "";
        }
        else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z");
            timeFormat = df.format(this.tweetedAt);
        }
        HashMap<String, String> output = new HashMap<>();
        output.put("tweeter", tweeter);
        output.put("urlInTweet", urlInTweet);
        output.put("tweetText", tweetText);
        output.put("tweetedAt", timeFormat);
        output.put("isRetweet", isRetweet.toString());

        Gson gson = new Gson();
        return gson.toJson(output);
    }
}
