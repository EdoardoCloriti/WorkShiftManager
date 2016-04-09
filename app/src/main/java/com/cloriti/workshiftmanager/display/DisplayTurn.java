package com.cloriti.workshiftmanager.display;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

/**
 * Activity per la visualizzazione del turno registrato in una determinata data
 * è possibile anche eliminare il turno
 *
 * @Athor edoardo.cloriti@studio.unibo.it
 */
public class DisplayTurn extends AppCompatActivity {

    private Turn turn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_turn);
        //Gestione della toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_app_upper);
        toolbar.setLogo(R.mipmap.ic_insert_invitation_black_48dp);
        setSupportActionBar(toolbar);
        //Gestione del navigation Button della toolbar
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        //recupero la data passata in input tramite l'intent creato dal chiamante
        final Bundle inputBundle = this.getIntent().getExtras();
        String selectedDay = inputBundle.getString("SELECTED_DAY");
        AccessToDB db = new AccessToDB();
        //recupero il turno dal DB tramite la data
        turn = db.getTurnBySelectedDay(selectedDay, getApplicationContext());

        TextView title = (TextView) findViewById(R.id.title);

        TextView inizioMattina = (TextView) findViewById(R.id.iniziovalue);
        TextView fineMattina = (TextView) findViewById(R.id.finevalue);

        TextView inizioPomeriggio = (TextView) findViewById(R.id.iniziopomeriggio);
        TextView finePomeriggio = (TextView) findViewById(R.id.finepomeriggio);

        TextView importante = (TextView) findViewById(R.id.importante);
        TextView overValue = (TextView) findViewById(R.id.overvalue);
        TextView orevalue = (TextView) findViewById(R.id.orevalue);

        title.setText(selectedDay);

        //Controllo se è settato è presente un turno di mattina altrimenti lo setto "Di Riposo"
        if (turn.getInizioMattina() != null && turn.getFineMattina() != null && !isNull(turn.getInizioMattina(), turn.getFineMattina())) {
            inizioMattina.setText(turn.getInizioMattina());
            fineMattina.setText(turn.getFineMattina());
        } else {
            inizioMattina.setText(R.string.riposo);
            fineMattina.setText(R.string.riposo);
        }

        //Controllo se è settato è presente un turno di pomeriggio altrimenti lo setto "Di Riposo
        if (turn.getInizioPomeriggio() != null && turn.getFinePomeriggio() != null && !isNull(turn.getInizioPomeriggio(), turn.getFinePomeriggio())) {
            inizioPomeriggio.setText(turn.getInizioPomeriggio());
            finePomeriggio.setText(turn.getFinePomeriggio());
        } else {
            inizioPomeriggio.setText(R.string.riposo);
            finePomeriggio.setText(R.string.riposo);
        }

        //estraggo le date e le scrivo
        orevalue.setText(Double.toString(turn.getHour()));
        overValue.setText(Double.toString(turn.getOvertime()));

        //controllo sull'importanza alla giornate , se è settata appare la scritta importante in rosso altrimenti in trasparente
        if (turn.getIsImportante()) {
            importante.setTextColor(getResources().getColor(R.color.Red));
        } else {
            importante.setTextColor(getResources().getColor(R.color.transparent));
        }
    }

    /**
     * Durante la chiusura dell'activity si controlla se il CheckBox elimina è impostato
     * in caso affermativo si elimina il turno dal databse aggiornando la settimana
     */
    private void close() {
        int result = 7;
        CheckBox cancella = (CheckBox) findViewById(R.id.delete);
        Intent output = new Intent();
        if (cancella.isChecked()) {
            turn.setIsOnlyDelete(true);
        }
        if (turn.getDataRierimentoDateStr() == null)
            result = 0;
        output = Turn.intentByTurn(output, turn);
        setResult(result, output);
        finish();
    }

    /**
     * Controlla se i due valori sono nulli per l'applicazione "null:null"
     *
     * @param value1
     * @param value2
     * @return
     */
    private boolean isNull(String value1, String value2) {
        return "null:null".equalsIgnoreCase(value1) && "null:null".equalsIgnoreCase(value2);
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
            WorkshiftManagerTutorial.showWorkShiftManagerTurorial(DisplayTurn.this, WorkshiftManagerTutorial.DISPLAY);
        }


        return super.onOptionsItemSelected(item);
    }
}
