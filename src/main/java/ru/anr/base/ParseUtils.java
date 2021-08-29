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

import com.jamesmurty.utils.XMLBuilder;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simplest parsing utilities for quick searching in XMLs or strings via regular
 * expressions.
 *
 * @author Alexey Romanchuk
 * @created Mar 4, 2016
 */

public final class ParseUtils {

    /**
     * Parse utilities
     */
    private ParseUtils() {
        super();
    }

    /**
     * Parsing the provided xml by the specified xpath query
     *
     * @param xml   The XML to use specified as a string
     * @param query The xpath query
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
     * @param text    The text to parse
     * @param pattern The pattern to use
     * @param groups  The number of groups in the regular expression to extract. If
     *                more than one group is used, all the groups will be combined
     *                as a single string
     * @return The resulted value
     */
    public static String regexp(String text, String pattern, Integer... groups) {

        List<String> list = regexpGroups(text, pattern, groups);
        return list == null ? null : String.join("", list);
    }

    /**
     * Parses a string via the provided regular expression and returns the given
     * groups
     *
     * @param text    An original text
     * @param pattern A pattern to use
     * @param groups  Number of groups in the pattern
     * @return A list of groups
     */
    public static List<String> regexpGroups(String text, String pattern, Integer... groups) {

        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        Matcher m = p.matcher(text);

        /*
         * There can be conditional groups like (..)? which give null values
         */
        return m.find() ? BaseParent.list(BaseParent.list(groups).stream().map(m::group).filter(Objects::nonNull)) : null;
    }

    /**
     * Parses the string enum value as a enum but with suppressing all exceptions
     *
     * @param clazz    A enum class
     * @param strValue A string value
     * @param <S>      The enum type
     * @return The parsed value
     */
    public static <S extends Enum<S>> S parseEnum(Class<S> clazz, String strValue) {
        S v = null;
        try {
            v = Enum.valueOf(clazz, strValue);
        } catch (NullPointerException | IllegalArgumentException ignored) {
        }
        return v;
    }

    /**
     * Parses the given string value with the use of the given pattern.
     *
     * @param value   The value
     * @param pattern The pattern
     * @return The parsed local date/time object
     */
    public static LocalDateTime parseLocal(String value, String pattern) {
        LocalDateTime t;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            return LocalDateTime.parse(value, formatter);
        } catch (DateTimeParseException ex1) {
            try {
                LocalDate d = LocalDate.parse(value, formatter);
                return d.atStartOfDay();
            } catch (DateTimeParseException ex2) {
                t = null;
            }
        }
        return t;
    }
}
