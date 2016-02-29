package com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.IDs;

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
                Integer hourStart = orarioInizio.getCurrentHour();
                Integer minuteStart = orarioInizio.getCurrentMinute();
                Integer hourFinish = orarioFine.getCurrentHour();
                Integer minuteFinish = orarioFine.getCurrentMinute();
                outputIntent.putExtra(IDs.PART_OF_DAY, h);
                if ("am".equalsIgnoreCase(h))
                    outputIntent.putExtra(IDs.ORARIO_MATTINA, manageAM(hourStart, minuteStart, hourFinish, minuteFinish));
                else if ("pm".equalsIgnoreCase(h))
                    outputIntent.putExtra(IDs.ORARIO_POMERIGGIO, managePM(hourStart, minuteStart, hourFinish, minuteFinish));
                setResult(2, outputIntent);
                finish();
            }

            /*
             * Metodo per la gestione dell'orario del turno caso 'AM'
             */
            private String manageAM(Integer hourStart, Integer minuteStart, Integer hourFinish, Integer minuteFinish) {
                String result = null;
                if (!verifyInitHour("am", hourStart)) {
                    allert("am");
                } else {
                    result = hourStart + ":" + minuteStart;
                }

                if (!verifyFinishHour("am", hourFinish)) {
                    allert("am");
                } else {
                    result = result + "-" + hourFinish + ":" + minuteFinish;
                }
                return result;
            }

            /*
             * Metodo per la gestione dell'orario del turno caso 'PM'
             */
            private String managePM(Integer hourStart, Integer minuteStart, Integer hourFinish, Integer minuteFinish) {
                String result = null;
                if (!verifyInitHour("pm", hourStart)) {
                    allert("pm");
                } else {
                    result = (hourStart + ":" + minuteStart);
                }

                if (!verifyFinishHour("pm", hourFinish)) {
                    allert("pm");
                } else {
                    result = result + "-" + (hourFinish + ":" + minuteFinish);
                }
                return result;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*importante.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    outputIntent.putExtra(IDs.PRIORITY, 1);
                } else {
                    outputIntent.putExtra(IDs.PRIORITY, 0);
                }
            }
        });*/
    }

    private void allert(String orario) {
        String message = null;
        if ("am".equalsIgnoreCase(orario))
            message = getString(R.string.msg_orario_inizio_non_corretto_am);
        else if ("pm".equalsIgnoreCase(orario))
            message = getString(R.string.msg_orario_inizio_non_corretto_pm);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setTitle(getString(R.string.msg_orario_non_corretto));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(getString(R.string.msg_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
