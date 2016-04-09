package com.cloriti.workshiftmanager.util.calendar;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Classe per la gestione della API di google Calendar
 * essa permette di inserire i turni nel proprio Account
 */
public class GoogleCalendarManager {
    //field per i servizi delle Google API
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    //gestore delle credenziali
    private GoogleAccountCredential login;

    //dati relativi all'applicazione
    private Context context;
    private Activity activity;
    private Turn turnToAdd = null;


    public GoogleCalendarManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setTurnToAdd(Turn turnToAdd) {
        this.turnToAdd = turnToAdd;
    }

    /**
     * metodo necessario all'istanziazione delle API di Google Calendar
     * è necessaria la chiamata all'interno del metodo onCreate della Activity
     *
     * @Author edoardo.cloriti@studio.unibo.it
     */
    public void createGoogleCalendar() {
        // Initialize credentials and service object.
        login = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }


    public void getResultsFromApi() {
        try {
            if (!isGooglePlayServicesAvailable()) {
                acquireGooglePlayServices();
            } else if (login.getSelectedAccountName() == null) {
                chooseAccount();
            } else if (!isDeviceOnline()) {
                Toast.makeText(context, "No network connection available.", Toast.LENGTH_SHORT).show();
            } else {
                new GoogleCalendarInsert(login, context, activity, turnToAdd).execute();
            }
        } catch (Throwable t) {
            t.getCause();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(activity, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = null;
            AccessToDB db = new AccessToDB();
            if (db.existPropery(Property.ACCOUNT, context) != 0) {
                Property account = db.getProperty(Property.ACCOUNT, context);
                accountName = account.getValue();
            }

            if (accountName != null) {
                login.setSelectedAccountName(accountName);
            } else {
                // Start a dialog from which the user can choose an account
                activity.startActivityForResult(login.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != activity.RESULT_OK) {
                    Toast.makeText(context, "This app requires Google Play Services. Please install Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == activity.RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        AccessToDB db = new AccessToDB();
                        Property account = new Property();
                        account.setProperty(Property.ACCOUNT);
                        account.setValue(accountName);
                        db.insertProperty(account, context);
                        login.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == activity.RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(activity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(activity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
