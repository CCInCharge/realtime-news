package com.github.ccincharge.realtime_news_producers;

import com.github.shyiko.dotenv.DotEnv;
import com.google.gson.Gson;
import org.apache.commons.lang.WordUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;

class NewsApiEndpoint {
    private String newsApiSource;
    private String sortBy;

    NewsApiEndpoint(String newsApiSource, String sortBy) {
        this.newsApiSource = newsApiSource;
        this.sortBy = sortBy;
    }

    private class NewsApiArticlesResponse {
        private String author;
        private String title;
        private String description;
        private String url;
        private String urlToImage;
        private String publishedAt;

        String getAuthor() {
            if (Objects.equals(author, null)) {
                return "";
            }
            else {
                return author;
            }
        }

        String getTitle() {
            if (Objects.equals(title, null)) {
                return "";
            }
            else {
                return title;
            }
        }

        String getDescription() {
            if (Objects.equals(description, null)) {
                return "";
            }
            else {
                return description;
            }
        }

        String getUrl() {
            if (Objects.equals(url, null)) {
                return "";
            }
            else {
                return url;
            }
        }

        String getUrlToImage() {
            if (Objects.equals(urlToImage, null)) {
                return "";
            }
            else {
                return urlToImage;
            }
        }

        Date getPublishedAt() {
            if (Objects.equals(publishedAt, "")) {
                return null;
            }
            if (publishedAt == null) {
                return null;
            }
            List<SimpleDateFormat> knownDateFormats = new ArrayList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            knownDateFormats.add(format);
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            knownDateFormats.add(format);
            for (SimpleDateFormat df : knownDateFormats) {
                try {
                    return df.parse(publishedAt);
                }
                catch (ParseException p) {
                }
            }
            return null;
        }
    }

    private class NewsApiResponse {
        private String status;
        private String source;
        private String sortBy;
        private List<NewsApiArticlesResponse> articles;

        String getStatus() {
            if (Objects.equals(status, null)) {
                return "";
            }
            else {
                return status;
            }
        }

        String getSource() {
            if (Objects.equals(source, null)) {
                return "";
            }
            else {
                return source;
            }
        }

        String getSortBy() {
            if (Objects.equals(sortBy, null)) {
                return "";
            }
            else {
                return sortBy;
            }
        }
    }

    private String longName() {
        if (Objects.equals(this.newsApiSource, "abc-news-au")) {
            return "ABC News (AU)";
        }
        else if (Objects.equals(this.newsApiSource, "business-insider-uk")) {
            return "Business Insider (UK)";
        }
        else if (Objects.equals(this.newsApiSource, "mtv-news-uk")) {
            return "MTV News (UK)";
        }
        else if (Objects.equals(this.newsApiSource, "reddit-r-all")) {
            return "Reddit /r/all";
        }
        else if (Objects.equals(this.newsApiSource, "the-guardian-au")) {
            return "The Guardian (AU)";
        }
        else if (Objects.equals(this.newsApiSource, "the-guardian-uk")) {
            return "The Guardian (UK)";
        }
        else if (Objects.equals(this.newsApiSource, "wired-de")) {
            return "Wired.de";
        }
        else if (Objects.equals(this.newsApiSource, "bbc-news")) {
            return "BBC News";
        }
        else if (Objects.equals(this.newsApiSource, "bbc-sport")) {
            return "BBC Sport";
        }
        else if (Objects.equals(this.newsApiSource, "cnbc")) {
            return "CNBC";
        }
        else if (Objects.equals(this.newsApiSource, "cnn")) {
            return "CNN";
        }
        else if (Objects.equals(this.newsApiSource, "espn")) {
            return "ESPN";
        }
        else if (Objects.equals(this.newsApiSource, "espn-cric-info")) {
            return "ESPN Cric Info";
        }
        else if (Objects.equals(this.newsApiSource, "four-four-two")) {
            return "FourFourTwo";
        }
        else if (Objects.equals(this.newsApiSource, "ign")) {
            return "IGN";
        }
        else if (Objects.equals(this.newsApiSource, "mtv-news")) {
            return "MTV News";
        }
        else if (Objects.equals(this.newsApiSource, "nfl-news")) {
            return "NFL News";
        }
        else if (Objects.equals(this.newsApiSource, "talksport")) {
            return "TalkSport";
        }
        else if (Objects.equals(this.newsApiSource, "techcrunch")) {
            return "TechCrunch";
        }
        else if (Objects.equals(this.newsApiSource, "techradar")) {
            return "TechRadar";
        }
        else if (Objects.equals(this.newsApiSource, "usa-today")) {
            return "USA Today";
        }
        else {
            String longName = this.newsApiSource.replace("-", " ");
            longName = WordUtils.capitalize(longName);
            return longName;
        }
    }

    Queue<NewsArticle> getResponse() {
        Map<String, String> dotEnv = DotEnv.load();
        String API_KEY = dotEnv.get("NEWSAPI_API_KEY");

        Client client = ClientBuilder.newClient();
        String baseURL = "https://newsapi.org/v1/articles?";
        String targetURL = baseURL + "source=" + this.newsApiSource;
        targetURL += "&apiKey=" + API_KEY;
        targetURL += "&sortBy=" + this.sortBy;

        WebTarget target = client.target(targetURL);

        String response = target.request(MediaType.APPLICATION_JSON).get(String.class);

        NewsApiResponse responseObj = (new Gson()).fromJson(response, NewsApiResponse.class);

        Queue<NewsArticle> output = new LinkedList<>();
        if (Objects.equals(responseObj.status, "error")) {
            return output;
        }

        for (NewsApiArticlesResponse article : responseObj.articles) {
            String dataSource = longName();
            String title = article.getTitle();
            String URL = article.getUrl();
            String description = article.getDescription();
            Date publicationTime = article.getPublishedAt();
            NewsArticle curArticle = new NewsArticle(URL, dataSource, title, description, publicationTime);
            output.add(curArticle);
        }
        return output;
    }
}
