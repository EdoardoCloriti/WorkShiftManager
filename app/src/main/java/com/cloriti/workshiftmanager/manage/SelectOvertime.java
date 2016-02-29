package com.cloriti.workshiftmanager.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.cloriti.workshiftmanager.util.IDs;

public class SelectOvertime extends AppCompatActivity {

    private Intent outputIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_overtime);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      
        Bundle input = this.getIntent().getExtras();
        outputIntent = new Intent(getApplicationContext(), AddOvertime.class);
        outputIntent.putExtra(IDs.DATA, input.getString(IDs.DATA));


        TextView title = (TextView) findViewById(R.id.title);

        Button back = (Button) findViewById(R.id.back);
        // setto il titolo
        title.setText(R.string.title_overtime_manage);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText ore = (EditText) findViewById(R.id.ore);
                EditText minuti = (EditText) findViewById(R.id.minuti);
                double hours = 0;
                double min = 0;
                min = validateMinutes(getInteger(minuti.getText().toString()));
                hours = validateHours(getInteger(ore.getText().toString()));
                outputIntent.putExtra(IDs.OVERTIME, min + hours);
                setResult(1, outputIntent);
                finish();
            }
        });
    }

    private int getInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Throwable t) {
            return 0;
        }
    }

    private double validateMinutes(int newVal) {
        if (newVal > 60)
            allert("m", newVal);
        else {
            if (newVal < 15)
                return 0;
            else if (newVal < 30)
                return 0.25;
            else if (newVal < 45)
                return 0.5;
            else if (newVal < 60)
                return 0.75;
        }
        return 0;
    }

    private int validateHours(int newVal) {
        if (newVal > 60) {
            allert("h", newVal);
            return 0;
        } else
            return newVal;
    }

    private void allert(String picker, int value) {
        String message = null;
        if ("h".equalsIgnoreCase(picker))
            message = getString(R.string.msg_numero_ore) + value + getString(R.string.msg_orario_non_corretto) + "24";
        else if ("m".equalsIgnoreCase(picker))
            message = getString(R.string.msg_numero_minuti) + value + getString(R.string.msg_orario_non_corretto) + "60";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setTitle(getString(R.string.msg_valore_non_corretto));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(getString(R.string.msg_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent out = new Intent();
        out.putExtra("back", 1);
        setResult(1, out);
        finish();
    }

}
