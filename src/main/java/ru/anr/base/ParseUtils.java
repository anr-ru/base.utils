/*
 * Copyright 2014 the original author or authors.
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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.jamesmurty.utils.XMLBuilder;

/**
 * Simplest parsing utilities for quick searching in XMLs or strings via regular
 * expressions.
 *
 *
 * @author Alexey Romanchuk
 * @created Mar 4, 2016
 *
 */

public final class ParseUtils extends BaseParent {

    /**
     * Parse utilities
     */
    private ParseUtils() {

        super();
        /*
         * Empty
         */
    }

    /**
     * Parsing the provided xml by the specified xpath query
     * 
     * @param xml
     *            The XML to use specified as a string
     * @param query
     *            The xpath query
     * @return The result
     */
    public static String xpath(String xml, String query) {

        try {
            XMLBuilder b = XMLBuilder.parse(xml);
            return b.xpathQuery(query, XPathConstants.STRING).toString();

        } catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Parsing a string via the provided regular expression
     * 
     * @param text
     *            The text to parse
     * @param pattern
     *            The pattern to use
     * @param groups
     *            The number of groups in the regular expression to extract. If
     *            more than one group is used, all the groups will be combined
     *            as a single string
     * 
     * @return The resulted value
     */
    public static String regexp(String text, String pattern, Integer... groups) {

        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        Matcher m = p.matcher(text);

        return m.find() ? list(groups).stream().map(i -> m.group(i)).collect(Collectors.joining()) : null;
    }
}