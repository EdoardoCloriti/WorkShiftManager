package com.cloriti.workshiftmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.cloriti.workshiftmanager.selection.MultiSelectionMenu;
import com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.util.notification.WorkShiftManagerAlarmService;
import com.cloriti.workshiftmanager.util.notification.WorkShiftManagerNotificationService;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

public class WorkShiftManagerSetting extends AppCompatActivity {

    private Dialog d;
    private Intent m = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_shift_manager_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AccessToDB db = new AccessToDB();
        m = new Intent(getApplicationContext(), MultiSelectionMenu.class);
        Button submit = (Button) findViewById(R.id.submit);
        Button back = (Button) findViewById(R.id.back);

        CheckBox notify = (CheckBox) findViewById(R.id.notify);
        notify.setOnCheckedChangeListener(null);
        if (db.existPropery(Property.NOTIFICA, getApplicationContext()) != 0) {
            if ("true".equals(db.getProperty(Property.NOTIFICA, getApplicationContext()).getValue()))
                notify.setChecked(true);
            else
                notify.setChecked(false);
        }

        notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    d = new Dialog(WorkShiftManagerSetting.this);
                    d.setTitle("Prova");
                    d.setContentView(R.layout.dialog_notify_min);
                    d.show();
                    Button submit = (Button) d.findViewById(R.id.submit_dialog);
                    Button back = (Button) d.findViewById(R.id.back_dialog);
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AccessToDB db = new AccessToDB();
                            EditText minuti = (EditText) d.findViewById(R.id.minuti);
                            String min = minuti.getText().toString();
                            Property p = new Property();
                            p.setProperty(Property.NOTIFICA_MIN);
                            p.setValue(min);
                            db.insertProperty(p, getApplicationContext());
                            d.dismiss();
                        }
                    });

                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AccessToDB db = new AccessToDB();
                            Property p = new Property();
                            p.setProperty(Property.NOTIFICA_MIN);
                            p.setValue("30");
                            db.insertProperty(p, getApplicationContext());
                            d.dismiss();
                        }
                    });
                }

            }
        });

        setStateOre(db);
        setStateNotify(db);
        setStateAlarm(db);


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText oreContratto = (EditText) findViewById(R.id.ore);
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WorkShiftManagerSetting.this);
                    builder.setTitle(getApplicationContext().getString(R.string.title_activity_select_hours));
                    builder.setMessage("Impostazioni non valide, ore settimanali obbligatorie");
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setPositiveButton(getApplicationContext().getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog d = builder.create();
                    d.show();
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
