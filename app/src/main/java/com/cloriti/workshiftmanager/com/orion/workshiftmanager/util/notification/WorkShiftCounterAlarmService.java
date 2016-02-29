package com.cloriti.workshiftmanager.com.orion.workshiftmanager.util.notification;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;

import com.orion.workshiftmanager.util.Turn;
import com.orion.workshiftmanager.util.db.AccessToDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Edoardo on 22/02/2016.
 */
public class WorkShiftCounterAlarmService extends WorkShiftCounterService {

    public WorkShiftCounterAlarmService(Context context) {
        setHourToNotify(24);
        setContext(context);
    }

    @Override
    public void specializedNotify() {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        Calendar calendar;
        AccessToDB db = new AccessToDB();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        String tomorrow = sdf.format(calendar.getTime());
        if (db.existTurn(tomorrow, context) != 0) {
            Turn turn = db.getTurnBySelectedDay(tomorrow, context);
            int h = turn.getInizioMattinaH().intValue();
            int m = turn.getInizioMattinaM().intValue();
            h = h - 1;
            if (h < 0)
                h = 23;
            createAlarm(h, m);
        }
    }

    private void createAlarm(int hour, int minute) {
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, hour);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        context.startActivity(i);
    }
}
