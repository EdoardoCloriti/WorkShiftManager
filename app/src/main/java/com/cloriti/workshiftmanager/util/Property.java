package com.cloriti.workshiftmanager.util;

public class Property {
    public static final String READYTOGO = "ready-to-go";
    public static final String ORESETTIMANALI = "ore-settimanali";
    public static final String ALLARM = "allarm";
    public static final String NOTIFICA = "notify";
    public static final String DEVMODE = "dev-mode";
    public static final String TUTORIAL = "tutorial-req-";

    private String property = null;
    private String value = null;

    public void setProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
