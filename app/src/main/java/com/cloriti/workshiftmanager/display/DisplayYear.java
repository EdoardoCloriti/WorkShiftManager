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

import java.util.GregorianCalendar;
import java.util.List;

public class DisplayYear extends AppCompatActivity {

    GregorianCalendar now = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_year);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(R.string.title_app_upper);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AccessToDB db = new AccessToDB();
        List<Week> month = null;
        now = new GregorianCalendar();
        TextView anno = (TextView) findViewById(R.id.anno);

        anno.setText(Integer.toString(now.get(now.YEAR)));

        TextView oreGennaio = (TextView) findViewById(R.id.ore_lavorate_1);
        TextView straordinariGennaio = (TextView) findViewById(R.id.straordinari_1);
        month = db.getMounth(1, now.get(now.YEAR), getApplicationContext());
        oreGennaio.setText(calculateHour(month));
        straordinariGennaio.setText(calculateOvertime(month));
        month = null;

        TextView oreFebbraio = (TextView) findViewById(R.id.ore_lavorate_2);
        TextView straordinariFebbraio = (TextView) findViewById(R.id.straordinari_2);

        month = db.getMounth(2, now.get(now.YEAR), getApplicationContext());
        oreFebbraio.setText(calculateHour(month));
        straordinariFebbraio.setText(calculateOvertime(month));
        month = db.getMounth(2, now.get(now.YEAR), getApplicationContext());

        TextView oreMarzo = (TextView) findViewById(R.id.ore_lavorate_3);
        TextView straordinariMarzo = (TextView) findViewById(R.id.straordinari_3);

        month = db.getMounth(3, now.get(now.YEAR), getApplicationContext());
        oreMarzo.setText(calculateHour(month));
        straordinariMarzo.setText(calculateOvertime(month));
        month = null;

        TextView oreAprile = (TextView) findViewById(R.id.ore_lavorate_4);
        TextView straordinariAprile = (TextView) findViewById(R.id.straordinari_4);

        month = db.getMounth(4, now.get(now.YEAR), getApplicationContext());
        oreAprile.setText(calculateHour(month));
        straordinariAprile.setText(calculateOvertime(month));
        month = null;

        TextView oreMaggio = (TextView) findViewById(R.id.ore_lavorate_5);
        TextView straordinariMaggio = (TextView) findViewById(R.id.straordinari_5);

        month = db.getMounth(5, now.get(now.YEAR), getApplicationContext());
        oreMaggio.setText(calculateHour(month));
        straordinariMaggio.setText(calculateOvertime(month));
        month = null;

        TextView oreGiugno = (TextView) findViewById(R.id.ore_lavorate_6);
        TextView straordinariGiugno = (TextView) findViewById(R.id.straordinari_6);

        month = db.getMounth(6, now.get(now.YEAR), getApplicationContext());
        oreGiugno.setText(calculateHour(month));
        straordinariGiugno.setText(calculateOvertime(month));
        month = null;

        TextView oreLuglio = (TextView) findViewById(R.id.ore_lavorate_7);
        TextView straordinariLuglio = (TextView) findViewById(R.id.straordinari_7);

        month = db.getMounth(7, now.get(now.YEAR), getApplicationContext());
        oreLuglio.setText(calculateHour(month));
        straordinariLuglio.setText(calculateOvertime(month));
        month = null;

        TextView oreAgosto = (TextView) findViewById(R.id.ore_lavorate_8);
        TextView straordinariAgosto = (TextView) findViewById(R.id.straordinari_8);

        month = db.getMounth(8, now.get(now.YEAR), getApplicationContext());
        oreAgosto.setText(calculateHour(month));
        straordinariAgosto.setText(calculateOvertime(month));
        month = null;

        TextView oreSettembre = (TextView) findViewById(R.id.ore_lavorate_9);
        TextView straordinariSettembre = (TextView) findViewById(R.id.straordinari_9);

        month = db.getMounth(9, now.get(now.YEAR), getApplicationContext());
        oreSettembre.setText(calculateHour(month));
        straordinariSettembre.setText(calculateOvertime(month));
        month = null;

        TextView oreOttobre = (TextView) findViewById(R.id.ore_lavorate_10);
        TextView straordinariOttobre = (TextView) findViewById(R.id.straordinari_10);

        month = db.getMounth(10, now.get(now.YEAR), getApplicationContext());
        oreOttobre.setText(calculateHour(month));
        straordinariOttobre.setText(calculateOvertime(month));
        month = null;

        TextView oreNovembre = (TextView) findViewById(R.id.ore_lavorate_11);
        TextView straordinariNovembre = (TextView) findViewById(R.id.straordinari_11);

        month = db.getMounth(11, now.get(now.YEAR), getApplicationContext());
        oreNovembre.setText(calculateHour(month));
        straordinariNovembre.setText(calculateOvertime(month));
        month = null;

        TextView oreDicembre = (TextView) findViewById(R.id.ore_lavorate_12);
        TextView straordinariDicembre = (TextView) findViewById(R.id.straordinari_12);

        month = db.getMounth(12, now.get(now.YEAR), getApplicationContext());
        oreDicembre.setText(calculateHour(month));
        straordinariDicembre.setText(calculateOvertime(month));
        month = null;
    }

    private String calculateHour(List<Week> month) {
        double hours = 0;
        for (Week week : month) {
            hours = hours + week.getHour();
        }
        return Double.toString(hours);

    }

    private String calculateOvertime(List<Week> month) {
        double hours = 0;
        for (Week week : month) {
            hours = hours + week.getExtraHour();
        }
        return Double.toString(hours);

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
            WorkshiftManagerTutorial.showWorkShiftManagerTurorial(DisplayYear.this, WorkshiftManagerTutorial.DISPLAY_YEAR);
        }


        return super.onOptionsItemSelected(item);
    }
}
