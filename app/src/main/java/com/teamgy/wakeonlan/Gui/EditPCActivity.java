package com.teamgy.wakeonlan.gui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.teamgy.wakeonlan.data.PCInfo;
import com.teamgy.wakeonlan.R;
import com.teamgy.wakeonlan.utils.Tools;

/**
 * Created by Jakov on 01/11/2015.
 */
public class EditPCActivity extends AppCompatActivity  {

    private EditText editMac;
    private EditText editSSID;
    private boolean editMode;
    private int positon;
    private PCInfo pcInfoEditing;

    private AppCompatActivity activity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.add_new_pc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        int mode = bundle.getInt("mode");

        this.pcInfoEditing = (PCInfo) bundle.getSerializable("pcInfo");

        editMac = (EditText) findViewById(R.id.edit_mac);
        editSSID = (EditText) findViewById(R.id.edit_ssid);

        if (mode == MainActivity.REQUEST_ADD) {

            getSupportActionBar().setTitle("Add New PC");
            this.pcInfoEditing = new PCInfo("","");
            //we are creating a new pc then
            //layout is fine since we have hints there
            editMode = false;
        } else {
            //its edit
            getSupportActionBar().setTitle("Edit PC");
            editMac.setText(pcInfoEditing.getMacAdress());
            editSSID.setText(pcInfoEditing.getPcName());
            positon = bundle.getInt("position");
            editMode = true;
            toolbar.setNavigationIcon(R.drawable.ic_delete_white_24dp);
        }

        editMac.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean isEnterPressed = (event.getAction() == KeyEvent.ACTION_DOWN)
                                       && (keyCode == KeyEvent.KEYCODE_ENTER);
                if (isEnterPressed) {
                    Log.d("enter pressed", "hello????");
                    finishIfValidMac(editMac.getText().toString());
                    return true;
                }
                return false;
            }
        });


        initializeCircularAnimation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setReturnTransition(null);
        }

    }


    private void initializeCircularAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.changebounds);

            getWindow().setSharedElementEnterTransition(transition);

            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    View v = findViewById(R.id.edit_pc_backgroud);
                    v.setVisibility(View.GONE);
                    //setting this drawable with 2 colors to transition between them after circualr reveal
                    v.setBackground(getDrawable(R.drawable.transition_drawable));
                    v = findViewById(R.id.app_bar_layout);
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.toolbar);
                    v.setVisibility(View.GONE);


                }

                @Override
                public void onTransitionEnd(Transition transition) {

                    View v = findViewById(R.id.edit_pc_backgroud);
                    v.setVisibility(View.VISIBLE);
                    Tools.backgroudTransition(v);
                    v = findViewById(R.id.toolbar);
                    Tools.circularRevealShow(v);
                    v = findViewById(R.id.app_bar_layout);
                    Tools.circularRevealShow(v);

                    //fading the color fast to white
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("edit", "called it");
        if (item.getItemId() == R.id.menu_pc_check) {
            String mac = editMac.getText().toString();

            return finishIfValidMac(mac);
        }
        if (item.getItemId() == android.R.id.home) {

            if (editMode) {
                //in edit mode back button is a trash icon...
                Intent data = new Intent();
                data.putExtra("position", positon);
                setResult(MainActivity.RESULT_DELETE, data);
                finish();
                return true;
            } else {
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean finishIfValidMac(String mac) {
        /**Checks if input mac is valid and Sets error to editMac if not
         * If its valid it exits activiry and applies result.
         */
        if (!Tools.isMacValid(mac)) {
            editMac.setError("Invalid MAC");
            editMac.requestFocus();
        } else {
            applyResult();
            finish();
            return true;
        }
        return false;
    }

    private void applyResult() {
        Intent data = new Intent();
        String newMac = editMac.getText().toString();
        String newName = editSSID.getText().toString();
        this.pcInfoEditing.setMacAdress(newMac);
        this.pcInfoEditing.setPcName(newName);
        data.putExtra("pcInfo", this.pcInfoEditing);
        data.putExtra("position", positon); //TODO PLEASE CHANGE THIS ITS DUMB
        setResult(RESULT_OK, data);

    }

    @Override
    public void onBackPressed() {
        finish();

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_pc_activity, menu);
        return true;
    }


}
