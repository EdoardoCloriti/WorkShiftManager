package com.cloriti.workshiftmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cloriti.workshiftmanager.selection.MultiSelectionMenu;
import com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.util.db.AccessToDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorkshiftManager extends AppCompatActivity {

    private final static String PATTERN = "dd/MM/yyyy";
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private String CLEANING_DATE = "16/02/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshift_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Portait (Verticale)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // creazione del button di uscita dall'applicaizone
        Button exitButton = (Button) findViewById(R.id.exitbutton);
        // creazione del button di ingresso dell'appicazione
        Button startButton = (Button) findViewById(R.id.startbutton);

        Calendar sysCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        try {
            CLEANING_DATE = CLEANING_DATE + sysCalendar.get(Calendar.YEAR);
            Date cleaning = sdf.parse(CLEANING_DATE);
            if (sysCalendar.getTime().equals(cleaning)) {
                AccessToDB db = new AccessToDB();
                int year = sysCalendar.get(Calendar.YEAR);
                int yearAnnual = sysCalendar.get(Calendar.YEAR) - 1;
                int yearBiannual = sysCalendar.get(Calendar.YEAR) - 2;
                db.clearTurnByYear(yearAnnual, getApplicationContext());
                db.cleanWeekByYearToYear(yearBiannual, year, getApplicationContext());
                Toast.makeText(getApplicationContext(), "Effettuata pulizia annuale di turni", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Impossibile Effettuare la  pulizia annuale di turni", Toast.LENGTH_LONG).show();
        }
        AccessToDB db = new AccessToDB();
        if (db.existPropery(Property.NOTIFICA, getApplicationContext()) != 0) {
            // Set the alarm to start at approximately 2:00 p.m.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int curHr = calendar.get(Calendar.HOUR_OF_DAY);

            // Checking whether current hour is over 14
            if (curHr >= 2) {
                // Since current hour is over 14, setting the date to the next day
                calendar.add(Calendar.DATE, 1);
            }
            Log.v("ADebugTag", "It work! - INIT");
            calendar.set(Calendar.HOUR_OF_DAY, 1);
            calendar.set(Calendar.MINUTE, 5);
            // Schedule alarm manager

            Intent myIntent = new Intent(WorkshiftManager.this, WorkshiftManager.class);
            pendingIntent = PendingIntent.getBroadcast(WorkshiftManager.this, 0, myIntent, 0);

            alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, calendar.getTimeInMillis(), AlarmManager.ELAPSED_REALTIME, pendingIntent);

        }

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent multiSelectionMenu = new Intent(getApplicationContext(), MultiSelectionMenu.class);
                Intent applicationSetting = new Intent(getApplicationContext(), WorkShiftManagerSetting.class);

                AccessToDB db = new AccessToDB();
                if (db.getProperty(Property.READYTOGO, getApplicationContext()) == null) {
                    startActivity(applicationSetting);
                } else {
                    startActivity(multiSelectionMenu);
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        //if (WorkshiftManagerTutorial.isTutorialReq(getApplicationContext(), getLocalClassName()))
        //  WorkshiftManagerTutorial.showWorkShiftManagerTurorial(getApplicationContext(), WorkshiftManagerTutorial.WORK_SHIFT_MANAGER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workshift_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
