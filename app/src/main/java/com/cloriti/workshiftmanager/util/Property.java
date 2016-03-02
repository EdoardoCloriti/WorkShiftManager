package com.cloriti.workshiftmanager.util;

public class Property {
    public static final String READYTOGO = "ready-to-go";
    public static final String ORESETTIMANALI = "ore-settimanali";
    public static final String ALLARM = "allarm";
    public static final String NOTIFICA = "notify";
    public static final String NOTIFICA_MIN = "notify-min";


    private String property = null;
    private String value = null;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
