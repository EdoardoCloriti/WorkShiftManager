package com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;

import java.text.SimpleDateFormat;

public class DbAdapter {

    // COLONNE TABELLA TURN
    // id del DB NUMBER
    public static final String ID = "_id";
    // numero della settimana
    public static final String WEEK_ID = "week_id";
    // year
    public static final String YEAR = "year";
    public static final String MOUNTH = "mounth";
    // data di riferimento DATE
    public static final String REFERENCE_DATE = "reference_date";
    // mattina VARCHAR che comprende l'orario di inizo e di fine -> bisogna splittare in uscita e compreimere in uscita
    public static final String MATTINA_INIZIO = "mattina_inizio";
    public static final String MATTINA_FINE = "mattina_fine";
    // pomeriggio VARCHAR che comprende l'orario di inizo e di fine -> bisogna splittare in uscita e compreimere in uscita
    public static final String POMERIGGIO_INIZIO = "pomeriggio_inizio";
    public static final String POMERIGGIO_FINE = "pomeriggio_fine";
    // overtime NUMBER
    public static final String OVERTIME = "overtime";
    // overtime NUMBER
    public static final String HOUR = "hour";
    // priority NUMBER priorita dell'impegno inserito -> bisogna creare un mapping
    public static final String PRIORITY = "priority";
    // property TEXT not null
    public static final String PROPERTY = "property";
    // value TEXT not null
    public static final String VALUE = "value";
    // nome della tabella
    private static final String TURN_DATABASE_TABLE = "turn";
    private static final String HOUR_DATABASE_TABLE = "hour";
    private static final String SETTING_DATABASE_TABLE = "setting";
    private Context context;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

    public DbAdapter(Context context) {
        this.context = context;
    }

    public DbAdapter open() {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // creare un turno
    public Long createTurn(String dataRiferimento, int weekId, int year, String oraInizioMattina, String oraFineMattina, String OraInizioPomeriggio, String OraFinePomeriggio, double overtime, double hour, Long priority) {
        try {
            ContentValues turn = fillTurn(dataRiferimento, weekId, year, oraInizioMattina, oraFineMattina, OraInizioPomeriggio, OraFinePomeriggio, overtime, hour, priority);
            return database.insertOrThrow(TURN_DATABASE_TABLE, null, turn);
        } catch (Throwable t) {
            return null;
        }
    }

    // aggiornare turno
    public boolean updateTurn(int id, int weekId, int year, String dataRiferimento, String oraInizioMattina, String oraFineMattina, String OraInizioPomeriggio, String OraFinePomeriggio, double overtime, double hour, Long priority) {
        ContentValues turn = fillTurn(dataRiferimento, weekId, year, oraInizioMattina, oraFineMattina, OraInizioPomeriggio, OraFinePomeriggio, overtime, hour, priority);
        return database.update(TURN_DATABASE_TABLE, turn, ID + "=?", new String[]{Integer.toString(id)}) > 0;
    }

    // rimuovere un turno
    public boolean deleteTurn(int id) {
        return database.delete(TURN_DATABASE_TABLE, ID + "=?", new String[]{Integer.toString(id)}) > 0;
    }

    // prendere tutti i dati da DB
    public Cursor fetchAllTurn() {
        return database.query(TURN_DATABASE_TABLE, new String[]{ID, WEEK_ID, YEAR, REFERENCE_DATE, MATTINA_INIZIO, MATTINA_FINE, POMERIGGIO_INIZIO, POMERIGGIO_FINE, OVERTIME, HOUR, PRIORITY}, null, null, null, null, null);
    }

    // prende i dati da DB in base a una condizione
    // fetch contacts filter by a string
    public Cursor fetchTurnByDate(String referenceDate) throws ParseException {
        Cursor mCursor = database.query(TURN_DATABASE_TABLE, new String[]{ID, WEEK_ID, YEAR, REFERENCE_DATE, MATTINA_INIZIO, MATTINA_FINE, POMERIGGIO_INIZIO, POMERIGGIO_FINE, OVERTIME, HOUR, PRIORITY}, REFERENCE_DATE + "=?", new String[]{referenceDate}, null, null, null);

        return mCursor;
    }

    public Cursor fetchTurnByYear(int year) {
        Cursor mCursor = database.query(TURN_DATABASE_TABLE, new String[]{ID, WEEK_ID, YEAR, REFERENCE_DATE, MATTINA_INIZIO, MATTINA_FINE, POMERIGGIO_INIZIO, POMERIGGIO_FINE, OVERTIME, HOUR, PRIORITY}, YEAR + "=?", new String[]{Integer.toString(year)}, null, null, null);
        return mCursor;
    }

    private ContentValues fillTurn(String dataRiferimento, int weekId, int year, String oraInizioMattina, String oraFineMattina, String OraInizioPomeriggio, String OraFinePomeriggio, double overtime, double hour, Long priority) {
        ContentValues values = new ContentValues();
        values.put(REFERENCE_DATE, dataRiferimento);
        values.put(WEEK_ID, weekId);
        values.put(YEAR, year);
        values.put(MATTINA_INIZIO, oraInizioMattina);
        values.put(MATTINA_FINE, oraFineMattina);
        values.put(POMERIGGIO_INIZIO, OraInizioPomeriggio);
        values.put(POMERIGGIO_FINE, OraFinePomeriggio);
        values.put(OVERTIME, overtime);
        values.put(HOUR, hour);
        values.put(PRIORITY, Long.toString(priority));

        return values;
    }

    // inserire un campo
    public long createWeek(int weekId, int year, int mounth, double hour, double extra_hour) {
        ContentValues week = fillWeek(weekId, year, mounth, hour, extra_hour);
        return database.insertOrThrow(HOUR_DATABASE_TABLE, null, week);
    }

    public boolean updateWeek(int id, int weekId, int year, int mounth, double hour, double extra_hour) {
        ContentValues week = fillWeek(weekId, year, mounth, hour, extra_hour);
        return database.update(HOUR_DATABASE_TABLE, week, ID + "=?", new String[]{Integer.toString(id)}) > 0;
    }

    public boolean deleteWeek(int id) {
        return database.delete(HOUR_DATABASE_TABLE, ID + "=?", new String[]{Integer.toString(id)}) > 0;
    }

    public Cursor fetchWeekByCorrelationID(int year, int week) {

        Cursor mCursor = database.query(HOUR_DATABASE_TABLE, new String[]{ID, WEEK_ID, YEAR, HOUR, MOUNTH, OVERTIME}, WEEK_ID + "=? AND " + YEAR + "=?", new String[]{Integer.toString(week), Integer.toString(year)}, null, null, null);
        return mCursor;
    }

    public Cursor fetchMount(int mounth, int year) {
        Cursor mcursor = database.query(HOUR_DATABASE_TABLE, new String[]{ID, WEEK_ID, YEAR, HOUR, MOUNTH, OVERTIME}, MOUNTH + "=? AND " + YEAR + "=?", new String[]{Integer.toString(mounth), Integer.toString(year)}, null, null, null, null);
        return mcursor;
    }

    public Cursor fetchMounthByYear(int year) {
        Cursor mcursor = database.query(HOUR_DATABASE_TABLE, new String[]{ID, WEEK_ID, YEAR, HOUR, MOUNTH, OVERTIME}, YEAR + "=?", new String[]{Integer.toString(year)}, null, null, null, null);
        return mcursor;
    }

    public Cursor fetchYear(int year) {
        Cursor mcursor = database.query(HOUR_DATABASE_TABLE, new String[]{ID, WEEK_ID, YEAR, HOUR, MOUNTH, OVERTIME}, YEAR + "=?", new String[]{Integer.toString(year)}, null, null, null, null);
        return mcursor;
    }

    private ContentValues fillWeek(int weekId, int year, int mounth, double hour, double extra_hour) {
        ContentValues values = new ContentValues();
        values.put(WEEK_ID, weekId);
        values.put(YEAR, year);
        values.put(MOUNTH, mounth);
        values.put(HOUR, hour);
        values.put(OVERTIME, extra_hour);
        return values;
    }


    public long creaProperty(String property, String value) {
        ContentValues propertyValues = fillProperty(property, value);
        return database.insertOrThrow(SETTING_DATABASE_TABLE, null, propertyValues);
    }

    private ContentValues fillProperty(String property, String value) {
        ContentValues values = new ContentValues();
        values.put(PROPERTY, property);
        values.put(VALUE, value);
        return values;
    }

    public Cursor fetchProperty(String property) {
        Cursor cursor = database.query(SETTING_DATABASE_TABLE, new String[]{ID, PROPERTY, VALUE}, PROPERTY + "=?", new String[]{property}, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
            return cursor;
        else
            return null;
    }

    public Cursor fetchSetting() {
        Cursor cursor = database.query(SETTING_DATABASE_TABLE, new String[]{ID, PROPERTY, VALUE}, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
            return cursor;
        else
            return null;
    }

    public boolean updateProperty(String property, String value) {
        ContentValues values = fillProperty(property, value);
        return database.update(SETTING_DATABASE_TABLE, values, PROPERTY + "=?", new String[]{property}) > 0;
    }

    // TODO:metodi in modalità DEV
    public boolean resetSettingDB() {
        try {
            dbHelper.onResetSetting(database);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean resetTurnDB() {
        try {
            dbHelper.onResetTurn(database);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean resetHourDB() {
        try {
            dbHelper.onResetTurn(database);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
