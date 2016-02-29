package com.cloriti.workshiftmanager.selection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.display.DisplayWorkShift;
import com.cloriti.workshiftmanager.manage.ManageWorkShift;

public class MultiSelectionMenu extends AppCompatActivity {

    private static final int MONDAY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_selection_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        setContentView(R.layout.activity_multi_selection_menu);

        Button manageWorkShift = (Button) findViewById(R.id.insertT);
        Button displayTurn = (Button) findViewById(R.id.visualTurn);
        Button addOvertime = (Button) findViewById(R.id.addextraordinary);
        Button back = (Button) findViewById(R.id.backbutton);
        Button displayMounth = (Button) findViewById(R.id.displayH);
        Button displayYear = (Button) findViewById(R.id.displayY);
        Button displayOreSettimali=(Button) findViewById(R.id.oreSettimanali);
        Button starlingHour = (Button) findViewById(R.id.storno);
        Button setting = (Button) findViewById(R.id.setting);

        manageWorkShift.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ManageWorkShift.class);
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        displayTurn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DisplayWorkShift.class);
                startActivity(i);
            }
        });

        addOvertime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddOvertime.class);
                startActivity(i);
            }
        });
        starlingHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StarlingHours.class);
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
                Intent i = new Intent(getApplicationContext(), MounthSelection.class);
                startActivity(i);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), WorkShiftManagerSetting.class);
                startActivity(i);
            }
        });

        //TODO:
        displayOreSettimali.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DisplaySettimana.class);
                startActivity(i);
            }
        });


    }
    }

}
