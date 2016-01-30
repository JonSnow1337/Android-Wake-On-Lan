package com.teamgy.wakeonlan.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.teamgy.wakeonlan.R;
import com.teamgy.wakeonlan.utils.Tools;

/**
 * Created by Jedi-Windows on 21.01.2016..
 */
public class MACFormatInvalidDialogFragment extends DialogFragment {


    public interface MACFormatInvalidListener {
        void onDialogEditClick();

        void onDialogDiscardClick();
    }

    private MACFormatInvalidListener listener;

    public static MACFormatInvalidDialogFragment newIntance(String badMac) {
        MACFormatInvalidDialogFragment fragment = new MACFormatInvalidDialogFragment();
        Bundle b = new Bundle();
        b.putString("badMac", badMac);
        fragment.setArguments(b);
        return fragment;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MACFormatInvalidListener) {
            this.listener = (MACFormatInvalidListener) activity;
        } else {
            throw new ClassCastException("Class must implement MACFormatInvalidListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String badMac = getArguments().getString("badMac");
        builder.setTitle("Edit invalid entry?");
        builder.setMessage(buildAlertMessage(badMac));
        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogEditClick();

            }
        });
        builder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogDiscardClick();
            }
        });

        return builder.create();
    }

    public String buildAlertMessage(String badMac) {
        String reformatedMAC = Tools.reformatMACInput(badMac, false);
        String alertMessage = "\'" + badMac + "\'" + " is not a valid MAC address! \n";
        if (reformatedMAC.length() != 12) {
            alertMessage += "Address should have 12 characters, not " + reformatedMAC.length() + ". \n";
        }
        String badCharactes = Tools.findBadMACCharacters(badMac);

        if (badCharactes.length() > 0) {
            if (badCharactes.length() == 1) {
                alertMessage += "\'" + badCharactes + "\'" + " is not a hexadecimal.";

            } else {
                alertMessage += "\'" + badCharactes + "\'" + " are not hexadecimal.";
            }
        }


        return alertMessage;

    }
}
