package com.cloriti.workshiftmanager.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.util.IDs;
import com.cloriti.workshiftmanager.util.Turn;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ManageWorkShift extends AppCompatActivity {

    private static final int MONDAY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_work_shift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CalendarView calendar = (CalendarView) findViewById(R.id.calendar);

        Button back = (Button) findViewById(R.id.back);

        setCalendar(calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar sysDate = Calendar.getInstance();

                Intent add = new Intent(getApplicationContext(), CreateWorkShift.class);
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                String selectedDay = new String(sdf.format(selected.getTime()));
                if (sysDate.before(selected)) {
                    Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();

                    add.putExtra(IDs.DATA, selectedDay);
                    add.putExtra(IDs.WEEK_ID, selected.get(Calendar.WEEK_OF_YEAR));
                    add.putExtra(IDs.YEAR, year);
                    add.putExtra(IDs.MONTH, month + 1);
                    startActivity(add);
                } else {
                    Toast.makeText(getApplicationContext(), "Giorno selezionato non disponibile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setCalendar(CalendarView calendar) {
        calendar.setShowWeekNumber(false);
        calendar.setFirstDayOfWeek(MONDAY);
    }
}
