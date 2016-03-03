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

/**
 * Activity per la selezione del giorno da gestire in qualche modo
 * univo entry point per la maggior parte deglli use-case
 *
 * @Author edoardo.cloriti@studio.unibo.it
 */
public class ManageCalendar extends AppCompatActivity {
    /**
     * Use-case gestiti dalla Activity
     */
    private static final String MANAGE = "ManageWorkShift";
    private static final String DISPLAY = "DisplayWorkShift";
    private static final String OVERTIME = "AddOvertime";
    private static final String STARLING = "StarlingHours";
    private static final String DISPLAY_WEEK = "DisplaySettimana";
    private static final int MONDAY = 1;
    /**
     * Use-case passato come parametro dal chiamante
     */
    private String useCaseCalling = null;
    private Dialog d;
    private Turn turn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_calendar);
        //Gestione della toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(R.string.title_app_upper);
        //Gestione della NavigationIcon back
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //estrazione del parametro di input - use-case da eseguire
        Bundle inputParam = this.getIntent().getExtras();
        useCaseCalling = inputParam.getString("USE_CASE");
        //gestione di use-case null ritorno al chiamante
        if (useCaseCalling == null) {
            Toast.makeText(getApplicationContext(), "Impossibile avviare il calendario dato che non è specificato un chiamante", Toast.LENGTH_LONG).show();
            finish();
        }
        //setting del CalendarView
        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
        setCalendar(calendar);
        /**
         * Gestione della selezione del giorno
         */
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                /**
                 * use case manage preparazione dei parametri e invocazione dell'activity 'CreateWorkShift'
                 */
                if (MANAGE.equals(useCaseCalling)) {
                    Calendar sysDate = Calendar.getInstance();
                    sysDate.add(Calendar.DAY_OF_MONTH, -3);

                    Intent add = new Intent(getApplicationContext(), CreateWorkShift.class);

                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(selected.getTime()));
                    //Gestione dei gionrni non selezionabili sysDate - 3
                    if (sysDate.before(selected)) {
                        Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                        //creazione dei parametri per la chiamata a 'CreateWorkShift'
                        add.putExtra(IDs.DATA, selectedDay);
                        add.putExtra(IDs.WEEK_ID, selected.get(Calendar.WEEK_OF_YEAR));
                        add.putExtra(IDs.YEAR, year);
                        add.putExtra(IDs.MONTH, month + 1);
                        startActivity(add);
                    } else {
                        //gestione del giorno non selezionabile tramite Toast
                        Toast.makeText(getApplicationContext(), "Giorno selezionato non disponibile", Toast.LENGTH_SHORT).show();
                    }
                }
                /**
                 * use case display preparazione dei parametri per la chiamata all'activity 'DisplayTurn'
                 */
                else if (DISPLAY.equals(useCaseCalling)) {
                    Intent i = new Intent(getApplicationContext(), DisplayTurn.class);
                    GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(day.getTime()));
                    Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                    //inserimento della data che si vuole visualizzare
                    i.putExtra("SELECTED_DAY", selectedDay);
                    startActivity(i);
                }
                /**
                 * use case overtime - gestione dell'inserimento degli straordinari tramite pop up
                 */
                else if (OVERTIME.equals(useCaseCalling)) {
                    GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(day.getTime()));
                    Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                    createDialogOvertime(selectedDay);
                }
                /**
                 * use case starling - gestione dell'inserimento dello storno tramite pop up
                 */
                else if (STARLING.equals(useCaseCalling)) {
                    GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(day.getTime()));
                    Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                    createDialogStarling(selectedDay);
                }
                /**
                 * use case display_week preparazione dei parametri e invocazione della activity 'DisplayHourWeek'
                 */
                else if (DISPLAY_WEEK.equals(useCaseCalling)) {
                    GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);
                    Intent i = new Intent(getApplicationContext(), DisplayHourWeek.class);
                    SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                    String selectedDay = new String(sdf.format(day.getTime()));
                    Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(day.getTime());
                    int mounth = cal.get(Calendar.WEEK_OF_YEAR);
                    //TODO:cambiare le key dei parametri
                    i.putExtra("MOUNTH", mounth);
                    i.putExtra("YEAR", year);
                    startActivity(i);
                }
            }
        });
    }

    /**
     * metodo per il setting del calelndario
     *
     * @param calendar
     */
    private void setCalendar(CalendarView calendar) {
        calendar.setShowWeekNumber(false);
        calendar.setFirstDayOfWeek(MONDAY);
    }

    /**
     * metodo per la creazione del dialo per l'inserimento degli straordinari
     *
     * @param selectedDate
     */
    private void createDialogOvertime(String selectedDate) {
        final AccessToDB db = new AccessToDB();
        turn = db.getTurnBySelectedDay(selectedDate, getApplicationContext());
        if (turn.isNull()) {
            turn = new Turn();
            turn.setDatariferimento(selectedDate);
        }
        //gestione del dialog
        d = new Dialog(ManageCalendar.this);
        d.setTitle("Prova");
        d.setContentView(R.layout.dialog_overtime);
        d.show();
        //setting dei button usati
        Button submit = (Button) d.findViewById(R.id.submit_dialog);
        Button back = (Button) d.findViewById(R.id.back_dialog);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recupero delle ore e dei minuti inseriti
                EditText ore = (EditText) d.findViewById(R.id.ore);
                EditText minuti = (EditText) d.findViewById(R.id.minuti);
                double hours = 0;
                double min = 0;
                //validazione dei delle ore e dei minuti e trasformazione in decimalli
                min = validateMinutes(getInteger(minuti.getText().toString()));
                hours = validateHours(getInteger(ore.getText().toString()));
                //calcolo degli straordinari
                double overtime = min + hours;
                overtime = turn.getOvertime() + overtime;
                turn.setOvertime(overtime);
                //insert-update del turno
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

    /**
     * metodo per la creazione del dialo per l'inserimento dello storno ore
     *
     * @param selectedDate
     */
    private void createDialogStarling(String selectedDate) {
        final AccessToDB db = new AccessToDB();
        //controlo dell'esistenza del turno
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
                //recupero delle ore e dei minuti inseriti
                EditText ore = (EditText) d.findViewById(R.id.ore);
                EditText minuti = (EditText) d.findViewById(R.id.minuti);
                double hours = 0;
                double min = 0;
                //validazione dei delle ore e dei minuti e trasformazione in decimalli
                min = validateMinutes(getInteger(minuti.getText().toString()));
                hours = validateHours(getInteger(ore.getText().toString()));
                double overtime = min + hours;
                //calcolo dello storno
                overtime = turn.getOvertime() - overtime;
                turn.setOvertime(overtime);
                //insert-update del turno
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

    /**
     * metodo per la validazione e conversione dei minuti
     *
     * @param newVal
     * @return
     */
    private double validateMinutes(int newVal) {
        if (newVal > 60)
            alert("m", newVal);
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

    /**
     * metodo per la validazione delle ore
     *
     * @param newVal
     * @return
     */
    private int validateHours(int newVal) {
        if (newVal > 24) {
            alert("h", newVal);
            return 0;
        } else
            return newVal;
    }

    /**
     * metodo per la creazione di un alert nel caso in cui le ore inserite non siano giuste
     *
     * @param picker
     * @param value
     */
    private void alert(String picker, int value) {
        String message = null;
        if ("h".equalsIgnoreCase(picker))
            message = getString(R.string.msg_numero_ore) + value + getString(R.string.msg_orario_non_corretto) + "24";
        else if ("m".equalsIgnoreCase(picker))
            message = getString(R.string.msg_numero_minuti) + value + getString(R.string.msg_orario_non_corretto) + "60";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setTitle(getString(R.string.msg_valore_non_corretto));
        alertDialogBuilder.setIcon(R.drawable.ic_error_black_48dp);
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

    /**
     * data una stringa effettua il cast ad int nel caso in cui fallisca il cast restituisce 0
     *
     * @param str
     * @return
     */
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(), WorkShiftManagerSetting.class);
                startActivity(i);
                break;
            case R.id.action_help:
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
