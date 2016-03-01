package com.cloriti.workshiftmanager.manage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.widget.Toast;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerReceiver;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.util.IDs;
import com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.util.SelectHours;
import com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateWorkShift extends AppCompatActivity {

    private final static String PATTERN = "dd/MM/yyyy";
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private Turn turn = new Turn();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_shift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Bundle inputBundle = this.getIntent().getExtras();
        TextView title = (TextView) findViewById(R.id.title_add_turn_menu);
        Button insertMattina = (Button) findViewById(R.id.inserisci_mattina);
        Button insertPomeriggio = (Button) findViewById(R.id.inserisci_pomeriggio);
        Button submit = (Button) findViewById(R.id.submit);
        Button back = (Button) findViewById(R.id.back);
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
                selectTurnCalendar.putExtra(IDs.PART_OF_DAY, "am");
                startActivityForResult(selectTurnCalendar, IDs.SelectTurn);
            }

        });
        // gestione dell'inserimento del pomeriggio
        insertPomeriggio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent selectTurnCalendar = new Intent(getApplicationContext(), SelectHours.class);
                selectTurnCalendar.putExtra(IDs.PART_OF_DAY, "pm");
                startActivityForResult(selectTurnCalendar, 2);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox priority = (CheckBox) findViewById(R.id.priority);
                turn.setIsImportante(priority.isChecked());
                turn.setHour();
                AccessToDB db = new AccessToDB();
                db.insertTurn(turn, getApplicationContext());
                if (db.existPropery(Property.NOTIFICA, getApplicationContext()) != 0) {
                    if (!isNull(turn.getInizioMattina()))
                        generateRememberNotify(turn.getInizioMattinaH(), turn.getInizioMattinaM(), turn.getDataRierimentoDateStr());
                    if (!isNull(turn.getInizioPomeriggio()))
                        generateRememberNotify(turn.getInizioPomeriggioH(), turn.getInizioPomeriggioM(), turn.getDataRierimentoDateStr());
                }
                turn = null;
                finish();
            }
        });
        // gestione del tasto back
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                turn = null;
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent out = new Intent();
        out.putExtra("back", 1);
        setResult(1, out);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 2) {
            Bundle result = data.getExtras();
            if (!result.containsKey("back")) {
                if (result.getString(IDs.ORARIO_MATTINA) != null) {
                    String[] orarioMattina = result.getString(IDs.ORARIO_MATTINA).split("-");
                    turn.setIniziotMattina(orarioMattina[0]);
                    turn.setFineMattina(orarioMattina[1]);
                }

                if (result.getString(IDs.ORARIO_POMERIGGIO) != null) {
                    String[] orarioPomeriggio = result.getString(IDs.ORARIO_POMERIGGIO).split("-");
                    turn.setIniziotPomeriggio(orarioPomeriggio[0]);
                    turn.setFinePomeriggio(orarioPomeriggio[1]);
                }
            }
        }
    }

    public void generateRememberNotify(Integer ore, Integer minuti, String data) {
        //Gestione delle notifiche giornaliere
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), WorkShiftManagerReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        // Set the alarm to start at some time.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Checking whether current hour is over 15
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        try {
            calendar.setTime(sdf.parse(data));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Impossibile creare la notifica", Toast.LENGTH_SHORT);
            return;
        }

        calendar.set(Calendar.HOUR_OF_DAY, ore);
        calendar.set(Calendar.MINUTE, minuti);
        calendar.add(Calendar.MINUTE, -30);

        // every day
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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
            WorkshiftManagerTutorial.showWorkShiftManagerTurorial(CreateWorkShift.this, "CreateWorkShift");
        }


        return super.onOptionsItemSelected(item);
    }

    private boolean isNull(String orario) {
        return "null:null".equals(orario) ? true : false;
    }
}
