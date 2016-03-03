package com.cloriti.workshiftmanager.util;

/**
 * Classe per contenere le Property ovvero i setting dell'applicazione
 * da inserire o da prendere da DB
 *
 * @Author edoardo.cloriti@studio.unibo.it
 */
public class Property {

    /**
     * Field statici per le Property gestite dall'applicazione
     */
    public static final String READYTOGO = "ready-to-go";
    public static final String ORESETTIMANALI = "ore-settimanali";
    public static final String ALLARM = "allarm";
    public static final String NOTIFICA = "notify";
    public static final String NOTIFICA_MIN = "notify-min";


    private String property = null;
    private String value = null;

    /**
     * metodo per restituire il nome della Property
     *
     * @return
     */
    public String getProperty() {
        return property;
    }

    /**
     * metodo per settare il nome della Property
     *
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * metodo per la restituzione del valore della property
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Metodo per settare il valore della Property
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
