package com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "workshiftmanager.db";
    public final static int DATABASE_VERSION = 1;

    private static final String TURN_DATABASE_CREATE = "create table turn (_id integer primary key autoincrement,week_id number not null,year number not null, reference_date text not null, mattina_inizio text, mattina_fine text,pomeriggio_inizio text,pomeriggio_fine text,overtime number,hour number,priority number);";
    private static final String HOUR_DATABASE_CREATE = "create table hour (_id integer primary key autoincrement,week_id number not null,year number not null, mounth number ,hour number, overtime number not null);";
    private static final String SETTING_DATABASE_CREATE = "create table setting (_id integer primary key autoincrement, property text not null, value text not null);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSettingTable(db);
        createTurnTable(db);
        createHourTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropSettingTable(db);
        dropHourTable(db);
        dropTurnTable(db);
        onCreate(db);

    }

    public void onResetSetting(SQLiteDatabase db) throws Throwable {
        try {
            dropSettingTable(db);
            createSettingTable(db);
        } catch (Throwable t) {
            throw t;
        }

    }

    public void onResetTurn(SQLiteDatabase db) throws Throwable {
        try {
            dropTurnTable(db);
            createTurnTable(db);
        } catch (Throwable t) {
            throw t;
        }

    }

    public void onResetHour(SQLiteDatabase db) throws Throwable {
        try {
            dropHourTable(db);
            createHourTable(db);
        } catch (Throwable t) {
            throw t;
        }

    }

    private void createSettingTable(SQLiteDatabase db) {
        db.execSQL(SETTING_DATABASE_CREATE);
    }

    private void createTurnTable(SQLiteDatabase db) {
        db.execSQL(TURN_DATABASE_CREATE);
    }

    private void createHourTable(SQLiteDatabase db) {
        db.execSQL(HOUR_DATABASE_CREATE);
    }

    private void dropSettingTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS setting");
    }

    private void dropTurnTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS turn");
    }

    private void dropHourTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS hour");
    }

}
