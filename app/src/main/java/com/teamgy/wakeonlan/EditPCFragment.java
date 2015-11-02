package com.teamgy.wakeonlan;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Jakov on 01/11/2015.
 */
public class EditPCFragment extends Fragment {

    private EditText editMac;
    private EditText editSSID;
    private PCInfo pcinfo;
    private onPCInfoAddedListener listener;
    private boolean editMode;

    private AppCompatActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_new_pc, container, false);
        Bundle bundle = getArguments();
        String macAdress = bundle.getString("macAdress");
        String ssid = bundle.getString("ssid");
        pcinfo = new PCInfo(macAdress,ssid);

        editMac = (EditText)view.findViewById(R.id.edit_mac);
        editSSID = (EditText)view.findViewById(R.id.edit_ssid);

        if(macAdress == null || ssid == null){


            //we are creating a new pc then
            //layout is fine since we have hints there
            editMode = false;
        }
        else{
            editMac.setText(pcinfo.getMacAdress());
            editSSID.setText(pcinfo.getSSID());
            editMode = true;

        }

        return view;

    }

    public void addOnPCInfoAddedListener(onPCInfoAddedListener lst){
        this.listener = lst;


    }

    public static EditPCFragment newInstance(PCInfo pcinfo) {

        Bundle args = new Bundle();
        if(pcinfo != null){
            args.putString("macAdress",pcinfo.getMacAdress());
            args.putString("ssid",pcinfo.getSSID());
        }
        else{

            args.putString("macAdress",null);
            args.putString("ssid",null);

        }
        EditPCFragment fragment = new EditPCFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onDetach() {
        //toolbar.setNavigationIcon(null);
        PCInfo addedPCInfo = new PCInfo(editMac.getText().toString().toLowerCase(),editSSID.getText().toString());

        listener.onPcInfoAdded(addedPCInfo,editMode);

        super.onDetach();

    }


    @Override
    public void onAttach(Context context) {
        if(context instanceof  AppCompatActivity){
            activity = (AppCompatActivity) context;
        }
        super.onAttach(context);
    }
    public interface onPCInfoAddedListener{

        void onPcInfoAdded(PCInfo pcInfo,boolean editMode);
    }

}
