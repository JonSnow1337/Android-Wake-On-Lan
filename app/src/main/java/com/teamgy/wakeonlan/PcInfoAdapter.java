package com.teamgy.wakeonlan;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jakov on 01/11/2015.
 */
public class PcInfoAdapter extends ArrayAdapter<PCInfo> {
    private Context context;
    private PCInfoAdapterCallback callback;

    public PcInfoAdapter(Context context, ArrayList<PCInfo> infos) {

        super(context, 0, infos);
        this.context = context;
    }

    public void setCallback(PCInfoAdapterCallback callback){
        this.callback = callback;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final PCInfo info = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pc_list_item, parent, false);
        }
        TextView tvMac = (TextView) convertView.findViewById(R.id.list_item_mac);
        TextView tvSSID = (TextView) convertView.findViewById(R.id.list_item_ssid);
        final CheckBox chk = (CheckBox) convertView.findViewById(R.id.pc_item_checkbox);
        final ImageButton preLolipopConfigureButton = (ImageButton) convertView.findViewById(R.id.pre_lolipop_editPC);
        tvMac.setText(info.getMacAdress());
        tvSSID.setText(info.getPcName());
        chk.setChecked(info.isEnabled());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            //callback here because i cant access activity methods
            //for lolipop we dont even respond to buttons,
            //its all long click and single click  on list item direcrtly

            if(callback != null){

                chk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.checkboxPressed(chk,info,position);
                    }
                });

                preLolipopConfigureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.configurePressed(info,position);
                    }
                });

            }
        }
        return convertView;

    }

    public interface PCInfoAdapterCallback{
        void checkboxPressed(CheckBox chk, PCInfo pcinfo,int position);
        void configurePressed(PCInfo pcinfo,int position);
    }

}
