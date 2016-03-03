package com.cloriti.workshiftmanager.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.util.IDs;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

import java.util.Calendar;

/**
 * Activity per la selezione dell'orario del turno sia per mattina che per pomeriggio
 * essa è parametrizzata con "am" o "pm" in riferimento alla parte della giornata a cui si riferisce
 *
 * @Author edoardo.cloriti@studio.unibo.it
 */
public class SelectHours extends AppCompatActivity {

    private Intent outputIntent = null;
    private String h;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Gestione della toolbar
        setContentView(R.layout.activity_select_hours);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(R.string.title_app_upper);
        setSupportActionBar(toolbar);
        //gestione delNavigation icon back
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //estrazione dell'imput relativo alla parte di giornata di riferimento
        Bundle bundle = this.getIntent().getExtras();
        outputIntent = new Intent(getApplicationContext(), CreateWorkShift.class);
        h = bundle.getString("part-of-day");

        final TextView titleOriario = (TextView) findViewById(R.id.title_orario);
        final TimePicker orarioInizio = (TimePicker) findViewById(R.id.inizio);
        final TimePicker orarioFine = (TimePicker) findViewById(R.id.fine);

        //setting dell'orario di inzio metodi deprecati in Android L ma ancora funzionanti
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
    }

    /**
     * Metodo per il salvataggio e la sottomissione dei dati
     */
    private void submit() {
        TimePicker orarioInizio = (TimePicker) findViewById(R.id.inizio);
        TimePicker orarioFine = (TimePicker) findViewById(R.id.fine);

        //salvataggio degli orari h e m immessi
        Integer hourStart = orarioInizio.getHour();
        Integer minuteStart = orarioInizio.getMinute();
        Integer hourFinish = orarioFine.getHour();
        Integer minuteFinish = orarioFine.getMinute();
        //inserimento della parte di giornata nel risultato
        outputIntent.putExtra(IDs.PART_OF_DAY, h);

        //validazione dell'orario della mattina
        String am = manageAM(hourStart, minuteStart, hourFinish, minuteFinish);
        //validazione dell'orario pomeridiano
        String pm = managePM(hourStart, minuteStart, hourFinish, minuteFinish);
        //controllo delle validazione
        if ("am".equalsIgnoreCase(h) && am != null) {
            //se l'orario immesso è relativo alla mattina e la validazione è stata effettuata con successo
            //si inseerisce il turno nel risultato e si restituisce il controllo al chiamante
            outputIntent.putExtra(IDs.ORARIO_MATTINA, am);
            setResult(2, outputIntent);
            finish();
        } else if ("pm".equalsIgnoreCase(h) && pm != null) {
            //se l'orario immesso è relativo del pomeriggio e la validazione è stata effettuata con successo
            //si inseerisce il turno nel risultato e si restituisce il controllo al chiamante
            outputIntent.putExtra(IDs.ORARIO_POMERIGGIO, pm);
            setResult(2, outputIntent);
            finish();
        } else
            //si gestisce l'errore con un alert
            alert();
    }

    /**
     * Metodo per la gestione dell'orario del turno caso 'AM'
     *
     * @param hourStart
     * @param minuteStart
     * @param hourFinish
     * @param minuteFinish
     * @return
     */
    private String manageAM(Integer hourStart, Integer minuteStart, Integer hourFinish, Integer minuteFinish) {
        if (!validatTimetable(hourStart, minuteStart, hourFinish, minuteFinish))
            return null;
        else if (!validateStartHour("am", hourStart))
            return null;
        else
            return (hourStart + ":" + minuteStart) + "-" + (hourFinish + ":" + minuteFinish);
    }


    /**
     * Metodo per la gestione dell'orario del turno caso 'PM'
     *
     * @param hourStart
     * @param minuteStart
     * @param hourFinish
     * @param minuteFinish
     * @return
     */
    private String managePM(Integer hourStart, Integer minuteStart, Integer hourFinish, Integer minuteFinish) {
        if (!validatTimetable(hourStart, minuteStart, hourFinish, minuteFinish))
            return null;
        else if (!validateStartHour("pm", hourStart))
            return null;
        else
            return (hourStart + ":" + minuteStart) + "-" + (hourFinish + ":" + minuteFinish);
    }

    /**
     * metodo per la creazione dell'alert nel caso in cui l'orario immesso non è corretto
     */
    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectHours.this);
        builder.setTitle(this.getString(R.string.title_activity_select_hours));
        builder.setMessage("Orario inserito non valido si prega di ricompilare");
        builder.setIcon(R.drawable.ic_error_black_48dp);
        builder.setPositiveButton(this.getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog d = builder.create();
        d.show();
    }

    /**
     * metodo per la validazione dell'orario di inzio e di fine turno
     * controlla che l'orario di inizio non sia dopo o sia uguale a quello di fine
     *
     * @param oraI
     * @param minI
     * @param oraf
     * @param minF
     * @return
     */
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

    /**
     * validazione dell'orario di inizio
     * controlla che esso si coerente con la parte del giorno gestita
     *
     * @param orario
     * @param hour
     * @return
     */
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

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(), WorkShiftManagerSetting.class);
                startActivity(i);
                break;
            case R.id.action_help:
                WorkshiftManagerTutorial.showWorkShiftManagerTurorial(SelectHours.this, "CreateWorkShift");
                break;
            case R.id.action_submit:
                submit();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
