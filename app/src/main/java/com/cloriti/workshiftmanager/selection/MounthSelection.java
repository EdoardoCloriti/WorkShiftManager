package com.cloriti.workshiftmanager.selection;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.util.Week;
import com.cloriti.workshiftmanager.util.db.AccessToDB;

import java.text.DateFormatSymbols;
import java.util.List;

public class MounthSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mounth_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button submit = (Button) findViewById(R.id.submit);
        Button back = (Button) findViewById(R.id.back);

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText mese = (EditText) findViewById(R.id.mese);
                EditText anno = (EditText) findViewById(R.id.anno);
                int month = Integer.parseInt(mese.getText().toString());
                int year = Integer.parseInt(anno.getText().toString());
                setContentView(R.layout.activity_display_mouth);
                Button back = (Button) findViewById(R.id.back);
                TextView meseAttuale = (TextView) findViewById(R.id.mese_attuale);
                TextView ore = (TextView) findViewById(R.id.ore);
                TextView straordinari = (TextView) findViewById(R.id.straordinari);
                meseAttuale.setText(new DateFormatSymbols().getMonths()[month - 1] + " " + year);
                ore.setText(calculateHour(month, year));
                straordinari.setText(calculateOvertime(month, year));
                back.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String calculateHour(int mounth, int year) {
        AccessToDB db = new AccessToDB();
        List<Week> weeks = db.getMounth(mounth, year, getApplicationContext());
        double hours = 0;
        for (Week week : weeks) {
            hours = hours + week.getHour();
        }
        return Double.toString(hours);

    }

    private String calculateOvertime(int mounth, int year) {
        AccessToDB db = new AccessToDB();
        List<Week> weeks = db.getMounth(mounth, year, getApplicationContext());
        double hours = 0;
        for (Week week : weeks) {
            hours = hours + week.getExtraHour();
        }
        return Double.toString(hours);

    }
    }


