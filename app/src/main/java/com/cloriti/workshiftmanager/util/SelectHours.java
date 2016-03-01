package com.cloriti.workshiftmanager.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.manage.CreateWorkShift;

import java.util.Calendar;

public class SelectHours extends AppCompatActivity {

    private Intent outputIntent = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_hours);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        String result = null;
        if (!validatTimetable(hourStart, minuteStart, hourFinish, minuteFinish)) {
            return null;
        } else {
            result = (hourStart + ":" + minuteStart);
            result = result + "-" + (hourFinish + ":" + minuteFinish);
        }
        return result;
    }

    /*
     * Metodo per la gestione dell'orario del turno caso 'PM'
     */
    private String managePM(Integer hourStart, Integer minuteStart, Integer hourFinish, Integer minuteFinish) {
        String result = null;
        if (!validatTimetable(hourStart, minuteStart, hourFinish, minuteFinish)) {
            return null;
        } else {
            result = (hourStart + ":" + minuteStart);
            result = result + "-" + (hourFinish + ":" + minuteFinish);
        }
        return result;
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

    private boolean verifyInitHour(String orario, int hour) {
        if ("am".equalsIgnoreCase(orario)) {
            return hour <= 13;
        } else if ("pm".equalsIgnoreCase(orario)) {
            return hour >= 13;
        }
        return false;
    }

    private boolean verifyFinishHour(String orario, int hour) {
        //TODO: per ora non sono previsti controlli per l'orario di fine turno -> return true
        /*if ("am".equalsIgnoreCase(orario)) {
            if (hour > 16)
                return false;
            else
                return true;
        } else if ("pm".equalsIgnoreCase(orario)) {
            if (hour < 14)
                return false;
            else
                return true;
        }
        return false;*/
        return true;
    }

}
