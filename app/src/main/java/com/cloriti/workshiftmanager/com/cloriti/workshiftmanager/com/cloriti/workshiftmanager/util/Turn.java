package com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.com.cloriti.workshiftmanager.util;


import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Turn {
    public static final String PATTERN = "dd/MM/yyyy";

    private int id = 0;
    private int weekId;
    private int year;
    private int mounth;
    private String datariferimento = null;

    private Integer inizioMattinaH = null;
    private Integer fineMattinaH = null;
    private Integer inizioMattinaM = null;
    private Integer fineMattinaM = null;

    private Integer inizioPomeriggioH = null;
    private Integer finePomeriggioH = null;
    private Integer inizioPomeriggioM = null;
    private Integer finePomeriggioM = null;

    private double hour = 0;
    private double overtime = 0;

    private boolean isImportante = false;

    // costruttore con Bundle
    public static final Turn turnByBundle(Bundle bundle) {
        Turn turn = new Turn();
        turn.setDatariferimento(bundle.getString(IDs.DATA));
        turn.setMounth(bundle.getInt(IDs.MONTH));
        if (bundle.containsKey(IDs.INIZIO_MATTINA))
            turn.setIniziotMattina(intervalIsNotNull(bundle.getString(IDs.INIZIO_MATTINA)) ? bundle.getString(IDs.INIZIO_MATTINA) : null);
        if (bundle.containsKey(IDs.FINE_MATTINA))
            turn.setFineMattina(intervalIsNotNull(bundle.getString(IDs.FINE_MATTINA)) ? bundle.getString(IDs.FINE_MATTINA) : null);
        if (bundle.containsKey(IDs.INIZIO_POMERIGGIO))
            turn.setIniziotPomeriggio(intervalIsNotNull(bundle.getString(IDs.INIZIO_POMERIGGIO)) ? bundle.getString(IDs.INIZIO_POMERIGGIO) : null);
        if (bundle.containsKey(IDs.FINE_POMERIGGIO))
            turn.setFinePomeriggio(intervalIsNotNull(bundle.getString(IDs.FINE_POMERIGGIO)) ? bundle.getString(IDs.FINE_POMERIGGIO) : null);
        if (bundle.containsKey(IDs.ORE))
            turn.setHour(new Double(bundle.getDouble(IDs.ORE, 0)));
        if (bundle.containsKey(IDs.OVERTIME))
            turn.setHour(new Double(bundle.getDouble(IDs.OVERTIME, 0)));
        turn.setIsImportante(bundle.getInt(IDs.PRIORITY));
        return turn;
    }

    public static boolean intervalIsNotNull(String interval) {
        return interval == null || "null:null".equals(interval) ? false : true;
    }

    public static final Intent intentByTurn(Intent i, Turn turn) {
        i.putExtra(IDs.ID, turn.getId());
        i.putExtra(IDs.DATA, turn.getDataRierimentoDate());
        i.putExtra(IDs.INIZIO_MATTINA, turn.getInizioMattina());
        i.putExtra(IDs.FINE_MATTINA, turn.getFineMattina());
        i.putExtra(IDs.INIZIO_POMERIGGIO, turn.getInizioPomeriggio());
        i.putExtra(IDs.FINE_POMERIGGIO, turn.getFinePomeriggio());
        i.putExtra(IDs.WEEK_ID, turn.getWeekId());
        i.putExtra(IDs.YEAR, turn.getYear());
        i.putExtra(IDs.MONTH, turn.getMounth());
        i.putExtra(IDs.ORE, turn.getHour());
        i.putExtra(IDs.OVERTIME, turn.getOvertime());
        i.putExtra(IDs.PRIORITY, turn.getIsImportante() ? 1 : 0);
        return i;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setcorrelationKey(int dd, int MM, int yyyy) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(yyyy, MM, dd);
        this.weekId = calendar.get(Calendar.WEEK_OF_YEAR);
        this.year = yyyy;
        this.mounth = MM;
    }

    public void setcorrelationKeyWithWeekId(int weekId, int MM, int yyyy) {
        this.weekId = weekId;
        this.year = yyyy;
        this.mounth = MM;
    }

    public void setHour(double hour) {
        this.hour = hour;
    }

    public void setHour() {
        double tmpHour = 0;
        if (inizioMattinaH != null && inizioMattinaM != null && fineMattinaH != null && fineMattinaM != null) {
            double tmpInizioMattina = inizioMattinaH + convertMinute(inizioMattinaM);
            double tmpFineMattina = fineMattinaH + convertMinute(fineMattinaM);
            tmpHour = tmpHour + (tmpFineMattina - tmpInizioMattina);
        }
        if (inizioPomeriggioH != null && inizioPomeriggioM != null && finePomeriggioH != null && finePomeriggioM != null) {
            double tmpInizioPomeriggio = inizioPomeriggioH + convertMinute(inizioPomeriggioM);
            double tmpFinePomeriggio = finePomeriggioH + convertMinute(finePomeriggioM);
            tmpHour = tmpHour + (tmpFinePomeriggio - tmpInizioPomeriggio);
        }
        this.hour = tmpHour;
    }

    public void setOvertime(double overtime) {
        this.overtime = overtime;
    }

    public void setIsImportante(boolean isImportante) {
        this.isImportante = isImportante;
    }

    public void setIsImportante(String isImportante) {
        this.isImportante = !"0".equals(isImportante);
    }

    // getter
    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public int getWeekId() {
        return weekId;
    }

    public void setWeekId(int weekId) {
        this.weekId = weekId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDatariferimento() {
        return datariferimento;
    }

    public void setDatariferimento(String datariferimento) {
        if (year == 0)
            year = new Integer(datariferimento.substring(6, 10)).intValue();
        if (mounth == 0)
            mounth = new Integer(datariferimento.substring(3, 5)).intValue();
        if (weekId == 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
                Calendar selected = Calendar.getInstance();
                selected.setTime(sdf.parse(datariferimento));
                weekId = selected.get(Calendar.WEEK_OF_YEAR);
            } catch (Exception e) {
                weekId = 0;
            }
        }
        this.datariferimento = datariferimento;
    }

    public String getDataRierimentoDateStr() {
        return datariferimento;
    }

    public Date getDataRierimentoDate() {
        Date data = null;
        try {
            data = new SimpleDateFormat(PATTERN).parse(datariferimento);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }

    public Integer getInizioMattinaH() {
        return inizioMattinaH;
    }

    public void setInizioMattinaH(Integer inizioMattinaH) {
        this.inizioMattinaH = inizioMattinaH;
    }

    public Integer getInizioMattinaM() {
        return inizioMattinaM;
    }

    public void setInizioMattinaM(Integer inizioMattinaM) {
        this.inizioMattinaM = inizioMattinaM;
    }

    public Integer getFineMattinaH() {
        return fineMattinaH;
    }

    public void setFineMattinaH(Integer fineMattinaH) {
        this.fineMattinaH = fineMattinaH;
    }

    public Integer getFineMattinaM() {
        return fineMattinaM;
    }

    public void setFineMattinaM(Integer fineMattinaM) {
        this.fineMattinaM = fineMattinaM;
    }

    public Integer getFinePomeriggioH() {
        return finePomeriggioH;
    }

    public void setFinePomeriggioH(Integer finePomeriggioH) {
        this.finePomeriggioH = finePomeriggioH;
    }

    public Integer getFinePomeriggioM() {
        return finePomeriggioM;
    }

    public void setFinePomeriggioM(Integer finePomeriggioM) {
        this.finePomeriggioM = finePomeriggioM;
    }

    public Integer getInizioPomeriggioH() {
        return inizioPomeriggioH;
    }

    public void setInizioPomeriggioH(Integer inizioPomeriggioH) {
        this.inizioPomeriggioH = inizioPomeriggioH;
    }

    public Integer getInizioPomeriggioM() {
        return inizioPomeriggioM;
    }

    public void setInizioPomeriggioM(Integer inizioPomeriggioM) {
        this.inizioPomeriggioM = inizioPomeriggioM;
    }

    public double getHour() {
        return hour;
    }

    public void setHour(String hour) {
        try {
            this.hour = new Double(hour).doubleValue();
        } catch (Throwable t) {
            this.hour = 0;
        }
    }

    public double getOvertime() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime = Double.parseDouble(overtime);
    }

    public int getMounth() {
        return mounth;
    }

    public void setMounth(int mounth) {
        this.mounth = mounth;
    }

    public Boolean getIsImportante() {
        return isImportante;
    }

    public void setIsImportante(int isImportante) {
        this.isImportante = 0 != isImportante;
    }

    // getter personalizzati
    public String getMattina() {
        return "" + inizioMattinaH + ":" + inizioMattinaM + "-" + fineMattinaH + ":" + fineMattinaM;
    }

    public String getPomeriggio() {
        return "" + inizioPomeriggioH + ":" + inizioPomeriggioM + "-" + finePomeriggioH + ":" + finePomeriggioM;
    }

    // setter personalizzati
    public void setIniziotMattina(String mattina) {
        if (mattina != null) {
            String[] singleTime = new String[2];
            singleTime = mattina.split(":");
            setInizioMattinaH(Integer.parseInt(singleTime[0]));
            setInizioMattinaM(Integer.parseInt(singleTime[1]));
        }
    }

    public void setIniziotPomeriggio(String pomeriggio) {
        if (pomeriggio != null) {
            String[] singleTime = new String[2];
            singleTime = pomeriggio.split(":");
            setInizioPomeriggioH(Integer.parseInt(singleTime[0]));
            setInizioPomeriggioM(Integer.parseInt(singleTime[1]));
        }
    }

    public String getInizioMattina() {
        if (inizioMattinaH != null && inizioPomeriggioM != null) {
            String h = inizioMattinaH.toString();
            String m = inizioMattinaM.toString();
            StringUtils.leftPad(h, 2, '0');
            StringUtils.rightPad(m, 2, '0');
            return h + ":" + m;
        } else
            return inizioMattinaH + ":" + inizioMattinaM;
    }

    public String getFineMattina() {
        if (fineMattinaH != null && fineMattinaM != null) {
            String h = fineMattinaH.toString();
            String m = fineMattinaM.toString();
            StringUtils.leftPad(h, 2, '0');
            StringUtils.rightPad(m, 2, '0');
            return h + ":" + m;
        } else
            return fineMattinaH + ":" + fineMattinaM;
    }

    public void setFineMattina(String mattina) {
        if (mattina != null) {
            String[] singleTime = new String[2];
            singleTime = mattina.split(":");
            setFineMattinaH(Integer.parseInt(singleTime[0]));
            setFineMattinaM(Integer.parseInt(singleTime[1]));
        }
    }

    public String getInizioPomeriggio() {
        if (inizioPomeriggioH != null && inizioPomeriggioM != null) {
            String h = inizioPomeriggioH.toString();
            String m = inizioPomeriggioM.toString();
            StringUtils.leftPad(h, 2, '0');
            StringUtils.rightPad(m, 2, '0');
            return h + ":" + m;
        } else
            return inizioPomeriggioH + ":" + inizioPomeriggioM;
    }

    public String getFinePomeriggio() {
        if (finePomeriggioM != null && finePomeriggioM != null) {
            String h = finePomeriggioH.toString();
            String m = finePomeriggioM.toString();
            StringUtils.leftPad(h, 2, '0');
            StringUtils.rightPad(m, 2, '0');
            return h + ":" + m;
        } else
            return finePomeriggioH + ":" + finePomeriggioM;
    }

    public void setFinePomeriggio(String pomeriggio) {
        if (pomeriggio != null) {
            String[] singleTime = new String[2];
            singleTime = pomeriggio.split(":");
            setFinePomeriggioH(Integer.parseInt(singleTime[0]));
            setFinePomeriggioM(Integer.parseInt(singleTime[1]));
        }
    }

    public void reset() {
        this.datariferimento = null;
        this.inizioMattinaH = null;
        this.inizioMattinaM = null;
        this.inizioPomeriggioH = null;
        this.inizioPomeriggioM = null;
        this.fineMattinaH = null;
        this.finePomeriggioM = null;
        this.isImportante = false;
    }

    private double convertMinute(int minute) {
        switch (minute) {
            case 15:
                return 0.25;
            case 30:
                return 0.50;
            case 45:
                return 0.75;
            default:
                return 0;
        }
    }

}
