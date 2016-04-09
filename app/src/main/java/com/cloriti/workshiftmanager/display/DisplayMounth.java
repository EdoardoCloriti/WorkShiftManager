package com.cloriti.workshiftmanager.display;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.util.Week;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

import java.text.DateFormatSymbols;
import java.util.List;

/**
 * Activity per estrarre il mese passato in input e a mostrarlo all'utente
 *
 * @Author edoardo.cloriti@studio.unibo.it
 */
public class DisplayMounth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_mounth);
        //Gestione della toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_app_upper);
        toolbar.setLogo(R.mipmap.ic_insert_invitation_black_48dp);
        //Gestione della navigation icon
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //estrazione del mese e anno passato dall'activity chiamante tramite l'intent
        Bundle input = this.getIntent().getExtras();
        int month = input.getInt("MONTH");
        int year = input.getInt("YEAR");
        TextView meseAttuale = (TextView) findViewById(R.id.mese_attuale);
        TextView ore = (TextView) findViewById(R.id.ore);
        TextView straordinari = (TextView) findViewById(R.id.straordinari);
        meseAttuale.setText(new DateFormatSymbols().getMonths()[month - 1] + " " + year);
        //setting delle ore
        ore.setText(calculateHour(month, year));
        //setting degli straordinari
        straordinari.setText(calculateOvertime(month, year));
    }

    /**
     * Metodo per l'estrazione dal database delle settimane che fanno parte di un determinato mese
     * associato ai parametri passati e al calcolo delle ore totali
     *
     * @param mounth
     * @param year
     * @return
     */
    private String calculateHour(int mounth, int year) {
        AccessToDB db = new AccessToDB();
        List<Week> weeks = db.getMounth(mounth, year, getApplicationContext());
        double hours = 0;
        for (Week week : weeks) {
            hours = hours + week.getHour();
        }
        return Double.toString(hours);

    }

    /**
     * Metodo per l'estrazione dal database delle settimane che fanno parte di un determinato mese
     * associato ai parametri passati e al calcolo degli straordinari totali
     *
     * @param mounth
     * @param year
     * @return
     */
    private String calculateOvertime(int mounth, int year) {
        AccessToDB db = new AccessToDB();
        List<Week> weeks = db.getMounth(mounth, year, getApplicationContext());
        double hours = 0;
        for (Week week : weeks) {
            hours = hours + week.getExtraHour();
        }
        return Double.toString(hours);

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
            Intent i = new Intent(getApplicationContext(), WorkShiftManagerSetting.class);
            startActivity(i);
        }
        if (id == R.id.action_help) {
            WorkshiftManagerTutorial.showWorkShiftManagerTurorial(DisplayMounth.this, WorkshiftManagerTutorial.DISPLAY_MONTH);
        }


        return super.onOptionsItemSelected(item);
    }

}
