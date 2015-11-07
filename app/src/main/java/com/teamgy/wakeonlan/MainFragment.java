package com.teamgy.wakeonlan;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.teamgy.wakeonlan.SQL.PCInfoDatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Jakov on 01/11/2015.
 */
public class MainFragment extends Fragment {
    

    private Context context;
    private OnCreateViewListener listener;

    public ListView getListview() {
        return listview;
    }

    private ListView listview;
    PCInfoDatabaseHelper dbHelper;


    ArrayList<PCInfo> pcinfoArrList;
    PcInfoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        context = view.getContext();
        dbHelper = PCInfoDatabaseHelper.getsInstance(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE);


        listview = (ListView)view.findViewById(R.id.pc_list_view);



        if(savedInstanceState == null){
            if(pcinfoArrList == null){
                pcinfoArrList = new ArrayList<PCInfo>();
                pcinfoArrList = dbHelper.getAllPCInfos();
                adapter = new PcInfoAdapter(context,R.layout.pc_list_item,pcinfoArrList);
            }



        }else{
            pcinfoArrList = (ArrayList<PCInfo>)savedInstanceState.getSerializable("pcinfoArrList");
            adapter = new PcInfoAdapter(context,R.layout.pc_list_item,pcinfoArrList);

        }

        listview.setAdapter(adapter);

        if(listener != null){listener.onViewCreated();}
        return view;


    }
    private void saveAdress(){


    }

    @Override
    public void onPause() {


        saveAdress();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(pcinfoArrList != null){
            outState.putSerializable("pcinfoArrList",pcinfoArrList);

        }
    }

    @Override
    public void onStop() {
        saveAdress();
        super.onStop();

    }

    private  void loadPCInfos() {

        if (pcinfoArrList == null) {

        }
        else{
            //TODO load from database

        }


    }
    public void addNewPCInfo(PCInfo pcInfo){

        pcinfoArrList.add(pcInfo);
        adapter.notifyDataSetChanged();

    }
    public void editPCInfo(PCInfo pcInfo,int position){
       //pcinfoArrList.remove(position);
        //pcinfoArrList.add(position, pcInfo);
        PCInfo toEdit = pcinfoArrList.get(position);
        toEdit.setMacAdress(pcInfo.getMacAdress());
        toEdit.setSSID(pcInfo.getSSID());
        toEdit.setEnabled(pcInfo.isEnabled());
        adapter.notifyDataSetChanged();
        dbHelper.updatePCInfo(pcInfo, position);

    }
    public void deletePcInfo(int position){

        pcinfoArrList.remove(position);
        adapter.notifyDataSetChanged();
        dbHelper.deletePCInfo(position); //database counts from 1

    }
    public PCInfo getPCInfo(int position){
        return pcinfoArrList.get(position);
    }
    public ArrayList<PCInfo> getPcinfoArrList() {
        return pcinfoArrList;
    }
    public void setOnCreateViewListener(OnCreateViewListener listener){
        this.listener = listener;
    }


}
