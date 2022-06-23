package ru.anr.base;

import java.math.BigDecimal;

/**
 * A test object.
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2014
 */

public class SampleObject {

    /**
     * Value field
     */
    private String value;

    /**
     * The index field
     */
    private Integer index;

    private String run(int x, String s, SampleObject o) {
        return s + x + value + " | " + o.toString();
    }

    /**
     * Construction
     *
     * @param value The value
     * @param index The index
     */
    public SampleObject(String value, Integer index) {

        this.value = value;
        this.index = index;
    }

    public SampleObject() {
        this(null, 0);
    }

    /**
     * Reading a value
     *
     * @return Value
     */
    public String getValue() {

        return value;
    }

    /**
     * @param value the value field
     */
    public void setValue(String value) {

        this.value = value;
    }

    /**
     * @return the index
     */
    public Integer getIndex() {

        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(Integer index) {

        this.index = index;
    }

    public BigDecimal asDecimal() {
        return new BigDecimal(this.value);
    }

    @Override
    public String toString() {
        return "SampleObject{" +
                "value='" + value + '\'' +
                ", index=" + index +
                '}';
    }
}
