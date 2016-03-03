package com.cloriti.workshiftmanager.selection;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.display.DisplayMounth;
import com.cloriti.workshiftmanager.display.DisplayYear;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

public class MultiSelectionMenu extends AppCompatActivity {

    private static final int MONDAY = 1;
    private Dialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_selection_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_app_upper);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Portait (Verticale)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button manageWorkShift = (Button) findViewById(R.id.insertT);
        Button displayTurn = (Button) findViewById(R.id.visualTurn);
        Button addOvertime = (Button) findViewById(R.id.addextraordinary);
        Button displayMounth = (Button) findViewById(R.id.displayH);
        Button displayYear = (Button) findViewById(R.id.displayY);
        Button displayOreSettimali = (Button) findViewById(R.id.oreSettimanali);
        Button starlingHour = (Button) findViewById(R.id.storno);
        Button setting = (Button) findViewById(R.id.setting);

        manageWorkShift.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ManageCalendar.class);
                i.putExtra("USE_CASE", "ManageWorkShift");
                startActivity(i);
            }
        });

        displayTurn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ManageCalendar.class);
                i.putExtra("USE_CASE", "DisplayWorkShift");
                startActivity(i);
            }
        });

        addOvertime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ManageCalendar.class);
                i.putExtra("USE_CASE", "AddOvertime");
                startActivity(i);
            }
        });
        starlingHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ManageCalendar.class);
                i.putExtra("USE_CASE", "StarlingHours");
                startActivity(i);
            }
        });
        displayYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DisplayYear.class);
                startActivity(i);
            }
        });

        displayMounth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                d = new Dialog(MultiSelectionMenu.this);
                d.setTitle("Prova");
                d.setContentView(R.layout.dialog_select_mounth);
                d.show();
                Button submit = (Button) d.findViewById(R.id.submit_dialog);
                Button back = (Button) d.findViewById(R.id.back_dialog);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText mese = (EditText) d.findViewById(R.id.mese);
                        EditText anno = (EditText) d.findViewById(R.id.anno);
                        int month = Integer.parseInt(mese.getText().toString());
                        int year = Integer.parseInt(anno.getText().toString());
                        Intent display = new Intent(getApplicationContext(), DisplayMounth.class);
                        display.putExtra("MONTH", month);
                        display.putExtra("YEAR", year);
                        startActivity(display);
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
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), WorkShiftManagerSetting.class);
                startActivity(i);
            }
        });

        displayOreSettimali.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ManageCalendar.class);
                i.putExtra("USE_CASE", "DisplaySettimana");
                startActivity(i);
            }
        });


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
                WorkshiftManagerTutorial.showWorkShiftManagerTurorial(MultiSelectionMenu.this, "MultiSelectionMenu");
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}


