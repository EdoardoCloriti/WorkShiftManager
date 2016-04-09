package com.cloriti.workshiftmanager.manage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.selection.ManageCalendar;
import com.cloriti.workshiftmanager.util.IDs;
import com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.util.calendar.GoogleCalendarManager;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

/**
 * Activity per la creazione e inserimento dei turni data una data specifica
 *
 * @Author edoardo.cloriti@studio.unibo.it
 */
public class CreateWorkShift extends AppCompatActivity {

    private final static String PATTERN = "dd/MM/yyyy";
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private Turn turn = new Turn();
    private Intent outputIntent = null;


    private GoogleCalendarManager calendarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_shift);
        //gestione della toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_insert_invitation_black_48dp);
        toolbar.setTitle(R.string.title_app_upper);
        setSupportActionBar(toolbar);
        //gestione del NavigarionIcon
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        outputIntent = new Intent(getApplicationContext(), ManageCalendar.class);
        final Bundle inputBundle = this.getIntent().getExtras();
        TextView title = (TextView) findViewById(R.id.title_add_turn_menu);
        Button insertMattina = (Button) findViewById(R.id.inserisci_mattina);
        Button insertPomeriggio = (Button) findViewById(R.id.inserisci_pomeriggio);

        // gestione del titolo della pagina -> default senza data... inserimento della data di riferimento
        turn.setDatariferimento(inputBundle.getString(IDs.DATA));
        turn.setWeekId(inputBundle.getInt(IDs.WEEK_ID));
        turn.setYear(inputBundle.getInt(IDs.YEAR));
        turn.setMounth(inputBundle.getInt(IDs.MONTH));

        title.setText(getString(R.string.title_turn_menu) + "\r\n" + inputBundle.getString(IDs.DATA));
        // gestione dell'inserimento della mattina
        insertMattina.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent selectTurnCalendar = new Intent(getApplicationContext(), SelectHours.class);
                //partrizzazione dell'invacazione dell'activity 'SelectHours' con "am" per fargli capire quale sia
                //la parte di giornata da valorizzare
                selectTurnCalendar.putExtra(IDs.PART_OF_DAY, "am");
                startActivityForResult(selectTurnCalendar, IDs.SelectTurn);
            }

        });
        // gestione dell'inserimento del pomeriggio
        insertPomeriggio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent selectTurnCalendar = new Intent(getApplicationContext(), SelectHours.class);
                //partrizzazione dell'invacazione dell'activity 'SelectHours' con "pm" per fargli capire quale sia
                //la parte di giornata da valorizzare
                selectTurnCalendar.putExtra(IDs.PART_OF_DAY, "pm");
                startActivityForResult(selectTurnCalendar, 2);
            }
        });
    }

    /**
     * metodo per la gestione della chiusura dell'activity
     */
    private void close() {
        turn = null;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Metodo per sottoscrivere il turno inserito, controllo del CheckBox relativo alla priorità
     */
    private void submit() {
        //controllo del CheckBox priorità
        CheckBox priority = (CheckBox) findViewById(R.id.priority);
        turn.setIsImportante(priority.isChecked());
        turn.setHour();
        outputIntent = Turn.intentByTurn(outputIntent, turn);
        setResult(6, outputIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(5, outputIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //gestione del risultato dell'activity 'SelectHours'
        if (resultCode == 2) {
            Bundle result = data.getExtras();
            //se il risultaato contiene "back" vuol  dire che è stato premuto il tasto "back" quindi non c'è valore di ritorno
            if (!result.containsKey("back")) {
                //salvataggio dell'orario mattutino
                if (result.getString(IDs.ORARIO_MATTINA) != null) {
                    String[] orarioMattina = result.getString(IDs.ORARIO_MATTINA).split("-");
                    turn.setIniziotMattina(orarioMattina[0]);
                    turn.setFineMattina(orarioMattina[1]);
                }
                //salvataggio dell'orario pomeridiano
                if (result.getString(IDs.ORARIO_POMERIGGIO) != null) {
                    String[] orarioPomeriggio = result.getString(IDs.ORARIO_POMERIGGIO).split("-");
                    turn.setIniziotPomeriggio(orarioPomeriggio[0]);
                    turn.setFinePomeriggio(orarioPomeriggio[1]);
                }
            }
        } else if (calendarManager != null)
            calendarManager.onActivityResult(requestCode, resultCode, data);
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

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(), WorkShiftManagerSetting.class);
                startActivity(i);
                break;
            case R.id.action_help:
                WorkshiftManagerTutorial.showWorkShiftManagerTurorial(CreateWorkShift.this, "CreateWorkShift");
                break;
            case R.id.action_submit:
                submit();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
