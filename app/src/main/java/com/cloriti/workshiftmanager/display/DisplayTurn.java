package com.cloriti.workshiftmanager.display;

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
import com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

public class DisplayTurn extends AppCompatActivity {

    private Turn turn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_turn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_app_upper);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);

        final Bundle inputBundle = this.getIntent().getExtras();
        String selectedDay = inputBundle.getString("SELECTED_DAY");
        AccessToDB db = new AccessToDB();
        turn = db.getTurnBySelectedDay(selectedDay, getApplicationContext());

        TextView title = (TextView) findViewById(R.id.title);

        TextView inizioMattina = (TextView) findViewById(R.id.iniziovalue);
        TextView fineMattina = (TextView) findViewById(R.id.finevalue);

        TextView inizioPomeriggio = (TextView) findViewById(R.id.iniziopomeriggio);
        TextView finePomeriggio = (TextView) findViewById(R.id.finepomeriggio);

        TextView importante = (TextView) findViewById(R.id.importante);
        TextView overValue = (TextView) findViewById(R.id.overvalue);
        TextView orevalue = (TextView) findViewById(R.id.orevalue);

        Button back = (Button) findViewById(R.id.back);

        title.setText(turn.getDatariferimento());

        if (turn.getInizioMattina() != null && turn.getFineMattina() != null && !isNull(turn.getInizioMattina(), turn.getFineMattina())) {
            inizioMattina.setText(turn.getInizioMattina());
            fineMattina.setText(turn.getFineMattina());
        } else {
            inizioMattina.setText(R.string.riposo);
            fineMattina.setText(R.string.riposo);
        }

        if (turn.getInizioPomeriggio() != null && turn.getFinePomeriggio() != null && !isNull(turn.getInizioPomeriggio(), turn.getFinePomeriggio())) {
            inizioPomeriggio.setText(turn.getInizioPomeriggio());
            finePomeriggio.setText(turn.getFinePomeriggio());
        } else {
            inizioPomeriggio.setText(R.string.riposo);
            finePomeriggio.setText(R.string.riposo);
        }

        orevalue.setText(Double.toString(turn.getHour()));
        overValue.setText(Double.toString(turn.getOvertime()));

        if (turn.getIsImportante()) {
            importante.setTextColor(getResources().getColor(R.color.Red));
        } else {
            importante.setTextColor(getResources().getColor(R.color.transparent));
        }
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cancella = (CheckBox) findViewById(R.id.delete);
                if (cancella.isChecked()) {
                    AccessToDB db = new AccessToDB();
                    db.deleteTurnAndUpdateWeek(turn, getApplicationContext());
                }
                finish();

            }
        });
    }

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
