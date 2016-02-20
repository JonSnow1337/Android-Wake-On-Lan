package com.teamgy.wakeonlan.data;

import java.io.Serializable;

/**
 * Created by Jakov on 01/11/2015.
 */
public class PCInfo implements Serializable {
    private String macAdress;
    private String pcName;
    private boolean onWifiEnabled = false;
    private boolean onAlarmEnabled = false;
    private boolean [] alarmDays = new boolean[7];

    private static final long serialVersionUID = 1L;


    public PCInfo(String macAdress, String ssid) {

        setMacAdress(macAdress);
        setPcName(ssid);
        setOnWifiEnabled(false);
    }

    public PCInfo(String macAdress, String ssid, boolean onWifiEnabled) {

        setMacAdress(macAdress);
        setPcName(ssid);
        setOnWifiEnabled(onWifiEnabled);
    }

    public PCInfo(String macAdress, String pcName, boolean onWifiEnabled, boolean onAlarmEnabled, boolean[] alarmDays) {
        this.alarmDays = alarmDays;
        this.macAdress = macAdress;
        this.pcName = pcName;
        this.onWifiEnabled = onWifiEnabled;
        this.onAlarmEnabled = onAlarmEnabled;
    }

    public boolean isOnWifiEnabled() {
        return onWifiEnabled;
    }

    public void setOnWifiEnabled(boolean onWifiEnabled) {
        this.onWifiEnabled = onWifiEnabled;
    }

    public String getMacAdress() {
        return macAdress;
    }

    public void setMacAdress(String macAdress) {
        this.macAdress = macAdress;
    }

    public String getPcName() {
        return pcName;
    }

    public void setPcName(String pcName) {
        this.pcName = pcName;
    }

    public boolean isOnAlarmEnabled() {
        return onAlarmEnabled;
    }

    public void setOnAlarmEnabled(boolean onAlarmEnabled) {
        this.onAlarmEnabled = onAlarmEnabled;
    }

    public boolean[] getAlarmDays() {
        return alarmDays;
    }

    public void setAlarmDays(boolean[] alarmDays) {
        this.alarmDays = alarmDays;
    }
    public void setAlarmDay(Day d, boolean isEnabled) {
        alarmDays[d.ordinal()] = isEnabled;
    }

}
