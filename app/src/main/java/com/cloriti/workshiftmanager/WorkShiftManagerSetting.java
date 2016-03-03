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

/**
 * Activity per la Gestione delle impostazioni della Apllicazione
 *
 * @Author edoardo.cloriti@studio.unibo.it
 */
public class WorkShiftManagerSetting extends AppCompatActivity {

    private Dialog selectMinuteToNotfiedDialog;
    private Intent m = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_shift_manager_setting);
        //Setting delle impostazioni della Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(R.string.title_app_upper);
        setSupportActionBar(toolbar);
        //setting del navigation button per tornare indietro
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        //Creazione dell'intent verso MultiSelectionMenu
        AccessToDB db = new AccessToDB();
        m = new Intent(getApplicationContext(), MultiSelectionMenu.class);

        //Gestione della spunta del CheckBox per le notifiche, in caso venga stuntato deve apparire un Pop-up per la selezione
        //di minuti di anticipo con cui inviare la notifica
        CheckBox notify = (CheckBox) findViewById(R.id.notify);
        notify.setOnCheckedChangeListener(null);
        if (db.existPropery(Property.NOTIFICA, getApplicationContext()) != 0) {
            if ("true".equals(db.getProperty(Property.NOTIFICA, getApplicationContext()).getValue()))
                notify.setChecked(true);
            else
                notify.setChecked(false);
        }

        //Setting del listener per il CheckBox notify
        notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    //Gestione dei dialog per l'immissione dei minuti quando il listener si accorge che viene spuntato il CheckBox
                    selectMinuteToNotfiedDialog = new Dialog(WorkShiftManagerSetting.this);
                    selectMinuteToNotfiedDialog.setContentView(R.layout.dialog_notify_min);
                    selectMinuteToNotfiedDialog.show();
                    Button submitDialog = (Button) selectMinuteToNotfiedDialog.findViewById(R.id.submit_dialog);
                    Button backDialog = (Button) selectMinuteToNotfiedDialog.findViewById(R.id.back_dialog);
                    submitDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Alla pressione del tasto 'CONFERMA' del dialog si gestisce l'inserimento dei minuti sul DB
                            AccessToDB db = new AccessToDB();
                            EditText minuti = (EditText) selectMinuteToNotfiedDialog.findViewById(R.id.minuti);
                            String min = minuti.getText().toString();
                            //Creazione della Property da Inserire
                            Property p = new Property();
                            p.setProperty(Property.NOTIFICA_MIN);
                            p.setValue(min);
                            db.insertProperty(p, getApplicationContext());
                            selectMinuteToNotfiedDialog.dismiss();
                        }
                    });

                    backDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Se viene premuto il tasto back si inserisce una Property di Default che corrisponde a 30 minuti
                            AccessToDB db = new AccessToDB();
                            Property p = new Property();
                            p.setProperty(Property.NOTIFICA_MIN);
                            p.setValue("30");
                            db.insertProperty(p, getApplicationContext());
                            selectMinuteToNotfiedDialog.dismiss();
                        }
                    });
                }

            }
        });

        setStateOre(db);
        setStateNotify(db);
    }

    /**
     * metodo per la Gestione al momento della perssione sul tasto Submit
     * valida e inserisce le ore-settimanali immesse, incaso siano in un formato errato o non siano
     * mostra un Dialog di Errore, Gestisce e inserisce la CheckBox della Notifica
     */
    private void submit() {
        EditText oreContratto = (EditText) findViewById(R.id.ore);
        CheckBox notify = (CheckBox) findViewById(R.id.notify);
        boolean check = true;
        String ore = oreContratto.getText().toString();
        AccessToDB db = new AccessToDB();

        //Validazione delle ore-settimanali immese
        if (ore.length() == 2 || ore.length() == 1) {
            check = true;
        } else {
            check = false;
        }

        //se la validazione delle ore settimanali immesse passa la validazione si può procede con l'inserimento
        if (check) {
            manageNotify(notify, db);
            insertWeeklyHours(ore, db);
            //controlla se esite la Property su DB che flegga il primo accesso e la Aggiorna
            if (db.getProperty(Property.READYTOGO, getApplicationContext()) == null) {
                Property ready = new Property();
                ready.setProperty(Property.READYTOGO);
                ready.setValue("true");
                db.insertProperty(ready, getApplicationContext());
                startActivity(m);
                close();
            } else {
                Property ready = new Property();
                ready.setProperty(Property.READYTOGO);
                ready.setValue("true");
                db.insertProperty(ready, getApplicationContext());
                startActivity(m);
                close();
            }
        } else {
            //Se non vengono passati i controlli di validotà dell'orario si crea una  Dialog per notificare l'errore
            AlertDialog.Builder builder = new AlertDialog.Builder(WorkShiftManagerSetting.this);
            builder.setTitle(getApplicationContext().getString(R.string.title_activity_select_hours));
            builder.setMessage("Impostazioni non valide, ore settimanali obbligatorie");
            builder.setIcon(R.drawable.ic_error_black_48dp);
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

    /**
     * Metodo per l'inserimento delle ore settimanali
     *
     * @param ore
     * @param db
     */
    private void insertWeeklyHours(String ore, AccessToDB db) {
        Property oreSettimanali = new Property();
        oreSettimanali.setProperty(Property.ORESETTIMANALI);
        oreSettimanali.setValue(ore);
        db.insertProperty(oreSettimanali, getApplicationContext());
    }

    /**
     * Metodo per la gestione della pressione del Button Back
     */
    private void close() {
        finish();
    }

    /**
     * Metodo per il controllo su DB se esiste la Property per le ore settimanali in caso affermativo la scrive come default
     * nell'editText per l'inserimento delle ore
     *
     * @param db
     */
    private void setStateOre(AccessToDB db) {
        EditText oreContratto = (EditText) findViewById(R.id.ore);
        if (db.existPropery(Property.ORESETTIMANALI, getApplicationContext()) != 0) {
            Property ore = db.getProperty(Property.ORESETTIMANALI, getApplicationContext());
            CharSequence str = ore.getValue();
            oreContratto.setText(str, TextView.BufferType.EDITABLE);
        }
    }

    /**
     * Metodo per il controllo su DB sull'esistenza della Propery per le notifiche in caso affermativo
     * setta come default il vaolre che c'è su DB
     *
     * @param db
     */
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

    /**
     * Metodo per la Gestione del CheckBox relativo alle  notifiche al momento del Submit
     * se la Property "notify" esiste su DB ed è differente la aggiorna, altrimenti la inserisce
     *
     * @param notify
     * @param db
     */
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
        switch (id) {
            case R.id.action_help:
                WorkshiftManagerTutorial.showWorkShiftManagerTurorial(WorkShiftManagerSetting.this, WorkshiftManagerTutorial.WORK_SHFIT_MANAGER_SETTING);
                break;
            case R.id.action_submit:
                submit();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
