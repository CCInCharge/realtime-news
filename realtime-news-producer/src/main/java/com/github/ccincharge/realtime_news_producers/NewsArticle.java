package com.github.ccincharge.realtime_news_producers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gson.Gson;

class NewsArticle {
    public String URL;
    public String dataSource;
    public String title;
    public String description;
    public Date publicationTime;

    public NewsArticle(String URL, String dataSource, String title, String description, Date publicationTime) {
        this.URL = URL;
        this.dataSource = dataSource;
        this.title = title;
        this.description = description;
        this.publicationTime = publicationTime;
    }

    /**
     * toString takes this news article and returns a string that is a JSON
     * representation of this article. If there was no publicationTime
     * reported from the API, returns empty string for the publicationTime.
     * @return JSON string representation of this object
     */
    public String toString() {
        String publicationTimeFormat;
        if (publicationTime == null) {
            publicationTimeFormat = "";
        }
        else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z");
            publicationTimeFormat = df.format(this.publicationTime);
        }

        // Maintains keys in order
        LinkedHashMap<String, String> output = new LinkedHashMap<>();
        output.put("URL", this.URL);
        output.put("dataSource", this.dataSource);
        output.put("title", this.title);
        output.put("description", this.description);
        output.put("publicationTime", publicationTimeFormat);

        Gson gson = new Gson();
        return gson.toJson(output, LinkedHashMap.class);
    }
}
