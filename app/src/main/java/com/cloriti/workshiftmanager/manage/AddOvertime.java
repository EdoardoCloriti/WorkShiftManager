package com.cloriti.workshiftmanager.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.util.IDs;
import com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.util.db.AccessToDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class AddOvertime extends AppCompatActivity {

    private static final int MONDAY = 1;

    List<Turn> turns = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_overtime);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
        Button submit = (Button) findViewById(R.id.submit);
        Button back = (Button) findViewById(R.id.back);

        setCalendar(calendar);
        turns = new ArrayList<Turn>();

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Intent select = new Intent(getApplicationContext(), SelectOvertime.class);
                GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                String selectedDay = new String(sdf.format(day.getTime()));
                Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                select.putExtra(IDs.DATA, selectedDay);
                startActivityForResult(select, 1);
            }

        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AccessToDB access = new AccessToDB();
                int n = access.updateTurn(turns, getApplicationContext());
                turns = null;
                finish();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setCalendar(CalendarView calendar) {
        calendar.setShowWeekNumber(false);
        calendar.setFirstDayOfWeek(MONDAY);
    }


    private double validateMinutes(int newVal) {
        if (newVal > 60)
            allert("m", newVal);
        else {
            if (newVal < 15)
                return 0;
            else if (newVal < 30)
                return 0.25;
            else if (newVal < 45)
                return 0.5;
            else if (newVal < 60)
                return 0.75;
        }
        return 0;
    }


    private int validateHours(int newVal) {
        if (newVal > 60) {
            allert("h", newVal);
            return 0;
        } else
            return newVal;
    }

    private void allert(String picker, int value) {
        String message = null;
        if ("h".equalsIgnoreCase(picker))
            message = getString(R.string.msg_numero_ore) + value + getString(R.string.msg_orario_non_corretto) + "24";
        else if ("m".equalsIgnoreCase(picker))
            message = getString(R.string.msg_numero_minuti) + value + getString(R.string.msg_orario_non_corretto) + "60";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setTitle(getString(R.string.msg_valore_non_corretto));
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

    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        Bundle bundle = arg2.getExtras();
        if (!bundle.containsKey("back")) {
            String referencesDate = null;
            if (bundle.containsKey(IDs.DATA))
                referencesDate = bundle.getString(IDs.DATA);
            double overtime = bundle.getDouble(IDs.OVERTIME);
            AccessToDB db = new AccessToDB();
            Turn turn = db.getTurnBySelectedDay(referencesDate, getApplicationContext());
            overtime = overtime + turn.getOvertime();
            turn.setOvertime(overtime);
            turns.add(turn);
        }
    }

}
