package ru.anr.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UriUtilsTest {

    @Test
    void getUri() {
        Assertions.assertEquals("https://api.host/ping",
                UriUtils.getUri("https", "api.host", 443, "/ping"));
        Assertions.assertEquals("http://api.host/ping",
                UriUtils.getUri("http", "api.host", 80, "/ping"));
        Assertions.assertEquals("http://api.host:8080/ping",
                UriUtils.getUri("http", "api.host", 8080, "/ping"));
    }
}