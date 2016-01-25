package com.teamgy.wakeonlan;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.teamgy.wakeonlan.utils.PCInfoDatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Jakov on 01/11/2015.
 */
public class MainFragment extends Fragment {


    private OnCreateViewListener listener;

    public ListView getListview() {
        return listview;
    }

    private ListView listview;
    private PCInfoDatabaseHelper dbHelper;
    private ArrayList<PCInfo> pcinfoArrList;
    private PcInfoAdapter adapter;
    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        Context context = view.getContext();
        dbHelper = PCInfoDatabaseHelper.getsInstance(context);


        listview = (ListView) view.findViewById(R.id.pc_list_view);


        if (savedInstanceState == null) {
            if (pcinfoArrList == null) {
                pcinfoArrList = new ArrayList<PCInfo>();
                pcinfoArrList = dbHelper.getAllPCInfos();
                adapter = new PcInfoAdapter(context, pcinfoArrList);
            }


        } else {
            pcinfoArrList = (ArrayList<PCInfo>) savedInstanceState.getSerializable("pcinfoArrList");
            adapter = new PcInfoAdapter(context, pcinfoArrList);

        }

        listview.setAdapter(adapter);
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
        PCInfo toEdit = pcinfoArrList.get(position);
        toEdit.setMacAdress(pcInfo.getMacAdress());
        toEdit.setPcName(pcInfo.getPcName());
        toEdit.setEnabled(pcInfo.isEnabled());
        adapter.notifyDataSetChanged();
        dbHelper.updatePCInfo(pcInfo, position);

    }
    public void editPcInfoEnabled(boolean newEnabled, final int position) {
        final PCInfo toEdit = pcinfoArrList.get(position);
        toEdit.setEnabled(newEnabled);
        adapter.notifyDataSetChanged();

        dbHelper.updatePCInfo(toEdit, position);
      /*  Thread t = new Thread(){
            public void run(){

                dbHelper.updatePCInfo(toEdit,position);
            }

        };*/

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
