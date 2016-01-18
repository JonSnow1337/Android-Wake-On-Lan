package com.teamgy.wakeonlan;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.teamgy.wakeonlan.utils.Tools;

/**
 * Created by Jakov on 01/11/2015.
 */
public class EditPCActivity extends AppCompatActivity {

    private EditText editMac;
    private EditText editSSID;
    private PCInfo pcinfo;
    private onPCInfoAddedListener listener;
    private boolean editMode;
    private int mode;
    private int positon;

    private AppCompatActivity activity;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        setContentView(R.layout.add_new_pc);
        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_check_white_24dp);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mode = bundle.getInt("mode");
        String macAdress = bundle.getString("macAdress");
        String ssid = bundle.getString("ssid");
        pcinfo = new PCInfo(macAdress,ssid);

        editMac = (EditText)findViewById(R.id.edit_mac);
        editSSID = (EditText)findViewById(R.id.edit_ssid);
        //Log.d("d", "meme");

        if(mode == MainActivity.REQUEST_ADD){

            getSupportActionBar().setTitle("Add New PC");
            //we are creating a new pc then
            //layout is fine since we have hints there
            editMode = false;
        }
        else{
            //its edit
            getSupportActionBar().setTitle("Edit PC");

            editMac.setText(pcinfo.getMacAdress());
            editSSID.setText(pcinfo.getPcName());
            positon = bundle.getInt("position");
            editMode = true;

        }

        editMac.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

               reformatMac();


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

                    v  = findViewById(R.id.app_bar_layout);
                    v.setVisibility(View.GONE);
                    v  = findViewById(R.id.toolbar);
                    v.setVisibility(View.GONE);



                }

                @Override
                public void onTransitionEnd(Transition transition) {

                    View v = findViewById(R.id.edit_pc_backgroud);
                    v.setVisibility(View.VISIBLE);
                    Tools.backgroudTransition(v);
                    v = findViewById(R.id.app_bar_layout);
                    Tools.circularRevealShow(v);
                    v = findViewById(R.id.toolbar);
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

    public void addOnPCInfoAddedListener(onPCInfoAddedListener lst){
        this.listener = lst;
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("edit", "called it");
        if(item.getItemId() == android.R.id.home){
            reformatMac();
            applyResult();
            finish();//very sketch, why do you do this android, just call the method in main :((
            return true;
        }
        if(item.getItemId() == R.id.menu_pc_trash){
            if(!editMode){
                onBackPressed();
            }else{
                Intent data = new Intent();
                data.putExtra("position", positon);
                setResult(MainActivity.RESULT_DELETE, data);
                finish();
            }


        }
        return super.onOptionsItemSelected(item);
    }

    private void applyResult(){
        reformatMac();
        Intent data = new Intent();
        Log.d("",editMac.getText().toString());
        data.putExtra("macAdress",editMac.getText().toString());
        data.putExtra("ssid",editSSID.getText().toString());
        data.putExtra("position", positon); //TODO PLEASE CHANGE THIS ITS DUMB
        setResult(RESULT_OK, data);

    }

    public interface onPCInfoAddedListener{

        void onPcInfoAdded(PCInfo pcInfo,boolean editMode);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_pc_activity, menu);
        return true;
    }
    private void reformatMac(){

        String inputText = editMac.getText().toString();
        String formatedText = Tools.reformatMACInput(inputText);
        if(!inputText.equals(formatedText)){
            Log.d("debug", "input: " + inputText + " format: " + formatedText);
            editMac.setText(Tools.reformatMACInput(editMac.getText().toString()));
            Snackbar.make(findViewById(R.id.edit_pc_view),"Reformatted MAC to application format",Snackbar.LENGTH_SHORT).show();

        }

    }


}
