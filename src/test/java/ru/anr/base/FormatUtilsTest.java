package ru.anr.base;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

public class FormatUtilsTest extends BaseParent {

    @Test
    public void format() {
        Assert.assertEquals("$100.00",
                FormatUtils.format(d("100"), 2, true, true, "$",
                        Locale.ENGLISH));
        Assert.assertEquals("$1,000.00",
                FormatUtils.format(d("1000"), 2, true, true, "$",
                        Locale.ENGLISH));

        Assert.assertEquals("$1 000,00",
                FormatUtils.format(d("1000"), 2, true, true, "$",
                        new Locale("ru")));

        // How currency = false works
        Assert.assertEquals("100g",
                FormatUtils.format(d("100"), 5, false, false, "g",
                        Locale.ENGLISH));
    }
}