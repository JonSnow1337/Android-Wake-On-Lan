package com.teamgy.wakeonlan.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.CheckBox;

import com.teamgy.wakeonlan.data.PCInfo;

import org.json.JSONArray;
import org.json.JSONException;

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

    public static String reformatMACInput(String input, boolean cutOverLimit){
        //this method reformats mac to correct format
        //user can input spaces or whatever
        //this overwrites it to correct format

        //get out all numbers and characters from string
        //merge them back
        String formattedString = "";
        input  = input.toLowerCase();
        input = input.replaceAll("\\s+","");


        String pattern = "[abcdef0-9]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);

        while(m.find()){

            formattedString += m.group();

        }

        if(cutOverLimit){
            if(formattedString.length() > 12 && formattedString.length() > 0){
                try{
                    formattedString = formattedString.substring(0,12);

                }catch (IndexOutOfBoundsException e){

                    Log.d("exception","lenght is " +  formattedString.length());
                }
            }
        }
        return formattedString;

    }


    public static void circularRevealShow( View myView){
        // previously invisible view


        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (myView.isAttachedToWindow()){
                //this is to avoid crash when back is pressed instantly after animation starts
                Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
                myView.setVisibility(View.VISIBLE);
                anim.start();
            }
        }
        else{

            //TODO
            //add animation for sdk <5.0
        }




    }
    public static void circularRevealShow(View myView,int cx, int cy){
        // previously invisible view

        // get the center for the clipping circle

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, 1500);
        }
        else{

            //TODO
            //add animation for sdk <5.0
        }
        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();

    }
    public static void circularRevealHide(final View myView) {
        // previously visible view

        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
        } else {

            //TODO
            //add animation for sdk <5.0
        }

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();

    }

    public  static void backgroudTransition(View v){
        //has to have 2 colors in drawable as background
        TransitionDrawable transition = (TransitionDrawable) v.getBackground();
        transition.startTransition(500);

    }


    public static boolean isMacValid(String input) {
        //checks to see if letters and numbers adds up to 12
        input = input.replaceAll("\\s+","");
        input  = input.toLowerCase();
        String pattern = "[a-f0-9]";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        int macValidCounter = 0;
        while(m.find()){
            macValidCounter ++;
        }

        String invalidPattern = "[g-z]";
        p = Pattern.compile(invalidPattern);
        m = p.matcher(input);
        while(m.find()){
            return false;
        }
        return macValidCounter == 12;

    }

    public static String findBadMACCharacters(String input){
        input = input.replaceAll("\\s+","");
        input  = input.toLowerCase();
        String pattern = "[g-z]";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);

        String badCharacters = "";
        while(m.find()){
            badCharacters += m.group();
        }

        return badCharacters;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void openURL(String url,Context c){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(i.FLAG_ACTIVITY_NEW_TASK); //crash without this..
        i.setData(Uri.parse(url));
        c.startActivity(i);
    }

    public static int [] loadJsonTime(Context c, String key){
        /**
         * Returns time as int arr
         * [0] = hour
         * [1] = minute
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        String savedJSONTime = prefs.getString(key, "0,0");
        int [] returnArray = new int[2];
        try {
            JSONArray jsonArray = new JSONArray(savedJSONTime);
            returnArray[0] = (int)jsonArray.get(0);
            returnArray[1] = (int) jsonArray.get(1);
        } catch (JSONException e) {
            e.printStackTrace();
            returnArray = null;
        }
        return returnArray;
    }

    public static void saveJsonTime(Context c, String key, int hour, int minutes) {
        SharedPreferences prefs  = PreferenceManager.getDefaultSharedPreferences(c);

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(hour);
        jsonArray.put(minutes);
        prefs.edit().putString(key, jsonArray.toString()).apply();
    }
}