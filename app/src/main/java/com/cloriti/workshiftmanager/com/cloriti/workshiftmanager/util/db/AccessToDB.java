package com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.db;

import android.content.Context;
import android.database.Cursor;
import android.net.ParseException;

import com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.NullObjectStrategy;
import com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util.Week;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Edoardo on 29/02/2016.
 */
public class AccessToDB {

    private DbAdapter dbAdapter = null;

    public void insertTurn(Turn turn, Context context) throws ParseException {
        try {
            int id = 0;
            Week week = null;
            if ((id = existTurn(turn.getDataRierimentoDateStr(), context)) != 0) {
                dbAdapter = new DbAdapter(context);
                dbAdapter.open();
                dbAdapter.updateTurn(id, turn.getWeekId(), turn.getYear(), turn.getDataRierimentoDateStr(), turn.getInizioMattina(), turn.getFineMattina(), turn.getInizioPomeriggio(), turn.getFinePomeriggio(), turn.getOvertime(), turn.getHour(), new Long(turn.getIsImportante() ? 1 : 0));
                dbAdapter.close();
            } else {
                dbAdapter = new DbAdapter(context);
                dbAdapter.open();
                dbAdapter.createTurn(turn.getDataRierimentoDateStr(), turn.getWeekId(), turn.getYear(), turn.getInizioMattina(), turn.getFineMattina(), turn.getInizioPomeriggio(), turn.getFinePomeriggio(), turn.getOvertime(), turn.getHour(), new Long(turn.getIsImportante() ? 1 : 0));
                dbAdapter.close();
            }
            dbAdapter.close();
            if ((week = getWeeekByCorrelationId(turn.getYear(), turn.getWeekId(), context)) == null) {
                week = new Week(context);
                week.setWeekId(turn.getWeekId());
                week.setYear(turn.getYear());
                week.setMounth(turn.getMounth());
                week.addHour(turn.getHour());
                week.setExtraHour(turn.getOvertime());
                insertWeek(week, context);
            } else {
                week.addHour(turn.getHour());
                week.setExtraHour(turn.getOvertime());
                updateWeek(week, context);
            }
        } finally {
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public boolean deleteTurnAndUpdateWeek(Turn turn, Context context) {
        dbAdapter = new DbAdapter(context);
        try {
            dbAdapter.open();
            if (turn.getId() != 0 && dbAdapter.deleteTurn(turn.getId())) {
                Week week = getWeeekByCorrelationId(turn.getYear(), turn.getWeekId(), context);
                if (week != null) {
                    double hour = week.getHour() - turn.getHour();
                    double extra = week.getExtraHour() - turn.getOvertime();
                    week.setHours(hour);
                    week.setExtraHour(extra);
                    updateWeek(week, context);
                }
                return true;
            }
            return false;
        } finally {
            dbAdapter.close();
        }
    }

    public boolean deleteTurn(Turn turn, Context context) {
        dbAdapter = new DbAdapter(context);
        try {
            dbAdapter.open();
            return dbAdapter.deleteTurn(turn.getId());
        } finally {
            dbAdapter.close();
        }
    }

    public void deleteTurns(List<Turn> turns, Context context) {
        for (Turn turn : turns) {
            deleteTurn(turn, context);
        }
    }

    public void clearTurnByYear(int year, Context context) {
        List<Turn> turns = new ArrayList<Turn>();
        dbAdapter = new DbAdapter(context);
        try {
            dbAdapter.open();
            Cursor cursor = dbAdapter.fetchTurnByYear(year);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Turn turn = new Turn();
                    turn.setId(cursor.getString(cursor.getColumnIndex(DbAdapter.ID)));
                    turn.setDatariferimento(cursor.getString(cursor.getColumnIndex(DbAdapter.REFERENCE_DATE)));
                    if (!isNull(cursor, DbAdapter.MATTINA_INIZIO))
                        turn.setIniziotMattina(cursor.getString(cursor.getColumnIndex(DbAdapter.MATTINA_INIZIO)));
                    if (!isNull(cursor, DbAdapter.MATTINA_FINE))
                        turn.setFineMattina(cursor.getString(cursor.getColumnIndex(DbAdapter.MATTINA_FINE)));
                    if (!isNull(cursor, DbAdapter.POMERIGGIO_INIZIO))
                        turn.setIniziotPomeriggio(cursor.getString(cursor.getColumnIndex(DbAdapter.POMERIGGIO_INIZIO)));
                    if (!isNull(cursor, DbAdapter.POMERIGGIO_FINE))
                        turn.setFinePomeriggio(cursor.getString(cursor.getColumnIndex(DbAdapter.POMERIGGIO_FINE)));
                    if (!isNull(cursor, DbAdapter.OVERTIME))
                        turn.setOvertime(cursor.getString(cursor.getColumnIndex(DbAdapter.OVERTIME)));
                    if (!isNull(cursor, DbAdapter.HOUR))
                        turn.setHour(cursor.getString(cursor.getColumnIndex(DbAdapter.HOUR)));
                    turn.setIsImportante(cursor.getString(cursor.getColumnIndex(DbAdapter.PRIORITY)));
                } while (cursor.moveToNext());
                cursor.close();
            }
            deleteTurns(turns, context);
        } finally {
            if (dbAdapter != null)
                dbAdapter.close();
        }

    }

    public void deleteWeek(Week week, Context context) {
        dbAdapter = new DbAdapter(context);
        try {
            dbAdapter.open();
            dbAdapter.deleteWeek(week.getId());
        } finally {
            dbAdapter.close();
        }
    }

    public void deleteWeeks(List<Week> weeks, Context context) {
        for (Week week : weeks) {
            deleteWeek(week, context);
        }
    }

    public void cleanWeekByYearToYear(int yearFrom, int yearTo, Context context) {
        List<Week> weeks = new ArrayList<Week>();
        try {
            dbAdapter.open();
            while (yearFrom < yearTo) {
                Cursor cursor = dbAdapter.fetchMounthByYear(yearFrom);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Week week = new Week(context);
                        week.setId(cursor.getInt(cursor.getColumnIndex(DbAdapter.ID)));
                        week.setWeekId(cursor.getInt(cursor.getColumnIndex(DbAdapter.WEEK_ID)));
                        week.setYear(cursor.getInt(cursor.getColumnIndex(DbAdapter.YEAR)));
                        week.setMounth(cursor.getInt(cursor.getColumnIndex(DbAdapter.MOUNTH)));
                        week.setHours(cursor.getDouble(cursor.getColumnIndex(DbAdapter.HOUR)));
                        week.setExtraHour(cursor.getDouble(cursor.getColumnIndex(DbAdapter.OVERTIME)));
                        weeks.add(week);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
            deleteWeeks(weeks, context);
        } finally {
            if (dbAdapter != null)
                dbAdapter.close();
        }

    }

    public int insertTurns(List<Turn> turns, Context context) throws ParseException {
        int n = 0;
        for (Turn turn : turns) {
            insertTurn(turn, context);
            n = n++;
        }
        return n;
    }

    public int updateTurn(List<Turn> turns, Context context) {
        try {
            Iterator<Turn> i = turns.iterator();
            int n = 0;
            while (i.hasNext()) {
                Turn turn = i.next();
                Week week = null;
                dbAdapter = new DbAdapter(context);
                dbAdapter.open();
                dbAdapter.updateTurn(turn.getId(), turn.getWeekId(), turn.getYear(), turn.getDataRierimentoDateStr(), turn.getInizioMattina(), turn.getFineMattina(), turn.getInizioPomeriggio(), turn.getFinePomeriggio(), turn.getOvertime(), turn.getHour(), new Long(turn.getIsImportante() ? 1 : 0));
                dbAdapter.close();
                if ((week = getWeekbySelectedDay(turn, context)) == null) {
                    week = new Week(context);
                    week.setWeekId(turn.getWeekId());
                    week.setYear(turn.getYear());
                    week.setMounth(turn.getMounth());
                    week.addHour(turn.getHour());
                    week.setExtraHour(turn.getOvertime());
                    insertWeek(week, context);
                } else {
                    week.addHour(turn.getHour());
                    week.setExtraHour(turn.getOvertime());
                    updateWeek(week, context);
                }
                n = n++;
            }
            return n;
        } finally {
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public Turn getTurnBySelectedDay(String day, Context context) {
        Cursor cursor = null;
        try {
            Turn turn = new Turn();
            dbAdapter = new DbAdapter(context);
            dbAdapter.open();
            try {
                cursor = dbAdapter.fetchTurnByDate(day);
            } catch (ParseException e) {
                cursor = null;
            }
            if (cursor != null && cursor.moveToFirst()) {
                turn.setId(cursor.getString(cursor.getColumnIndex(DbAdapter.ID)));
                turn.setDatariferimento(cursor.getString(cursor.getColumnIndex(DbAdapter.REFERENCE_DATE)));
                if (!isNull(cursor, DbAdapter.MATTINA_INIZIO))
                    turn.setIniziotMattina(cursor.getString(cursor.getColumnIndex(DbAdapter.MATTINA_INIZIO)));
                if (!isNull(cursor, DbAdapter.MATTINA_FINE))
                    turn.setFineMattina(cursor.getString(cursor.getColumnIndex(DbAdapter.MATTINA_FINE)));
                if (!isNull(cursor, DbAdapter.POMERIGGIO_INIZIO))
                    turn.setIniziotPomeriggio(cursor.getString(cursor.getColumnIndex(DbAdapter.POMERIGGIO_INIZIO)));
                if (!isNull(cursor, DbAdapter.POMERIGGIO_FINE))
                    turn.setFinePomeriggio(cursor.getString(cursor.getColumnIndex(DbAdapter.POMERIGGIO_FINE)));
                if (!isNull(cursor, DbAdapter.OVERTIME))
                    turn.setOvertime(cursor.getString(cursor.getColumnIndex(DbAdapter.OVERTIME)));
                if (!isNull(cursor, DbAdapter.HOUR))
                    turn.setHour(cursor.getString(cursor.getColumnIndex(DbAdapter.HOUR)));
                turn.setIsImportante(cursor.getString(cursor.getColumnIndex(DbAdapter.PRIORITY)));
                cursor.close();
            } else
                turn = NullObjectStrategy.nullTurn();
            dbAdapter.close();
            return turn;
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public long insertWeek(Week week, Context context) {
        try {
            dbAdapter = new DbAdapter(context);
            dbAdapter.open();
            long id = dbAdapter.createWeek(week.getWeekId(), week.getYear(), week.getMounth(), week.getHour(), week.getExtraHour());
            dbAdapter.close();
            return id;
        } finally {
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public boolean updateWeek(Week week, Context context) {
        try {
            dbAdapter = new DbAdapter(context);
            dbAdapter.open();
            boolean value = dbAdapter.updateWeek(week.getId(), week.getWeekId(), week.getYear(), week.getMounth(), week.getHour(), week.getExtraHour());
            dbAdapter.close();
            return value;
        } finally {
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public Week getWeeekByCorrelationId(int year, int weekid, Context context) {
        Cursor cursor = null;
        try {
            dbAdapter = new DbAdapter(context);

            Week week = new Week(context);
            dbAdapter.open();
            cursor = dbAdapter.fetchWeekByCorrelationID(year, weekid);
            if (cursor != null && cursor.moveToFirst()) {
                week.setId(cursor.getInt(cursor.getColumnIndex(DbAdapter.ID)));
                week.setWeekId(cursor.getInt(cursor.getColumnIndex(DbAdapter.WEEK_ID)));
                week.setYear(cursor.getInt(cursor.getColumnIndex(DbAdapter.YEAR)));
                week.setMounth(cursor.getInt(cursor.getColumnIndex(DbAdapter.MOUNTH)));
                week.setHours(cursor.getDouble(cursor.getColumnIndex(DbAdapter.HOUR)));
                week.setExtraHour(cursor.getDouble(cursor.getColumnIndex(DbAdapter.OVERTIME)));
                dbAdapter.close();
                return week;
            } else {
                dbAdapter.close();
                return null;
            }
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    @SuppressWarnings("static-access")
    public Week getWeekbySelectedDay(Turn day, Context context) {
        Cursor cursor = null;
        try {
            dbAdapter = new DbAdapter(context);

            Week week = new Week(context);
            dbAdapter.open();
            cursor = dbAdapter.fetchWeekByCorrelationID(day.getYear(), day.getWeekId());
            if (cursor != null && cursor.moveToFirst()) {
                week.setId(cursor.getInt(cursor.getColumnIndex(dbAdapter.ID)));
                week.setWeekId(cursor.getInt(cursor.getColumnIndex(dbAdapter.WEEK_ID)));
                week.setYear(cursor.getInt(cursor.getColumnIndex(dbAdapter.YEAR)));
                week.setMounth(cursor.getInt(cursor.getColumnIndex(dbAdapter.MOUNTH)));
                week.addHour(cursor.getDouble(cursor.getColumnIndex(dbAdapter.HOUR)));
                week.setExtraHour(cursor.getDouble(cursor.getColumnIndex(dbAdapter.OVERTIME)));
                dbAdapter.close();
                return week;
            } else {
                dbAdapter.close();
                return null;
            }
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    @SuppressWarnings("static-access")
    public List<Week> getMounth(int mounth, int year, Context context) {
        Cursor cursor = null;
        try {
            dbAdapter = new DbAdapter(context);

            Week week = null;
            List<Week> weekList = new ArrayList<Week>();
            dbAdapter.open();
            cursor = dbAdapter.fetchMount(mounth, year);
            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    week = new Week(context);
                    week.setId(cursor.getInt(cursor.getColumnIndex(dbAdapter.ID)));
                    week.setWeekId(cursor.getInt(cursor.getColumnIndex(dbAdapter.WEEK_ID)));
                    week.setYear(cursor.getInt(cursor.getColumnIndex(dbAdapter.YEAR)));
                    week.setMounth(cursor.getInt(cursor.getColumnIndex(dbAdapter.MOUNTH)));
                    week.addHour(cursor.getDouble(cursor.getColumnIndex(dbAdapter.HOUR)));
                    week.setExtraHour(cursor.getDouble(cursor.getColumnIndex(dbAdapter.OVERTIME)));
                    weekList.add(week);

                }
                dbAdapter.close();
                return weekList;
            } else
                return weekList;
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    @SuppressWarnings("static-access")
    public List<Week> getYear(int year, Context context) {
        Cursor cursor = null;
        try {
            dbAdapter = new DbAdapter(context);

            Week week = null;
            List<Week> weekList = new ArrayList<Week>();
            dbAdapter.open();
            cursor = dbAdapter.fetchYear(year);
            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    week = new Week(context);
                    week.setId(cursor.getColumnIndex(dbAdapter.ID));
                    week.setWeekId(cursor.getColumnIndex(dbAdapter.WEEK_ID));
                    week.setYear(cursor.getColumnIndex(dbAdapter.YEAR));
                    week.setYear(cursor.getColumnIndex(dbAdapter.MOUNTH));
                    week.addHour(cursor.getColumnIndex(dbAdapter.HOUR));
                    week.setExtraHour(cursor.getColumnIndex(dbAdapter.OVERTIME));
                    weekList.add(week);

                }
                return weekList;
            } else
                return weekList;
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }


    private boolean isNull(Cursor cursor, String field) {
        return "null:null".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(field)));
    }

    public int existTurn(String referenceDate, Context context) throws ParseException {
        Cursor cursor = null;
        try {
            int i;
            dbAdapter = new DbAdapter(context);
            dbAdapter.open();

            cursor = dbAdapter.fetchTurnByDate(referenceDate);
            if (cursor.getCount() != 0 && cursor.moveToFirst())
                i = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DbAdapter.ID)));
            else
                i = 0;

            return i;
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public void insertProperty(Property property, Context context) {
        try {

            if (existPropery(property, context) != 0) {
                dbAdapter = new DbAdapter(context);
                dbAdapter.open();
                dbAdapter.updateProperty(property.getProperty(), property.getValue());
            } else {
                dbAdapter = new DbAdapter(context);
                dbAdapter.open();
                dbAdapter.creaProperty(property.getProperty(), property.getValue());
            }

        } finally {
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public void insertProperties(List<Property> properties, Context context) {
        for (Property property : properties) {
            insertProperty(property, context);
        }
    }

    public List<Property> getProperies(Context context) {
        Cursor cursor = null;
        try {
            List<Property> properties = new ArrayList<Property>();
            dbAdapter = new DbAdapter(context);
            dbAdapter.open();
            cursor = dbAdapter.fetchSetting();
            if (cursor != null && cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    Property property = new Property();
                    cursor.moveToNext();
                    property.setProperty(cursor.getString(cursor.getColumnIndex(DbAdapter.PROPERTY)));
                    property.setValue(cursor.getString(cursor.getColumnIndex(DbAdapter.VALUE)));
                    properties.add(property);
                }
                return properties;
            } else
                return properties;
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public Property getProperty(String reqProperty, Context context) {
        Cursor cursor = null;
        try {
            Property property = new Property();
            dbAdapter = new DbAdapter(context);
            dbAdapter.open();
            cursor = dbAdapter.fetchProperty(reqProperty);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                property.setProperty(cursor.getString(cursor.getColumnIndex(DbAdapter.PROPERTY)));
                property.setValue(cursor.getString(cursor.getColumnIndex(DbAdapter.VALUE)));
                return property;
            } else
                return null;
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public int existPropery(Property property, Context context) {
        Cursor cursor = null;
        try {
            dbAdapter = new DbAdapter(context);
            dbAdapter.open();
            cursor = dbAdapter.fetchProperty(property.getProperty());
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                int i = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DbAdapter.ID)));
                return i;
            } else
                return 0;
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public int existPropery(String property, Context context) {
        Cursor cursor = null;
        try {
            dbAdapter = new DbAdapter(context);
            dbAdapter.open();
            cursor = dbAdapter.fetchProperty(property);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                int i = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DbAdapter.ID)));
                return i;
            } else
                return 0;
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }
}

