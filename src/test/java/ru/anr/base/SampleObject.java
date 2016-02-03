/**
 * 
 */
package ru.anr.base;

/**
 * A test object.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2014
 *
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

    /**
     * Construction
     * 
     * @param value
     *            The value
     * @param index
     *            The index
     */
    public SampleObject(String value, int index) {

        this.value = value;
        this.index = index;
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
     * 
     * @param value
     *            the value field
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
     * @param index
     *            the index to set
     */
    public void setIndex(Integer index) {

        this.index = index;
    }
}
