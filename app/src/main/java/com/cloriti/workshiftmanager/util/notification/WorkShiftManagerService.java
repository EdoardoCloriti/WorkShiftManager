package com.cloriti.workshiftmanager.util.notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

/**
 * Created by Edoardo on 22/02/2016.
 */
public abstract class WorkShiftManagerService extends IntentService {

    protected static final String PATTERN = "dd/MM/yyyy";
    protected int hourToNotify = 0;
    protected Context context;

    public WorkShiftManagerService() {
        super("WorkShiftManager");
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setHourToNotify(int hourToNotify) {
        this.hourToNotify = hourToNotify;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            specializedNotify();
            try {
                TimeUnit.HOURS.sleep(hourToNotify);
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    public abstract void specializedNotify();
}
