package com.teamgy.wakeonlan;

import com.teamgy.wakeonlan.sendWol.WakeOnHomeWifiReciever;

import junit.framework.Assert;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Jedi-Windows on 07.02.2016..
 */
public class TImeRangeTests {
    @Test
    public void test_timeRange_valid(){
        WakeOnHomeWifiReciever reciever = new WakeOnHomeWifiReciever();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try{

            Date timeStart = dateFormat.parse("10:30");
            Date timeEnd = dateFormat.parse("13:45");
            Date timeNow = dateFormat.parse("12:15");
            Assert.assertEquals(true,reciever.isTimeInRange(timeStart,timeEnd,timeNow));

        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void test_timeRange_invalid(){
        WakeOnHomeWifiReciever reciever = new WakeOnHomeWifiReciever();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try{
            Date timeStart = dateFormat.parse("10:30");
            Date timeEnd = dateFormat.parse("13:45");
            Date timeNow = dateFormat.parse("15:15");
            Assert.assertEquals(false,reciever.isTimeInRange(timeStart,timeEnd,timeNow));

        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void test_timeRange_invalid2(){
        WakeOnHomeWifiReciever reciever = new WakeOnHomeWifiReciever();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try{
            Date timeStart = dateFormat.parse("00:00");
            Date timeEnd = dateFormat.parse("10:00");
            Date timeNow = dateFormat.parse("9:30");
            Assert.assertEquals(true,reciever.isTimeInRange(timeStart,timeEnd,timeNow));

        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void test_timeRange_2_days_in_range(){
        WakeOnHomeWifiReciever reciever = new WakeOnHomeWifiReciever();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try{
            Date timeStart = dateFormat.parse("23:00");
            Date timeEnd = dateFormat.parse("10:00");
            Date timeNow = dateFormat.parse("0:30");
            Assert.assertEquals(true,reciever.isTimeInRange(timeStart,timeEnd,timeNow));

        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void test_timeRange_2_days_in_range_2(){
        WakeOnHomeWifiReciever reciever = new WakeOnHomeWifiReciever();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try{
            Date timeStart = dateFormat.parse("23:00");
            Date timeEnd = dateFormat.parse("10:00");
            Date timeNow = dateFormat.parse("23:30");
            Assert.assertEquals(true,reciever.isTimeInRange(timeStart,timeEnd,timeNow));

        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void test_timeRange_2_days_out_of_range(){
        WakeOnHomeWifiReciever reciever = new WakeOnHomeWifiReciever();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try{
            Date timeStart = dateFormat.parse("23:00");
            Date timeEnd = dateFormat.parse("10:00");
            Date timeNow = dateFormat.parse("11:30");
            Assert.assertEquals(false,reciever.isTimeInRange(timeStart,timeEnd,timeNow));

        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void test_timeRange_2_days_out_of_range_2(){
        WakeOnHomeWifiReciever reciever = new WakeOnHomeWifiReciever();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try{
            Date timeStart = dateFormat.parse("23:00");
            Date timeEnd = dateFormat.parse("10:00");
            Date timeNow = dateFormat.parse("21:30");
            Assert.assertEquals(false,reciever.isTimeInRange(timeStart,timeEnd,timeNow));

        }catch (ParseException e){
            e.printStackTrace();
        }
    }
}
