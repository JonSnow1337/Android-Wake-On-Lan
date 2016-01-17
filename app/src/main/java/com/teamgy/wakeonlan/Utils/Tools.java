package com.teamgy.wakeonlan.utils;

import android.util.Log;
import android.widget.CheckBox;

import com.teamgy.wakeonlan.PCInfo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Jakov on 02/11/2015.
 */
public final class Tools {
    private Tools () { // private constructor
    }
    public static void changeCheckboxState(CheckBox checkBox){

        if(checkBox.isChecked()){
            checkBox.setChecked(false);
        }
        else{
            checkBox.setChecked(true);
        }
    }
    public static int booleanToInt(boolean bool){
        if(bool){
            return 1;
        }
        return 0;
    }
    public static boolean intToBoolean(int i){
        return i == 1;
    }
    public static ArrayList<String> pcInfosToMacArrayList(ArrayList<PCInfo> pcInfos){
        ArrayList<String> macArraylist = new ArrayList<String>();
        for (PCInfo pcInfo : pcInfos) {
            macArraylist.add(pcInfo.getMacAdress());

        }
        return macArraylist;

    }

    public static ArrayList<String> getEnabledMacs(ArrayList<PCInfo> pcInfos){
        ArrayList<String> enabledMacs = new ArrayList<>();

        for(PCInfo pcInfo: pcInfos){
            if(pcInfo.isEnabled()){
                enabledMacs.add(pcInfo.getMacAdress());
            }
        }
        return enabledMacs;
    }

    public static String reformatMACInput(String input){
        //this method reformats mac to correct format
        //user can input spaces or whatever
        //this overwrites it to correct format

        //get out all numbers and characters from string
        //merge them back
        String formattedString = "";
        input = input.replaceAll("\\s+","");


        String pattern = "[abcdef0-9]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);

        while(m.find()){

            formattedString += m.group();

        }

        if(formattedString.length() > 12 && formattedString.length() > 0){
           try{
                formattedString = formattedString.substring(0,12);

            }catch (IndexOutOfBoundsException e){

                Log.d("exception","lenght is " +  formattedString.length());
            }


        }
        return formattedString;

    }
}