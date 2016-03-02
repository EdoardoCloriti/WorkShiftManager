package com.cloriti.workshiftmanager.util.tutorial;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.cloriti.workshiftmanager.R;


/**
 * Created by Edoardo on 27/01/2016.
 */
public class WorkshiftManagerTutorial {

    public static final String WORK_SHIFT_MANAGER = "WorkShiftManager";
    public static final String WORK_SHFIT_MANAGER_SETTING = "WorkShiftManagerSetting";
    public static final String DISPLAY = "DisplayTurn";
    public static final String MULTI_SELECTION_MENU = "MultiSelectionMenu";
    public static final String MANAGE_WORKSHIFT = "ManageWorkshift";
    public static final String ADD_OVERTIME = "AddOvertime";
    public static final String STARLING_HOURS = "StarlingHours";
    public static final String DISPLAY_MONTH = "DisplayMounth";
    public static final String DISPLAY_YEAR = "DisplayYear";
    public static final String DISPLAY_WEEK = "DisplayHourWeek";

    public static void showWorkShiftManagerTurorial(Context context, String tutorialName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.tutorial_title));
        builder.setMessage(context.getString(getMessageId(context, tutorialName)));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton(context.getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog d = builder.create();
        d.show();

    }

    private static int getMessageId(Context context, String tutorialName) {

        if (WORK_SHIFT_MANAGER.equals(tutorialName))
            return R.string.tutorial_WorkShfitManager;
        else if (WORK_SHFIT_MANAGER_SETTING.equals(tutorialName))
            return R.string.tutorial_WorkShfitManager_setting;
        else if (MULTI_SELECTION_MENU.equals(tutorialName))
            return R.string.tutorial_MultiSelectionMenu;
        else if (MANAGE_WORKSHIFT.equals(tutorialName))
            return R.string.tutorial_ManageWorkshift;
        else if (ADD_OVERTIME.equals(tutorialName))
            return R.string.tutorial_AddOvertime;
        else if (STARLING_HOURS.equals(tutorialName))
            return R.string.tutorial_StarlingHour;
        else if (DISPLAY.equals(tutorialName))
            return R.string.tutorial_display_turn;
        else if (DISPLAY_MONTH.equals(tutorialName))
            return R.string.tutorial_display_month;
        else if (DISPLAY_YEAR.equals(tutorialName))
            return R.string.tutorial_display_year;
        else if (DISPLAY_WEEK.equals(tutorialName))
            return R.string.tutorial_display_week;
        else
            return R.string.tutorial_not_available;

    }
}
