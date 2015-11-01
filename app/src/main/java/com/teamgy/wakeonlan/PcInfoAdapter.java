package com.teamgy.wakeonlan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Jakov on 01/11/2015.
 */
public class PcInfoAdapter extends ArrayAdapter<PCInfo> {

    public PcInfoAdapter(Context context, int resource, ArrayList<PCInfo> infos) {
        super(context, 0, infos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PCInfo info = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pc_list_item,parent,false);
        }
        TextView tvMac = (TextView) convertView.findViewById(R.id.list_item_mac);
        TextView tvSSID = (TextView) convertView.findViewById(R.id.list_item_ssid);
        tvMac.setText(info.getMacAdress());
        tvSSID.setText(info.getSSID());

        return convertView;

    }
}
