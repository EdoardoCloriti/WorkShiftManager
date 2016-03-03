package com.cloriti.workshiftmanager.util;

import android.content.Context;

/**
 * Strategy per evitare i NullPointer
 *
 * @Author edoardo.cloriti@studio.unibo.it
 */
public class NullObjectStrategy {

    /**
     * metodo per la gestione dei Turn Null
     *
     * @return
     */
    public static Turn nullTurn() {
        return new Turn();
    }

    /**
     * metodo per la gestione delle Property Null
     *
     * @return
     */
    public static Property nullProperty() {
        return new Property();
    }

    /**
     * metodo per la gestione delle Week Null
     *
     * @return
     */
    public static Week nullWeek(Context context) {
        return new Week(context);
    }
}
