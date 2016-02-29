package com.cloriti.workshiftmanager.util;

import android.content.Context;

import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.orion.workshiftmanager.util.db.AccessToDB;

public class Week {
    private int id;
    private int weekId;
    private int year;
    private int mounth;
    private double hour = 0;
    private double extraHour = 0;
    private Context context = null;
    private AccessToDB db = null;

    public Week(Context context) {
        this.context = context;
        db = new AccessToDB();
    }

    public void addHour(double hour) {
        Property oreSettimanaliPrp = db.getProperty(Property.ORESETTIMANALI, context);
        double oreSettimanali = Double.parseDouble(oreSettimanaliPrp.getValue());
        this.hour = this.hour + hour;
        if (this.hour > oreSettimanali)
            setExtraHour(this.hour - oreSettimanali);
    }

    public void setHours(double hour) {
        Property oreSettimanaliPrp = db.getProperty(Property.ORESETTIMANALI, context);
        double oreSettimanali = Double.parseDouble(oreSettimanaliPrp.getValue());
        this.hour = hour;
        if (this.hour > oreSettimanali)
            setExtraHour(this.hour - oreSettimanali);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getMounth() {
        return mounth;
    }

    public void setMounth(int mounth) {
        this.mounth = mounth;
    }

    public double getHour() {
        return hour;
    }

    public double getExtraHour() {
        return extraHour;
    }

    public void setExtraHour(double extraHour) {
        this.extraHour = this.extraHour + extraHour;
    }
}
