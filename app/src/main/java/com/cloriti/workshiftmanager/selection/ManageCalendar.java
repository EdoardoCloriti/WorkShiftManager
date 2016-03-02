package com.cloriti.workshiftmanager.selection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.display.DisplayHourWeek;
import com.cloriti.workshiftmanager.display.DisplayTurn;
import com.cloriti.workshiftmanager.manage.CreateWorkShift;
import com.cloriti.workshiftmanager.util.IDs;
import com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ManageCalendar extends AppCompatActivity {

    private static final int MONDAY = 1;
    private static final String MANAGE = "ManageWorkShift";
    private static final String DISPLAY = "DisplayWorkShift";
    private static final String OVERTIME = "AddOvertime";
    private static final String STARLING = "StarlingHours";
    private static final String DISPLAY_WEEK = "DisplaySettimana";
    private String useCaseCalling = null;
    private Dialog d;
    private Turn turn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle inputParam = this.getIntent().getExtras();
        useCaseCalling = inputParam.getString("USE_CASE");
        if (useCaseCalling == null) {
            Toast.makeText(getApplicationContext(), "Impossibile avviare il calendario dato che non è specificato un chiamante", Toast.LENGTH_LONG).show();
            finish();
        }
        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
        Button back = (Button) findViewById(R.id.back);

        setCalendar(calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if (MANAGE.equals(useCaseCalling)) {
                    Calendar sysDate = Calendar.getInstance();
                    sysDate.add(Calendar.DAY_OF_MONTH, -3);

                    Intent add = new Intent(getApplicationContext(), CreateWorkShift.class);
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(selected.getTime()));

                    if (sysDate.before(selected)) {
                        Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();

                        add.putExtra(IDs.DATA, selectedDay);
                        add.putExtra(IDs.WEEK_ID, selected.get(Calendar.WEEK_OF_YEAR));
                        add.putExtra(IDs.YEAR, year);
                        add.putExtra(IDs.MONTH, month + 1);
                        startActivity(add);
                    } else {
                        Toast.makeText(getApplicationContext(), "Giorno selezionato non disponibile", Toast.LENGTH_SHORT).show();
                    }
                } else if (DISPLAY.equals(useCaseCalling)) {
                    Intent i = new Intent(getApplicationContext(), DisplayTurn.class);
                    GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(day.getTime()));
                    Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                    i.putExtra("SELECTED_DAY", selectedDay);
                    startActivity(i);
                } else if (OVERTIME.equals(useCaseCalling)) {
                    GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(day.getTime()));
                    Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                    createDialogOvertime(selectedDay);
                } else if (STARLING.equals(useCaseCalling)) {
                    GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(day.getTime()));
                    Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                    createDialogStarling(selectedDay);
                } else if (DISPLAY_WEEK.equals(useCaseCalling)) {
                    GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);
                    Intent i = new Intent(getApplicationContext(), DisplayHourWeek.class);
                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(day.getTime()));
                    Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(day.getTime());
                    int mounth = cal.get(Calendar.WEEK_OF_YEAR);
                    i.putExtra("MOUNTH", mounth);
                    i.putExtra("YEAR", year);
                    startActivity(i);
                }
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

    private void createDialogOvertime(String selectedDate) {
        final AccessToDB db = new AccessToDB();
        turn = db.getTurnBySelectedDay(selectedDate, getApplicationContext());
        if (turn.isNull()) {
            turn = new Turn();
            turn.setDatariferimento(selectedDate);
        }
        d = new Dialog(ManageCalendar.this);
        d.setTitle("Prova");
        d.setContentView(R.layout.dialog_overtime);
        d.show();
        Button submit = (Button) d.findViewById(R.id.submit_dialog);
        Button back = (Button) d.findViewById(R.id.back_dialog);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ore = (EditText) d.findViewById(R.id.ore);
                EditText minuti = (EditText) d.findViewById(R.id.minuti);
                double hours = 0;
                double min = 0;
                min = validateMinutes(getInteger(minuti.getText().toString()));
                hours = validateHours(getInteger(ore.getText().toString()));
                double overtime = min + hours;
                overtime = turn.getOvertime() + overtime;
                turn.setOvertime(overtime);
                db.insertTurn(turn, getApplicationContext());
                turn = null;
                d.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                d.dismiss();
            }
        });
    }

    private void createDialogStarling(String selectedDate) {
        final AccessToDB db = new AccessToDB();
        turn = db.getTurnBySelectedDay(selectedDate, getApplicationContext());
        if (turn.isNull()) {
            turn = new Turn();
            turn.setDatariferimento(selectedDate);
        }
        d = new Dialog(ManageCalendar.this);
        d.setTitle("Prova");
        d.setContentView(R.layout.dialog_overtime);
        d.show();
        Button submit = (Button) d.findViewById(R.id.submit_dialog);
        Button back = (Button) d.findViewById(R.id.back_dialog);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ore = (EditText) d.findViewById(R.id.ore);
                EditText minuti = (EditText) d.findViewById(R.id.minuti);
                double hours = 0;
                double min = 0;
                min = validateMinutes(getInteger(minuti.getText().toString()));
                hours = validateHours(getInteger(ore.getText().toString()));
                double overtime = min + hours;
                overtime = turn.getOvertime() - overtime;
                turn.setOvertime(overtime);
                db.insertTurn(turn, getApplicationContext());
                d.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                d.dismiss();
            }
        });
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

    private int getInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Throwable t) {
            return 0;
        }
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
            if (MANAGE.equals(useCaseCalling)) {
                WorkshiftManagerTutorial.showWorkShiftManagerTurorial(ManageCalendar.this, WorkshiftManagerTutorial.MANAGE_WORKSHIFT);
            }
            if (DISPLAY.equalsIgnoreCase(useCaseCalling)) {
                WorkshiftManagerTutorial.showWorkShiftManagerTurorial(ManageCalendar.this, WorkshiftManagerTutorial.DISPLAY);
            }
            if (OVERTIME.equalsIgnoreCase(useCaseCalling)) {
                WorkshiftManagerTutorial.showWorkShiftManagerTurorial(ManageCalendar.this, WorkshiftManagerTutorial.ADD_OVERTIME);
            }
            if (STARLING.equalsIgnoreCase(useCaseCalling)) {
                WorkshiftManagerTutorial.showWorkShiftManagerTurorial(ManageCalendar.this, WorkshiftManagerTutorial.STARLING_HOURS);
            }
            if (DISPLAY_WEEK.equalsIgnoreCase(useCaseCalling)) {
                WorkshiftManagerTutorial.showWorkShiftManagerTurorial(ManageCalendar.this, WorkshiftManagerTutorial.DISPLAY_WEEK);
            }
        }


        return super.onOptionsItemSelected(item);
    }

}
