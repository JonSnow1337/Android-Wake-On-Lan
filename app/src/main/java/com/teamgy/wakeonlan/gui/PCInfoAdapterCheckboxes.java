package com.teamgy.wakeonlan.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.teamgy.wakeonlan.R;
import com.teamgy.wakeonlan.data.PCInfo;

import java.io.Serializable;
import java.util.ArrayList;


public class PCInfoAdapterCheckboxes extends PcInfoAdapter implements Serializable {
    private static final long serialVersionUID = 1L;
    public PCInfoAdapterCheckboxes(Context context, ArrayList<PCInfo> infos) {
        super(context, infos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PCInfo info = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pc_list_item_checkboxes, parent, false);
        }
        TextView tvMac = (TextView) convertView.findViewById(R.id.list_item_mac);
        TextView tvSSID = (TextView) convertView.findViewById(R.id.list_item_ssid);
        tvMac.setText(info.getMacAdress());
        tvSSID.setText(info.getPcName());
        return convertView;
    }
}
