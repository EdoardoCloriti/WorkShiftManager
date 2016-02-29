package com.cloriti.workshiftmanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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
