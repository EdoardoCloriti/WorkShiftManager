package com.cloriti.workshiftmanager.util.db;

import android.content.Context;
import android.database.Cursor;
import android.net.ParseException;

import com.cloriti.workshiftmanager.util.NullObjectStrategy;
import com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.util.Week;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utility per l'accesso semplificato al database
 *
 * @Author: edoardo.cloriti@studio.unibo.it
 */
public class AccessToDB {

    private DbAdapter dbAdapter = null;

    /**
     * metodo che effettua <code>insert</code>/<code>update</code> del <code>Turn</code> e del <code>Week</code>
     *
     * @param turn
     * @param context
     * @throws ParseException
     */
    public void insertTurn(Turn turn, Context context) throws ParseException {
        try {
            int id = 0;
            Week week = null;
            /**
             * Gestione del Turn
             */

            //controllo dell'esistenza del turno du db
            if ((id = existTurn(turn.getDataRierimentoDateStr(), context)) != 0) {
                //se esiste effettua l'update
                dbAdapter = new DbAdapter(context);
                dbAdapter.open();
                dbAdapter.updateTurn(id, turn.getWeekId(), turn.getYear(), turn.getDataRierimentoDateStr(), turn.getInizioMattina(), turn.getFineMattina(), turn.getInizioPomeriggio(), turn.getFinePomeriggio(), turn.getOvertime(), turn.getHour(), new Long(turn.getIsImportante() ? 1 : 0));
                dbAdapter.close();
            } else {
                //se non esiste effettu la insert
                dbAdapter = new DbAdapter(context);
                dbAdapter.open();
                dbAdapter.createTurn(turn.getDataRierimentoDateStr(), turn.getWeekId(), turn.getYear(), turn.getInizioMattina(), turn.getFineMattina(), turn.getInizioPomeriggio(), turn.getFinePomeriggio(), turn.getOvertime(), turn.getHour(), new Long(turn.getIsImportante() ? 1 : 0));
                dbAdapter.close();
            }
            dbAdapter.close();

            /**
             * Gestione del Week
             */

            //controllo dell'esistenza del week
            if ((week = getWeeekByCorrelationId(turn.getYear(), turn.getWeekId(), context)) == null) {
                //se non esiste lo crea e effettua la insert
                week = new Week(context);
                week.setWeekId(turn.getWeekId());
                week.setYear(turn.getYear());
                week.setMounth(turn.getMounth());
                week.addHour(turn.getHour());
                week.setExtraHour(turn.getOvertime());
                insertWeek(week, context);
            } else {
                //se esiste lo aggiorna e effettua l'update
                week.addHour(turn.getHour());
                week.setExtraHour(turn.getOvertime());
                updateWeek(week, context);
            }
        } finally {
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    public void insertGoogleCalendarId(Turn turn, Context context) {
        dbAdapter = new DbAdapter(context);
        dbAdapter.open();
        dbAdapter.updateTurnWithGoogleCalendarId(turn.getId(), turn.getWeekId(), turn.getYear(), turn.getDataRierimentoDateStr(), turn.getInizioMattina(), turn.getFineMattina(), turn.getInizioPomeriggio(), turn.getFinePomeriggio(), turn.getOvertime(), turn.getHour(), new Long(turn.getIsImportante() ? 1 : 0), turn.getGoogleCalendarIDMattina(), turn.getGoogleCalendarIDPomeriggio());
        dbAdapter.close();
    }

    /**
     * metodo per l'eliminazione del turno e aggiornamento della settimana di riferimento
     *
     * @param turn
     * @param context
     * @return
     */
    public boolean deleteTurnAndUpdateWeek(Turn turn, Context context) {
        dbAdapter = new DbAdapter(context);
        try {
            dbAdapter.open();
            //se il turno esiste e l'eliminazione avviene con successo
            if (turn.getId() != 0 && dbAdapter.deleteTurn(turn.getId())) {
                //effettua l'aggiornamento della settimana
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

    /**
     * metodo per l'eliminazione del turno
     *
     * @param turn
     * @param context
     * @return
     */
    public boolean deleteTurn(Turn turn, Context context) {
        dbAdapter = new DbAdapter(context);
        try {
            dbAdapter.open();
            return dbAdapter.deleteTurn(turn.getId());
        } finally {
            dbAdapter.close();
        }
    }

    /**
     * eliminazione di una lista di turni
     *
     * @param turns
     * @param context
     */
    public void deleteTurns(List<Turn> turns, Context context) {
        for (Turn turn : turns) {
            deleteTurn(turn, context);
        }
    }

    /**
     * pulizia dei turni in base all'anno passato come parametro
     *
     * @param year
     * @param context
     */
    public void clearTurnByYear(int year, Context context) {
        List<Turn> turns = new ArrayList<Turn>();
        dbAdapter = new DbAdapter(context);
        try {
            dbAdapter.open();
            //estrazione dei turni dell'anno
            Cursor cursor = dbAdapter.fetchTurnByYear(year);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    //preparazione del turno all'eliminazione
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
            //eliminazione dei turni estratti
            deleteTurns(turns, context);
        } finally {
            if (dbAdapter != null)
                dbAdapter.close();
        }

    }

    /**
     * metodo per l'eliminazione di una settimana
     *
     * @param week
     * @param context
     */
    public void deleteWeek(Week week, Context context) {
        dbAdapter = new DbAdapter(context);
        try {
            dbAdapter.open();
            dbAdapter.deleteWeek(week.getId());
        } finally {
            dbAdapter.close();
        }
    }

    /**
     * metodo per l'eliminazione di una lista di settimane
     *
     * @param weeks
     * @param context
     */
    public void deleteWeeks(List<Week> weeks, Context context) {
        for (Week week : weeks) {
            deleteWeek(week, context);
        }
    }


    /**
     * metodo per la pulizia delle sttimane da anno x a anno y passati come parametro
     *
     * @param yearFrom
     * @param yearTo
     * @param context
     */
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

    /**
     * metodo per l'estrazione di un turno in base alla data passata come parametro
     *
     * @param day
     * @param context
     * @return
     */
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
                if (cursor.getString(cursor.getColumnIndex(DbAdapter.GOOGLE_ID_MATTINA)) != null)
                    turn.setGoogleCalendarIDMattina(cursor.getString(cursor.getColumnIndex(DbAdapter.GOOGLE_ID_MATTINA)));
                if (cursor.getString(cursor.getColumnIndex(DbAdapter.GOOGLE_ID_POMERIGGIO)) != null)
                    turn.setGoogleCalendarIDPomeriggio(cursor.getString(cursor.getColumnIndex(DbAdapter.GOOGLE_ID_POMERIGGIO)));
                cursor.close();
            } else
                turn = NullObjectStrategy.nullTurn();
            dbAdapter.close();
            return turn;
        } catch (Throwable t) {
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
            if (dbAdapter != null)
                dbAdapter.close();
        }
    }

    /**
     * metodo per l'inserimento della settimana
     *
     * @param week
     * @param context
     * @return
     */
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

    /**
     * metodo per l'aggiornamento della settimana passat come parametro
     *
     * @param week
     * @param context
     * @return
     */
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

    /**
     * metodo per estrare una settimana tramite anno e id della settimana (correlation id)
     *
     * @param year
     * @param weekid
     * @param context
     * @return
     */
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

    /**
     * metodo che dato un mese e un anno restitusce la lista di settimane che compongono il mese
     *
     * @param mounth
     * @param year
     * @param context
     * @return
     */
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


    /**
     * metodo per verificare se un orario è null (null:null)
     *
     * @param cursor
     * @param field
     * @return
     */
    private boolean isNull(Cursor cursor, String field) {
        return "null:null".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(field)));
    }

    /**
     * metodo per contollare se esiste un turno
     * restitusce l'id se esiste 0 altrimenti
     *
     * @param referenceDate
     * @param context
     * @return
     * @throws ParseException
     */
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

    /**
     * metodo per inserire un Property (Setting)
     *
     * @param property
     * @param context
     */
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

    /**
     * metodo che dato il nome di una property restituisce la property salvata su DB se esiste null altrimenti
     *
     * @param reqProperty
     * @param context
     * @return
     */
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

    /**
     * metodo che controlla l'esistenza di una propertyt su DB tramite un istanza di essa
     *
     * @param property
     * @param context
     * @return
     */
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

    /**
     * metodo per controllare l'esistenza di una property su DB tramite il nome
     *
     * @param property
     * @param context
     * @return
     */
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

