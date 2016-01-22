package com.teamgy.wakeonlan;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class sendWakeOnLan extends AsyncTask {

    private WifiManager wifiManager;

    public sendWakeOnLan(WifiManager wifiManager) {
        this.wifiManager = wifiManager;

    }

    @Override
    protected Object doInBackground(Object[] params) {


        try {

            DatagramSocket socket = new DatagramSocket(4000);
            socket.setBroadcast(true);
            String wolHeader = "ffffffffffff";
            //String mac = "fcaa142804ea";
            String mac = params[0].toString();
            String macWolData = new String(new char[16]).replace("\0", mac); //repeat mac 16 times
            byte[] data = hexStringToByteArray(wolHeader + macWolData); //6 byes


            DatagramPacket packet = new DatagramPacket(data, data.length, getBroadcastAddress(), 40000);
            socket.send(packet);
            socket.close();
            return "done";
        } catch (IOException e) {
            return null;

        }

    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
}
