package com.cloriti.workshiftmanager.util;

import android.content.Context;

/**
 * Created by Edoardo on 18/02/2016.
 */
public class NullObjectStrategy {

    public static Turn nullTurn() {
        return new Turn();
    }

    public static Property nullProperty() {
        return new Property();
    }

    public static Week nullWeek(Context context) {
        return new Week(context);
    }
}
