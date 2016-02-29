package com.cloriti.workshiftmanager.selection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.display.DisplayMounth;
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
                Intent display = new Intent(getApplicationContext(), DisplayMounth.class);
                display.putExtra("MONTH", month);
                display.putExtra("YEAR", year);
                startActivity(display);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}


