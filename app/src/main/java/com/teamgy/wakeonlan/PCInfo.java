package com.teamgy.wakeonlan;

import java.io.Serializable;

/**
 * Created by Jakov on 01/11/2015.
 */
public class PCInfo  implements Serializable{
    private String macAdress;
    private String pcName;
    private boolean enabled = false;

    public PCInfo(String macAdress,String ssid){

        setMacAdress(macAdress);
        setPcName(ssid);
        setEnabled(false);
    }
    public PCInfo(String macAdress,String ssid,boolean enabled){

        setMacAdress(macAdress);
        setPcName(ssid);
        setEnabled(enabled);
    }
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
}
