package com.cloriti.workshiftmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cloriti.workshiftmanager.selection.MultiSelectionMenu;
import com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.util.notification.WorkShiftManagerAlarmService;
import com.cloriti.workshiftmanager.util.notification.WorkShiftManagerNotificationService;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

import org.xml.sax.ErrorHandler;

public class WorkShiftManagerSetting extends AppCompatActivity {

    private Intent e = null;
    private Intent m = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_shift_manager_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AccessToDB db = new AccessToDB();
        e = new Intent(getApplicationContext(), ErrorHandler.class);
        m = new Intent(getApplicationContext(), MultiSelectionMenu.class);
        Button submit = (Button) findViewById(R.id.submit);
        Button back = (Button) findViewById(R.id.back);

        setStateOre(db);
        setStateNotify(db);
        setStateAlarm(db);


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText oreContratto = (EditText) findViewById(R.id.ore);
                CheckBox allarm = (CheckBox) findViewById(R.id.activeAllarm);
                CheckBox notify = (CheckBox) findViewById(R.id.notify);
                boolean check = true;
                String ore = oreContratto.getText().toString();
                AccessToDB db = new AccessToDB();
                if (ore.length() == 2 || ore.length() == 1) {
                    Property oreSettimanali = new Property();
                    oreSettimanali.setProperty(Property.ORESETTIMANALI);
                    oreSettimanali.setValue(ore);
                    db.insertProperty(oreSettimanali, getApplicationContext());
                } else {
                    check = false;
                }
                manageAlarm(allarm, db);
                manageNotify(notify, db);

                if (check) {
                    if (db.getProperty(Property.READYTOGO, getApplicationContext()) == null) {
                        Property ready = new Property();
                        ready.setProperty(Property.READYTOGO);
                        ready.setValue("true");
                        db.insertProperty(ready, getApplicationContext());
                        startActivity(m);
                        finish();
                    } else {
                        Property ready = new Property();
                        ready.setProperty(Property.READYTOGO);
                        ready.setValue("true");
                        db.insertProperty(ready, getApplicationContext());
                        startActivity(m);
                        finish();
                    }
                } else
                    startActivity(e);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setStateOre(AccessToDB db) {
        EditText oreContratto = (EditText) findViewById(R.id.ore);
        if (db.existPropery(Property.ORESETTIMANALI, getApplicationContext()) != 0) {
            Property ore = db.getProperty(Property.ORESETTIMANALI, getApplicationContext());
            CharSequence str = ore.getValue();
            oreContratto.setText(str, TextView.BufferType.EDITABLE);
        }
    }

    private void setStateAlarm(AccessToDB db) {
        CheckBox alarm = (CheckBox) findViewById(R.id.activeAllarm);
        if (db.existPropery(Property.ALLARM, getApplicationContext()) != 0) {
            Property notifyPr = db.getProperty(Property.ALLARM, getApplicationContext());
            if ("true".equals(notifyPr.getValue()))
                alarm.setChecked(true);
            else
                alarm.setChecked(false);
        } else
            alarm.setChecked(false);
    }

    private void setStateNotify(AccessToDB db) {
        CheckBox notify = (CheckBox) findViewById(R.id.notify);
        if (db.existPropery(Property.NOTIFICA, getApplicationContext()) != 0) {
            Property notifyPr = db.getProperty(Property.NOTIFICA, getApplicationContext());
            if ("true".equals(notifyPr.getValue()))
                notify.setChecked(true);
            else
                notify.setChecked(false);
        } else
            notify.setChecked(false);
    }

    private void manageAlarm(CheckBox alarm, AccessToDB db) {
        if (alarm.isChecked()) {
            Property activeAlarm = new Property();
            activeAlarm.setProperty(Property.ALLARM);
            activeAlarm.setValue("true");
            if (db.existPropery(activeAlarm, getApplicationContext()) != 0) {
                Property propertyOnDbdb = db.getProperty(Property.ALLARM, getApplicationContext());
                if (!activeAlarm.getValue().equals(propertyOnDbdb.getValue())) {
                    db.insertProperty(activeAlarm, getApplicationContext());
                    startService(new Intent(getApplicationContext(), WorkShiftManagerAlarmService.class));
                }
            } else {
                db.insertProperty(activeAlarm, getApplicationContext());
                startService(new Intent(getApplicationContext(), WorkShiftManagerAlarmService.class));
            }
        } else if (!alarm.isChecked()) {
            Property activeAlarm = new Property();
            activeAlarm.setProperty(Property.ALLARM);
            activeAlarm.setValue("false");
            if (db.existPropery(activeAlarm, getApplicationContext()) != 0) {
                Property propertyOnDB = db.getProperty(Property.ALLARM, getApplicationContext());
                if (!activeAlarm.getValue().equals(propertyOnDB.getValue())) {
                    db.insertProperty(activeAlarm, getApplicationContext());
                    stopService(new Intent(getApplicationContext(), WorkShiftManagerAlarmService.class));
                }
            } else {
                db.insertProperty(activeAlarm, getApplicationContext());
                stopService(new Intent(getApplicationContext(), WorkShiftManagerAlarmService.class));
            }
        }
    }


    private void manageNotify(CheckBox notify, AccessToDB db) {
        if (notify.isChecked()) {
            Property activeNotify = new Property();
            activeNotify.setProperty(Property.NOTIFICA);
            activeNotify.setValue("true");
            if (db.existPropery(activeNotify, getApplicationContext()) != 0) {
                Property propertyOnDD = db.getProperty(Property.NOTIFICA, getApplicationContext());
                if (!activeNotify.getValue().equals(propertyOnDD.getValue())) {
                    db.insertProperty(activeNotify, getApplicationContext());
                    startService(new Intent(getApplicationContext(), WorkShiftManagerNotificationService.class));
                }
            } else {
                db.insertProperty(activeNotify, getApplicationContext());
                startService(new Intent(getApplicationContext(), WorkShiftManagerNotificationService.class));
            }
        } else if (!notify.isChecked()) {
            Property activeNotify = new Property();
            activeNotify.setProperty(Property.NOTIFICA);
            activeNotify.setValue("false");
            if (db.existPropery(activeNotify, getApplicationContext()) != 0) {
                Property propertyOnDB = db.getProperty(Property.NOTIFICA, getApplicationContext());
                if (!activeNotify.getValue().equals(propertyOnDB.getValue())) {
                    db.insertProperty(activeNotify, getApplicationContext());
                    stopService(new Intent(getApplicationContext(), WorkShiftManagerNotificationService.class));
                }
            } else {
                db.insertProperty(activeNotify, getApplicationContext());
                stopService(new Intent(getApplicationContext(), WorkShiftManagerAlarmService.class));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workshift_manager_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_help) {
            WorkshiftManagerTutorial.showWorkShiftManagerTurorial(WorkShiftManagerSetting.this, WorkshiftManagerTutorial.WORK_SHFIT_MANAGER_SETTING);
        }
        return super.onOptionsItemSelected(item);
    }
}
