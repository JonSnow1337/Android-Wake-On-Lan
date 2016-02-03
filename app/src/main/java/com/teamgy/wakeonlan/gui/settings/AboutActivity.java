package com.teamgy.wakeonlan.gui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.teamgy.wakeonlan.R;
import com.teamgy.wakeonlan.utils.Tools;

import java.util.ArrayList;

/**
 * Created by Jedi-Windows on 30.01.2016..
 */
public class AboutActivity  extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        intialiseButtonLinks();

    }

    private void intialiseButtonLinks() {

        ImageButton linkAppIntro = (ImageButton) findViewById(R.id.link_appintro);
        ImageButton linkDBManager = (ImageButton) findViewById(R.id.link_dbmanager);
        ArrayList <ImageButton> buttonList = new ArrayList<>();
        buttonList.add(linkAppIntro);
        buttonList.add(linkDBManager);

        for (final ImageButton b : buttonList) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //tag is used because i have no idea where to put data in imagebutton xml..
                    //and i dont want to do this without for loop..
                    Tools.openURL(b.getTag().toString(),getBaseContext());
                }
            });
        }
    }
}
