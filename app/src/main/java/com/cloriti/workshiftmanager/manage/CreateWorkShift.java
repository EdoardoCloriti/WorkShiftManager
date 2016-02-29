package com.cloriti.workshiftmanager.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cloriti.workshiftmanager.util.IDs;
import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.util.SelectHours;

public class CreateWorkShift extends AppCompatActivity {

    private Intent outputIntent = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_shift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Bundle inputBundle = this.getIntent().getExtras();
        outputIntent = new Intent(getApplicationContext(), ManageWorkShift.class);
        TextView title = (TextView) findViewById(R.id.title_add_turn_menu);
        Button insertMattina = (Button) findViewById(R.id.inserisci_mattina);
        Button insertPomeriggio = (Button) findViewById(R.id.inserisci_pomeriggio);
        Button back = (Button) findViewById(R.id.back);
        // gestione del titolo della pagina -> default senza data... inserimento della data di riferimento
        outputIntent.putExtra(IDs.DATA, inputBundle.getString(IDs.DATA));
        outputIntent.putExtra(IDs.WEEK_ID, inputBundle.getInt(IDs.WEEK_ID));
        outputIntent.putExtra(IDs.YEAR, inputBundle.getInt(IDs.YEAR));
        outputIntent.putExtra(IDs.MONTH, inputBundle.getInt(IDs.MONTH));
        title.setText(getString(R.string.title_turn_menu) + "\r\n" + inputBundle.getString(IDs.DATA));
        // gestione dell'inserimento della mattina
        insertMattina.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent selectTurnCalendar = new Intent(getApplicationContext(), SelectHours.class);
                selectTurnCalendar.putExtra(IDs.PART_OF_DAY, "am");
                outputIntent.putExtra(IDs.PART_OF_DAY, "am");
                startActivityForResult(selectTurnCalendar, IDs.SelectTurn);
            }

        });
        // gestione dell'inserimento del pomeriggio
        insertPomeriggio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent selectTurnCalendar = new Intent(getApplicationContext(), SelectHours.class);
                selectTurnCalendar.putExtra(IDs.PART_OF_DAY, "pm");
                outputIntent.putExtra(IDs.PART_OF_DAY, "pm");
                startActivityForResult(selectTurnCalendar, 2);
            }
        });
        // gestione del tasto back
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox priority = (CheckBox) findViewById(R.id.priority);
                outputIntent.putExtra(IDs.PRIORITY, priority.isChecked() ? 1 : 0);
                setResult(1, outputIntent);
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
                    outputIntent.putExtra(IDs.INIZIO_MATTINA, orarioMattina[0]);
                    outputIntent.putExtra(IDs.FINE_MATTINA, orarioMattina[1]);
                }

                if (result.getString(IDs.ORARIO_POMERIGGIO) != null) {
                    String[] orarioPomeriggio = result.getString(IDs.ORARIO_POMERIGGIO).split("-");
                    outputIntent.putExtra(IDs.INIZIO_POMERIGGIO, orarioPomeriggio[0]);
                    outputIntent.putExtra(IDs.FINE_POMERIGGIO, orarioPomeriggio[1]);
                }
                outputIntent.putExtra(IDs.PRIORITY, result.getBoolean(IDs.PRIORITY));
            }
        }
    }

}
