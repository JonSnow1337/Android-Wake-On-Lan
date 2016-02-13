package com.teamgy.wakeonlan.gui.settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.teamgy.wakeonlan.utils.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jedi-Windows on 05.02.2016..
 */
public class TimePickerPreference extends DialogPreference {


    private TimePicker timePicker = null;
    private boolean is24HourTime;
    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        timePicker = new TimePicker(getContext());
        timePicker.setIs24HourView(is24HourTime);
        int [] loadedTime = Tools.loadJsonTime(getContext(),this.getKey());
        if(loadedTime != null){
           timePicker.setCurrentHour(loadedTime[0]);
           timePicker.setCurrentMinute(loadedTime[1]);
        }
        return timePicker;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        this.is24HourTime = DateFormat.is24HourFormat(getContext());
        int [] loadedTime = Tools.loadJsonTime(getContext(),this.getKey());
        try {
            if(loadedTime != null){
                setSummaryTimeFormat(loadedTime[0], loadedTime[1], is24HourTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return super.onCreateView(parent);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        if(positiveResult){
            displaySummary();
            Tools.saveJsonTime(getContext(),getKey(),timePicker.getCurrentHour(),timePicker.getCurrentMinute());
        }
    }

    private void displaySummary(){
        int hour = timePicker.getCurrentHour();
        int minutes = timePicker.getCurrentMinute();

        try {
            setSummaryTimeFormat(hour,minutes,is24HourTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setSummaryTimeFormat(int hour, int minutes, boolean is24Hour) throws ParseException {

        if(is24Hour){
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
            Date date = displayFormat.parse(hour + ":" + minutes);
            setSummary(displayFormat.format(date)); // :)
        }else{
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
            if(hour >=12){
                hour -=12;
                Date date = displayFormat.parse(hour + ":" + minutes + " PM");
                setSummary(displayFormat.format(date)); // :)
            }else{
                Date date = displayFormat.parse(hour + ":" + minutes + " AM");
                setSummary(displayFormat.format(date)); // :)
            }
        }

    }
}

