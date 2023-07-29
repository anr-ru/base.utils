/*
 * Copyright 2014-2023 the original author or authors.
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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Alexey Romanchuk
 * @created Dec 05, 2021
 */
public class FormatUtils extends BaseParent {

    /**
     * Formats the given number with the use of the given pattern.
     *
     * @param value         The decimal value to format
     * @param scale         The scale
     * @param currency      true, the value means a currency or false if we deal with a commodity
     * @param symbolAtStart true, if the symbol needs to be put before the value, or false if after the value
     * @param symbol        The currency/commodity symbol
     * @param locale        the locale
     * @return The resulted formatted string with the value
     */
    public static String format(BigDecimal value, int scale, boolean currency, boolean symbolAtStart, String symbol, Locale locale) {

        NumberFormat f = NumberFormat.getInstance(locale);
        int s = nullSafeOp(scale).orElse(0);

        if (currency) {
            f.setMinimumFractionDigits(s);
            f.setMaximumFractionDigits(s);
        } else {
            /*
             * Commodities may have many fraction digits, i.e. 0.12345 g. To shorten the formatted
             * amount we may omit last zeros, i.e. to write 0.1 g instead of 0.10000 g.
             */
            f.setMaximumFractionDigits(s);
            f.setMinimumIntegerDigits(1);
        }

        String str = f.format(value.doubleValue());
        if (symbolAtStart) {
            str = nullSafe(symbol) + str;
        } else {
            str = str + nullSafe(symbol);
        }
        return str;
    }

}
