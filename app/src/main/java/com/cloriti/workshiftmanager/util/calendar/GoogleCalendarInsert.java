package com.cloriti.workshiftmanager.util.calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.util.Turn;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Edoardo on 06/04/2016.
 */
public class GoogleCalendarInsert extends AsyncTask<Void, Void, Integer> {

    private static final Integer RESULT_OK = new Integer(1);
    private static final Integer RESULT_KO = new Integer(-1);
    private static final Integer RESULT_KO_AND_RETRY = new Integer(-2);

    private Calendar service = null;
    private ProgressDialog progress = null;
    private Activity activity = null;
    private Context context;
    private Turn turn;

    public GoogleCalendarInsert(GoogleAccountCredential credential, Context context, Activity activity, Turn turn) {
        HttpTransport client = AndroidHttp.newCompatibleTransport();
        JsonFactory json = JacksonFactory.getDefaultInstance();
        service = new Calendar.Builder(client, json, credential).build();

        this.turn = turn;
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        AccessToDB db = new AccessToDB();
        Property property = db.getProperty(Property.SUMMARY, context);
        try {
            if (!existCalendar(service)) {
                addCalendar();
            }

            if (turn != null) {
                if (turn.isOnlyDelete())
                    deleteTurnOnGoogleCalendar(turn.getGoogleCalendarIDMattina(), turn.getGoogleCalendarIDPomeriggio());
                else
                    manageSynchTurn();
            }
        } catch (UserRecoverableAuthIOException ioe) {
            activity.startActivityForResult(ioe.getIntent(), 1001);
            return RESULT_KO_AND_RETRY;
        } catch (Throwable e) {
            return RESULT_KO;
        }
        return RESULT_OK;
    }

    private void manageSynchTurn() throws IOException, ParseException {
        java.util.Calendar sys = java.util.Calendar.getInstance();
        AccessToDB db = new AccessToDB();
        Property property = db.getProperty(Property.SUMMARY, context);
        String calendarSummary = property.getValue();
        String summaryEvent = "turn";
        String visibility = "private";
        java.util.Calendar beginM = null;
        java.util.Calendar endM = null;
        java.util.Calendar beginP = null;
        java.util.Calendar endP = null;

        deleteTurnOnGoogleCalendar(turn.getGoogleCalendarIDMattina2Rem(), turn.getGoogleCalendarIDPomeriggio2Rem());

        if (turn.getInizioMattinaM() != null && turn.getInizioMattinaH() != null) {
            beginM = turn.getDataRierimentoDate();
            endM = turn.getDataRierimentoDate();
            int minute = 0;
            int hour = 0;
            minute = turn.getInizioMattinaM();
            hour = turn.getInizioMattinaH();
            beginM.set(java.util.Calendar.MINUTE, minute);
            beginM.set(java.util.Calendar.HOUR, hour);

            minute = turn.getFineMattinaM();
            hour = turn.getFineMattinaH();
            endM.set(java.util.Calendar.MINUTE, minute);
            endM.set(java.util.Calendar.HOUR, hour);

            String id = insertEvent(service, calendarSummary, summaryEvent, null, beginM, endM, null, visibility);
            turn.setGoogleCalendarIDMattina(id);
        }

        if (turn.getInizioPomeriggioH() != null && turn.getInizioPomeriggioM() != null) {
            beginP = turn.getDataRierimentoDate();
            endP = turn.getDataRierimentoDate();
            int minute = 0;
            int hour = 0;
            minute = turn.getInizioPomeriggioM();
            hour = turn.getInizioPomeriggioH();
            beginP.set(java.util.Calendar.MINUTE, minute);
            beginP.set(java.util.Calendar.HOUR, hour);

            minute = turn.getFinePomeriggioM();
            hour = turn.getFinePomeriggioH();
            endP.set(java.util.Calendar.MINUTE, minute);
            endP.set(java.util.Calendar.HOUR, hour);

            String id = insertEvent(service, calendarSummary, summaryEvent, null, beginP, endP, null, visibility);
            turn.setGoogleCalendarIDPomeriggio(id);
        }
        int id = db.existTurn(turn.getDataRierimentoDateStr(), context);
        if (id != 0) {
            turn.setId(id);
            db.insertGoogleCalendarId(turn, context);
        }
    }

    private void deleteTurnOnGoogleCalendar(String idMattina2Rem, String idPomeriggio2Rem) throws IOException {
        AccessToDB db = new AccessToDB();
        Property property = db.getProperty(Property.SUMMARY, context);
        if (idMattina2Rem != null)
            deleteEvent(service, getIdFromSummary(service, property.getValue()), idMattina2Rem);
        if (idPomeriggio2Rem != null)
            deleteEvent(service, getIdFromSummary(service, property.getValue()), idPomeriggio2Rem);
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    protected void onPostExecute(Integer output) {
        if (output == RESULT_KO)
            Toast.makeText(context, "Impossibile sincronizzare l'applicazione con Google Calendar.", Toast.LENGTH_SHORT).show();
        if (output == RESULT_OK)
            Toast.makeText(context, "sincronizzazione con Google Calendar avvenuta con successo.", Toast.LENGTH_SHORT).show();
    }

    public boolean existCalendar(Calendar service) throws IOException {
        AccessToDB db = new AccessToDB();
        Property property = db.getProperty(Property.SUMMARY, context);
        String pageToken = null;
        boolean isPresent = false;
        CalendarList Calendarlist = service.calendarList().list().setPageToken(pageToken).execute();
        List<CalendarListEntry> list = Calendarlist.getItems();
        do {
            for (CalendarListEntry entry : list) {
                if ((property.getValue()).equalsIgnoreCase(entry.getSummary()))
                    isPresent = true;
            }
        }
        while (pageToken != null);
        return isPresent;
    }


    public void addCalendar() throws IOException {
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        AccessToDB db = new AccessToDB();
        Property property = db.getProperty(Property.SUMMARY, context);
        calendar.setSummary(property.getValue());
        calendar.setDescription("Calendario utilizzato per la sincronizzazione con l'applicazione WorkShiftManager per contenre i turni lavorativi registrati");
        service.calendars().insert(calendar).execute();
    }


    public String insertEvent(Calendar service, String summary, String eventSummary, String description, java.util.Calendar startDate, java.util.Calendar endDate, String timeZone, String visibility) throws IOException, ParseException {
        Event event = new Event();
        event.setSummary("Turn");
        event.setDescription("[" + turn.getId() + "][WorkShiftManager] Turn - hour: " + turn.getHour() + ", overtime: " + turn.getOvertime() + " - Priority:" + (turn.getIsImportante() ? " high." : " normal."));

        EventDateTime start = new EventDateTime();

        start.setDateTime(new DateTime(startDate.getTime()));
        if (isNullOrEmpry(timeZone))
            start.setTimeZone(timeZone);
        event.setStart(start);

        EventDateTime end = new EventDateTime();
        end.setDateTime(new DateTime(endDate.getTime()));
        if (isNullOrEmpry(timeZone))
            end.setTimeZone(timeZone);
        event.setEnd(end);

        event.setVisibility(visibility);
        event = service.events().insert(getIdFromSummary(service, summary), event).execute();

        return event.getId();
    }


    private String getIdFromSummary(Calendar service, String summary) throws IOException {
        String pageToken = null;
        CalendarList Calendarlist = service.calendarList().list().setPageToken(pageToken).execute();
        List<CalendarListEntry> list = Calendarlist.getItems();
        do {
            for (CalendarListEntry entry : list) {
                if (summary.equalsIgnoreCase(entry.getSummary()))
                    return entry.getId();
            }
        }
        while (pageToken != null);
        return null;
    }

    private boolean isNullOrEmpry(String field) {
        return field != null && !"".equals(field);
    }

    public void deleteEvent(Calendar service, String calendarId, String eventId) throws IOException {
        try {
            if (existEvent(service, calendarId, eventId))
                service.events().delete(calendarId, eventId).execute();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

    }

    public boolean existEvent(Calendar service, String calendarId, String eventId) throws IOException {
        String pageToken = null;
        do {
            Events events = service.events().list(calendarId).setPageToken(pageToken).execute();
            List<Event> list = events.getItems();
            for (Event event : list) {
                if (eventId.equals(event.getId()))
                    return true;
            }
        }
        while (pageToken != null);
        return false;
    }
}
