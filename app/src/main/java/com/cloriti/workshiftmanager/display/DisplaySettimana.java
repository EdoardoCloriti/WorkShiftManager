package com.cloriti.workshiftmanager.display;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.util.Turn;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DisplaySettimana extends AppCompatActivity {

    private static final int MONDAY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_settimana);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
        Button back = (Button) findViewById(R.id.back);

        setCalendar(calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                GregorianCalendar day = new GregorianCalendar(year, month, dayOfMonth);
                Intent i = new Intent(getApplicationContext(), DisplayHourWeek.class);
                SimpleDateFormat sdf = new SimpleDateFormat(Turn.PATTERN);
                String selectedDay = new String(sdf.format(day.getTime()));
                Toast.makeText(getApplicationContext(), selectedDay, Toast.LENGTH_SHORT).show();
                Calendar cal = Calendar.getInstance();
                cal.setTime(day.getTime());
                int mounth = cal.get(Calendar.WEEK_OF_YEAR);
                i.putExtra("MOUNTH", mounth);
                i.putExtra("YEAR", year);
                startActivity(i);
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
