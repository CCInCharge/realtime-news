package com.github.ccincharge;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NewsArticleTest {
    @Test public void emptyInit() throws Exception {
        NewsArticle article = new NewsArticle("", "", "", "", null);
        assertEquals("", article.URL);
        assertEquals("", article.dataSource);
        assertEquals("", article.title);
        assertEquals("", article.description);
        assertNull(article.publicationTime);
    }
}