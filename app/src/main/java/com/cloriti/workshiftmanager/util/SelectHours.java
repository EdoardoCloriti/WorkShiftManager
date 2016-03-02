package com.cloriti.workshiftmanager.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.manage.CreateWorkShift;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

import java.util.Calendar;

public class SelectHours extends AppCompatActivity {

    private Intent outputIntent = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_hours);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(R.string.title_app_upper);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);

        Bundle bundle = this.getIntent().getExtras();
        outputIntent = new Intent(getApplicationContext(), CreateWorkShift.class);
        final String h = bundle.getString("part-of-day");
        final TextView titleOriario = (TextView) findViewById(R.id.title_orario);
        final TimePicker orarioInizio = (TimePicker) findViewById(R.id.inizio);
        final TimePicker orarioFine = (TimePicker) findViewById(R.id.fine);
        Button submit = (Button) findViewById(R.id.submit);
        Button back = (Button) findViewById(R.id.back);
        //CheckBox importante = (CheckBox) findViewById(R.id.riunione);
        outputIntent.putExtra("priority", false);
        orarioInizio.setIs24HourView(false);
        if ("am".equalsIgnoreCase(h)) {
            titleOriario.setText(R.string.matt);
            orarioInizio.setCurrentHour(0);
            orarioInizio.setCurrentMinute(0);
            orarioFine.setCurrentHour(13);
            orarioFine.setCurrentMinute(0);
        } else if ("pm".equalsIgnoreCase(h)) {
            titleOriario.setText(R.string.pome);
            orarioInizio.setCurrentHour(14);
            orarioInizio.setCurrentMinute(0);
            orarioFine.setCurrentHour(24);
            orarioFine.setCurrentMinute(0);
        } else {
            //non dovrebbe mai passare di qui visto che i dati arrivano da un data-entry
            // per cui gli unici valorei possibili sono 'am' || 'pm'
            throw new RuntimeException("part of the day does not exist, can not continue");
        }

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePicker orarioInizio = (TimePicker) findViewById(R.id.inizio);
                TimePicker orarioFine = (TimePicker) findViewById(R.id.fine);
                Integer hourStart = orarioInizio.getHour();
                Integer minuteStart = orarioInizio.getMinute();
                Integer hourFinish = orarioFine.getHour();
                Integer minuteFinish = orarioFine.getMinute();
                outputIntent.putExtra(IDs.PART_OF_DAY, h);
                String am = manageAM(hourStart, minuteStart, hourFinish, minuteFinish);
                String pm = managePM(hourStart, minuteStart, hourFinish, minuteFinish);
                if ("am".equalsIgnoreCase(h) && am != null) {
                    outputIntent.putExtra(IDs.ORARIO_MATTINA, am);
                    setResult(2, outputIntent);
                    finish();
                } else if ("pm".equalsIgnoreCase(h) && pm != null) {
                    outputIntent.putExtra(IDs.ORARIO_POMERIGGIO, pm);
                    setResult(2, outputIntent);
                    finish();
                } else
                    alert();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /*
      * Metodo per la gestione dell'orario del turno caso 'AM'
      */
    private String manageAM(Integer hourStart, Integer minuteStart, Integer hourFinish, Integer minuteFinish) {
        if (!validatTimetable(hourStart, minuteStart, hourFinish, minuteFinish))
            return null;
        else if (!validateStartHour("am", hourStart))
            return null;
        else
            return (hourStart + ":" + minuteStart) + "-" + (hourFinish + ":" + minuteFinish);
    }

    /*
     * Metodo per la gestione dell'orario del turno caso 'PM'
     */
    private String managePM(Integer hourStart, Integer minuteStart, Integer hourFinish, Integer minuteFinish) {
        if (!validatTimetable(hourStart, minuteStart, hourFinish, minuteFinish))
            return null;
        else if (!validateStartHour("am", hourStart))
            return null;
        else
            return (hourStart + ":" + minuteStart) + "-" + (hourFinish + ":" + minuteFinish);
    }

    private void alertTost() {
        Toast.makeText(this, "Orario inserito non valido si prega di ricompilare", Toast.LENGTH_LONG).show();
    }

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectHours.this);
        builder.setTitle(this.getString(R.string.title_activity_select_hours));
        builder.setMessage("Orario inserito non valido si prega di ricompilare");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton(this.getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog d = builder.create();
        d.show();
    }

    private boolean validatTimetable(int oraI, int minI, int oraf, int minF) {
        Calendar init = Calendar.getInstance();
        init.set(Calendar.HOUR, oraI);
        init.set(Calendar.MINUTE, minI);

        Calendar finish = Calendar.getInstance();
        finish.set(Calendar.HOUR, oraf);
        finish.set(Calendar.MINUTE, minF);
        if (finish.before(init) || finish.equals(init))
            return false;
        else
            return true;
    }

    private boolean validateStartHour(String orario, int hour) {
        if ("am".equalsIgnoreCase(orario)) {
            return hour <= 11;
        } else if ("pm".equalsIgnoreCase(orario)) {
            return hour >= 11;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workshift_manager_submit, menu);
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
            WorkshiftManagerTutorial.showWorkShiftManagerTurorial(SelectHours.this, "CreateWorkShift");
        }


        return super.onOptionsItemSelected(item);
    }
}
