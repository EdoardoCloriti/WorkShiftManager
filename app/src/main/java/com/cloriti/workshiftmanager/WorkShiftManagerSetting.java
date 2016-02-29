package com.cloriti.workshiftmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.manage.MultiSelectionMenu;
import com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.com.orion.workshiftmanager.util.notification.WorkShiftCounterAlarmService;
import com.cloriti.workshiftmanager.com.orion.workshiftmanager.util.notification.WorkShiftCounterNotificationService;

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
        setStateAllarm(db);


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
                mangaeNotify(notify, db);

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
        if(db.existPropery( Property.ORESETTIMANALI,getApplicationContext())!=0)
        {
            Property ore=db.getProperty(Property.ORESETTIMANALI,getApplicationContext());
            CharSequence str=ore.getValue();
            oreContratto.setText(str, TextView.BufferType.EDITABLE);
        }
    }

    private void setStateAllarm(AccessToDB db) {
        CheckBox allarm = (CheckBox) findViewById(R.id.activeAllarm);
        if (db.existPropery(Property.ALLARM, getApplicationContext()) != 0) {
            Property notifyPr = db.getProperty(Property.ALLARM, getApplicationContext());
            if ("true".equals(notifyPr.getValue()))
                allarm.setChecked(true);
            else
                allarm.setChecked(false);
        } else
            allarm.setChecked(false);
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

    private void manageAlarm(CheckBox allarm, AccessToDB db) {
        if (allarm.isChecked()) {
            Property activeAllarm = new Property();
            activeAllarm.setProperty(Property.ALLARM);
            activeAllarm.setValue("true");
            if (db.existPropery(activeAllarm, getApplicationContext()) != 0) {
                Property propertyOnDbdb = db.getProperty(Property.ALLARM, getApplicationContext());
                if (!activeAllarm.getValue().equals(propertyOnDbdb.getValue())) {
                    db.insertProperty(activeAllarm, getApplicationContext());
                    startService(new Intent(getApplicationContext(), WorkShiftCounterAlarmService.class));
                }
            } else {
                db.insertProperty(activeAllarm, getApplicationContext());
                startService(new Intent(getApplicationContext(), WorkShiftCounterAlarmService.class));
            }
        } else if (!allarm.isChecked()) {
            Property activeAllarm = new Property();
            activeAllarm.setProperty(Property.ALLARM);
            activeAllarm.setValue("false");
            if (db.existPropery(activeAllarm, getApplicationContext()) != 0) {
                Property propertyOnDbdb = db.getProperty(Property.ALLARM, getApplicationContext());
                if (!activeAllarm.getValue().equals(propertyOnDbdb.getValue())) {
                    db.insertProperty(activeAllarm, getApplicationContext());
                    stopService(new Intent(getApplicationContext(), WorkShiftCounterAlarmService.class));
                }
            } else {
                db.insertProperty(activeAllarm, getApplicationContext());
                stopService(new Intent(getApplicationContext(), WorkShiftCounterAlarmService.class));
            }
        }
    }


    private void mangaeNotify(CheckBox notify, AccessToDB db) {
        if (notify.isChecked()) {
            Property activeNotify = new Property();
            activeNotify.setProperty(Property.NOTIFICA);
            activeNotify.setValue("true");
            if (db.existPropery(activeNotify, getApplicationContext()) != 0) {
                Property propertyOnDbdb = db.getProperty(Property.NOTIFICA, getApplicationContext());
                if (!activeNotify.getValue().equals(propertyOnDbdb.getValue())) {
                    db.insertProperty(activeNotify, getApplicationContext());
                    startService(new Intent(getApplicationContext(), WorkShiftCounterNotificationService.class));
                }
            } else {
                db.insertProperty(activeNotify, getApplicationContext());
                startService(new Intent(getApplicationContext(), WorkShiftCounterNotificationService.class));
            }
        } else if (!notify.isChecked()) {
            Property activeNotify = new Property();
            activeNotify.setProperty(Property.NOTIFICA);
            activeNotify.setValue("false");
            if (db.existPropery(activeNotify, getApplicationContext()) != 0) {
                Property propertyOnDbdb = db.getProperty(Property.NOTIFICA, getApplicationContext());
                if (!activeNotify.getValue().equals(propertyOnDbdb.getValue())) {
                    db.insertProperty(activeNotify, getApplicationContext());
                    stopService(new Intent(getApplicationContext(), WorkShiftCounterNotificationService.class));
                }
            } else {
                db.insertProperty(activeNotify, getApplicationContext());
                stopService(new Intent(getApplicationContext(), WorkShiftCounterAlarmService.class));
            }
        }
    }

}
