package com.puc.parte_electronico.model;

/**
 * Created by jose on 5/13/14.
 */
public class TrafficViolation {
    private String mType;
    private int mValue;


    public TrafficViolation() {

    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }
}
