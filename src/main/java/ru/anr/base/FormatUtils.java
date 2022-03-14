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
