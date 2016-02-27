package com.teamgy.wakeonlan.gui;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.teamgy.wakeonlan.data.PCInfo;
import com.teamgy.wakeonlan.R;

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
        tvMac.setText(info.getMacAdress());
        tvSSID.setText(info.getPcName());

        //TODO change names no longer prelolipop
        final ImageButton preLolipopConfigureButton = (ImageButton) convertView.findViewById(R.id.pre_lolipop_editPC);

        if(callback != null){
            preLolipopConfigureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.configurePressed(info,position);
                }
            });

        }

        return convertView;

    }

    public interface PCInfoAdapterCallback{
        void configurePressed(PCInfo pcinfo,int position);
    }


}
