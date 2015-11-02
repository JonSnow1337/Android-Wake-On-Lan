package com.teamgy.wakeonlan;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Jakov on 01/11/2015.
 */
public class MainFragment extends Fragment {
    private EditText editText;
    private String macAdress;
    private Context context;
    private OnCreateViewListener listener;

    public ListView getListview() {
        return listview;
    }

    private ListView listview;



    ArrayList<PCInfo> pcinfoArrList;
    PcInfoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_main, container, false);
        context = view.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE);


        listview = (ListView)view.findViewById(R.id.pc_list_view);





        loadPCInfos();
        adapter = new PcInfoAdapter(context,R.layout.pc_list_item,pcinfoArrList);
        listview.setAdapter(adapter);


        if(listener != null){listener.onViewCreated();}
        return view;


    }
    private void saveAdress(){
        if(macAdress != null){

            SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("macAdress",macAdress);
            editor.commit();

        }

    }

    @Override
    public void onPause() {
        saveAdress();
        super.onPause();
    }

    @Override
    public void onStop() {
        saveAdress();
        super.onStop();

    }

    private  void loadPCInfos() {

        if (pcinfoArrList == null) {

            pcinfoArrList = new ArrayList<PCInfo>();
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
        pcinfoArrList.remove(position);
        pcinfoArrList.add(position,pcInfo);
        adapter.notifyDataSetChanged();

    }
    public ArrayList<PCInfo> getPcinfoArrList() {
        return pcinfoArrList;
    }
    public void setOnCreateViewListener(OnCreateViewListener listener){
        this.listener = listener;
    }


}
