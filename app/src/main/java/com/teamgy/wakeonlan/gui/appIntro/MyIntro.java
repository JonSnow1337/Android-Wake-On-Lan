package com.teamgy.wakeonlan.gui.appIntro;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro;
import com.teamgy.wakeonlan.R;

/**
 * Created by Jedi-Windows on 23.01.2016..
 */
public class MyIntro extends AppIntro {
    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        addSlide(IntroFragment.newInstance(R.layout.intro_slide_1));
        addSlide(IntroFragment.newInstance(R.layout.intro_slide_2));
        addSlide(IntroFragment.newInstance(R.layout.intro_slide_3));
        addSlide(IntroFragment.newInstance(R.layout.intro_slide_4));



    }

    @Override
    public void onSkipPressed() {
        finish();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        finish();

    }

    @Override
    public void onSlideChanged() {

    }
}
