/*
 * Copyright 2014-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.anr.base;

/**
 * Utilities for working with urls.
 *
 * @author Alexey Romanchuk
 * @created Mar 18, 2015
 */

public final class UriUtils {

    /**
     * Empty
     */
    private UriUtils() {

    }

    /**
     * Building a base url string (server location), excluding a printing of
     * standard http ports.
     *
     * @param schema https or http
     * @param host   Name of host
     * @param port   Port number
     * @return String with server location with schema, host and port
     */
    public static String getBaseUrl(String schema, String host, int port) {
        String portSuffix = (port == 80 || port == 443) ? "" : ":" + port;
        return schema + "://" + host + portSuffix;
    }

    /**
     * Get final URI of http resource
     *
     * @param schema https or http
     * @param host   Name of host
     * @param port   Port number
     * @param path   Can be a relative path (for instance, '/ping') or a full one (
     *               {@link #getBaseUrl(String, String, int)} is not used) like
     *               <a href="http://localhost:9090/ping">...</a>
     * @return A full path to http resource (included schema, host, port,
     * relative path)
     */
    public static String getUri(String schema, String host, int port, String path) {

        return hasHost(path) ? path : getBaseUrl(schema, host, port) + (path.charAt(0) == '/' ? path : "/" + path);
    }

    /**
     * Is a host present in the url?
     *
     * @param path Url path
     * @return true, if presents
     */
    public static boolean hasHost(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }
}
