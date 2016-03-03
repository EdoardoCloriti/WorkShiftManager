package com.cloriti.workshiftmanager.display;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.WorkShiftManagerSetting;
import com.cloriti.workshiftmanager.util.Week;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

public class DisplayHourWeek extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_hour_week);
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

        AccessToDB db = new AccessToDB();
        Bundle input = getIntent().getExtras();
        int mounth = input.getInt("MOUNTH");
        int year = input.getInt("YEAR");
        Week week = db.getWeeekByCorrelationId(year, mounth, getApplicationContext());
        TextView ore = (TextView) findViewById(R.id.ore);
        TextView straordinari = (TextView) findViewById(R.id.straordinari);

        if (week != null) {
            CharSequence oreCs = new Double(week.getHour()).toString();
            CharSequence straordinariCs = new Double(week.getExtraHour()).toString();
            ore.setText(oreCs);
            straordinari.setText(straordinariCs);
        } else {
            CharSequence oreCs = new Double(0).toString();
            CharSequence straordinariCs = new Double(0).toString();
            ore.setText(oreCs);
            straordinari.setText(straordinariCs);
        }
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
            WorkshiftManagerTutorial.showWorkShiftManagerTurorial(DisplayHourWeek.this, "DisplayHourWeek");
        }


        return super.onOptionsItemSelected(item);
    }

}
