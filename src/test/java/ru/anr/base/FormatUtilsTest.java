package ru.anr.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class FormatUtilsTest extends BaseParent {

    @Test
    public void format() {
        Assertions.assertEquals("$100.00",
                FormatUtils.format(d("100"), 2, true, true, "$",
                        Locale.ENGLISH));
        Assertions.assertEquals("$1,000.00",
                FormatUtils.format(d("1000"), 2, true, true, "$",
                        Locale.ENGLISH));

        Assertions.assertEquals("$1 000,00",
                FormatUtils.format(d("1000"), 2, true, true, "$",
                        new Locale("ru")));

        // How currency = false works
        Assertions.assertEquals("100g",
                FormatUtils.format(d("100"), 5, false, false, "g",
                        Locale.ENGLISH));
    }
}