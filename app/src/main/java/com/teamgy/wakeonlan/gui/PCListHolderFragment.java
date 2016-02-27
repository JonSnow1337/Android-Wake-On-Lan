package com.teamgy.wakeonlan.gui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.teamgy.wakeonlan.data.PCInfo;
import com.teamgy.wakeonlan.R;
import com.teamgy.wakeonlan.utils.PCInfoDatabaseHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakov on 01/11/2015.
 */
public class PCListHolderFragment extends Fragment {


    private OnCreateViewListener listener;

    public ListView getListview() {
        return listview;
    }

    private ListView listview;
    private PCInfoDatabaseHelper dbHelper;
    private ArrayList<PCInfo> pcinfoArrList;
    private MainActivity activity;
    private BaseAdapter adapter;
    private int layout = R.layout.content_main; //default layout with fab


    public static  PCListHolderFragment newInstance(BaseAdapter adapter){
        PCListHolderFragment fragmentDemo = new PCListHolderFragment();
        Bundle args = new Bundle();
        args.putSerializable("adapter", (Serializable)adapter);
        fragmentDemo.setArguments(args);
        return fragmentDemo;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(layout, container, false);
        Context context = view.getContext();
        dbHelper = PCInfoDatabaseHelper.getsInstance(context);


        listview = (ListView) view.findViewById(R.id.pc_list_view);



        if (savedInstanceState == null) {
            if (pcinfoArrList == null) {
                pcinfoArrList = new ArrayList<PCInfo>();
                pcinfoArrList = dbHelper.getAllPCInfos();
            }

        } else {
            pcinfoArrList = (ArrayList<PCInfo>) savedInstanceState.getSerializable("pcinfoArrList");

        }
        //reload adapter that might be set from newInstance(adapter)
        Bundle args = getArguments();
        if(args != null){
            BaseAdapter adapterLoaded = (BaseAdapter) args.getSerializable("adapter");
            if(adapterLoaded != null){
                this.adapter = (BaseAdapter) adapterLoaded;
            }
        }
        else{
            this.adapter = new PcInfoAdapter(context,pcinfoArrList);
        }
        //adapter laoded or defaulted, set it
        listview.setAdapter(this.adapter);

        if (listener != null) {
            listener.onViewCreated();
        }
        return view;


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (pcinfoArrList != null) {
            outState.putSerializable("pcinfoArrList", pcinfoArrList);

        }
    }

    public void addNewPCInfo(PCInfo pcInfo) {

        pcinfoArrList.add(pcInfo);
        adapter.notifyDataSetChanged();

    }

    public void editPCInfo(PCInfo pcInfo, int position) {
        pcinfoArrList.set(position,pcInfo);
        adapter.notifyDataSetChanged();
        updateDatabase(pcInfo,position);


    }
    public void editPcInfoEnabled(boolean newEnabled, final int position) {
        final PCInfo toEdit = pcinfoArrList.get(position);
        toEdit.setOnWifiEnabled(newEnabled);
        adapter.notifyDataSetChanged();
       // dbHelper.updatePCInfo(toEdit, position);
        updateDatabase(toEdit,position);

    }

    public void updateDatabase(final PCInfo toEdit,final int position){
        Thread t = new Thread(){
            public void run(){

                dbHelper.updatePCInfo(toEdit,position);
            }

        };
        t.start();


    }

    public void deletePcInfo(int position) {

        pcinfoArrList.remove(position);
        adapter.notifyDataSetChanged();
        dbHelper.deletePCInfo(position);

    }

    public PCInfo getPCInfo(int position) {
        return pcinfoArrList.get(position);
    }

    public ArrayList<PCInfo> getPcinfoArrList() {
        return pcinfoArrList;
    }

    public void setOnCreateViewListener(OnCreateViewListener listener) {
        this.listener = listener;
    }

}
