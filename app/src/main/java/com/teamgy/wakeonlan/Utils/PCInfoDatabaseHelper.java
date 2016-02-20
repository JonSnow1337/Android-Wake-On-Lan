package com.teamgy.wakeonlan.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toolbar;

import com.teamgy.wakeonlan.data.PCInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakov on 07/11/2015.
 */
//Note, no real need for database in this application, i only did it for practice purposes
public class PCInfoDatabaseHelper extends SQLiteOpenHelper {
    //database info
    private static final String DATABASE_NAME = "pcInfoDatabase";
    private static final int DATABASE_VERSION = 3;
    //Table name
    private static final String TABLE_PC_INFO="pcInfo";
    //table columns
    private static final String KEY_ID = "id";
    private static final String KEY_MAC = "mac";
    private static final String KEY_SSID = "ssid";
    private static final String KEY_ON_WIFI_ENABLED = "onWifiEnabled";
    private static final String KEY_ON_ALARM_ENABLED = "onAlarmEnabled";
    private static final String KEY_ALARM_DAYS = "alarmDays"; //1|0|1|1... boolean array


    //SINGLETON DESIGN PATTERN(one instance allowed)
    private static PCInfoDatabaseHelper sInstance;

    public static  synchronized PCInfoDatabaseHelper getsInstance(Context context){
        if(sInstance == null){

            sInstance = new PCInfoDatabaseHelper(context.getApplicationContext());
        }

        return sInstance;

    }
    private PCInfoDatabaseHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //called when db is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PC_INFO_TABLE = "CREATE TABLE " + TABLE_PC_INFO +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY,"+
                KEY_MAC + " TEXT," +
                KEY_SSID + " TEXT," +
                KEY_ON_WIFI_ENABLED + " INTEGER," +
                KEY_ON_ALARM_ENABLED + " INTEGER," +
                KEY_ALARM_DAYS + " TEXT" +
                ")";
        Log.d("db helper", CREATE_PC_INFO_TABLE);
        db.execSQL(CREATE_PC_INFO_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXITS " + TABLE_PC_INFO);
            onCreate(db);
        }
    }
    public void updatePCInfo(PCInfo pcInfo, int position){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PC_INFO, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MAC,pcInfo.getMacAdress());
        contentValues.put(KEY_SSID,pcInfo.getPcName());
        contentValues.put(KEY_ON_WIFI_ENABLED, Tools.booleanToInt(pcInfo.isOnWifiEnabled()));
        contentValues.put(KEY_ON_ALARM_ENABLED, Tools.booleanToInt(pcInfo.isOnWifiEnabled()));
        contentValues.put(KEY_ALARM_DAYS, Tools.booleanArrayToString(pcInfo.getAlarmDays()));

        try{
            //find row id by using position in table and update it
            cursor.moveToPosition(position);
            String rowId = cursor.getString(cursor.getColumnIndex(KEY_ID));
            db.update(TABLE_PC_INFO, contentValues,"rowid = " + rowId,null);

        }catch (Exception e){
            Log.d("DbHelper", "Error while updating pcInfo at position: " + position);

        }
        finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
    }
    public void addPCInfo(PCInfo pcInfo){


        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MAC, pcInfo.getMacAdress());
        contentValues.put(KEY_SSID, pcInfo.getPcName());
        contentValues.put(KEY_ON_WIFI_ENABLED, pcInfo.isOnWifiEnabled());
        contentValues.put(KEY_ON_ALARM_ENABLED,pcInfo.isOnAlarmEnabled());
        contentValues.put(KEY_ALARM_DAYS, Tools.booleanArrayToString(pcInfo.getAlarmDays()));
        db.insertOrThrow(TABLE_PC_INFO, null, contentValues);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public ArrayList<PCInfo> getAllPCInfos(){
        ArrayList<PCInfo> pcInfos = new ArrayList<PCInfo>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PC_INFO, null);
        try{
            if(cursor.moveToFirst()){
                do{
                    String mac = cursor.getString(cursor.getColumnIndex(KEY_MAC));
                    String ssid = cursor.getString(cursor.getColumnIndex(KEY_SSID));
                    int onWifiEnabled = cursor.getInt(cursor.getColumnIndex(KEY_ON_WIFI_ENABLED));
                    int onAlarmEnabled = cursor.getInt(cursor.getColumnIndex(KEY_ON_ALARM_ENABLED));
                    String alarmDays = cursor.getString(cursor.getColumnIndex(KEY_ALARM_DAYS));

                    PCInfo pcInfo = new PCInfo(
                            mac,
                            ssid,
                            Tools.intToBoolean(onWifiEnabled),
                            Tools.intToBoolean(onAlarmEnabled),
                            Tools.stringToBolleanArray(alarmDays)
                    );

                    pcInfos.add(pcInfo);
                }while (cursor.moveToNext());

            }

        }catch (Exception e ){
            Log.d("DbHelper", "Error while fetching pcinfos");
        }
        finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return pcInfos;

    }
    public void deletePCInfo(int position){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PC_INFO, null);
        try{
            cursor.moveToPosition(position);
            String rowId = cursor.getString(cursor.getColumnIndex(KEY_ID));
            db.delete(TABLE_PC_INFO, "rowid = " +  rowId ,null);

        }catch (Exception e){
            Log.d("DbHelper", "Error while deleting pcInfo at position: " + position);

        }
        finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }

        }

    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
