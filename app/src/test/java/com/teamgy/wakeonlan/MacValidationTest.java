package com.teamgy.wakeonlan;

import com.teamgy.wakeonlan.utils.Tools;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by Jedi-Windows on 21.01.2016..
 */
public class MacValidationTest {

    @Test
    public void test_macLenght_valid(){
        Assert.assertEquals(true, Tools.isMacValid("00-14-22-01-23-45"));
        Assert.assertEquals(true, Tools.isMacValid("00-AB-22-01-23-45"));


    }
    @Test
    public void test_macLenght_invalid(){
        Assert.assertEquals(false, Tools.isMacValid("00-14-22-01-23-1"));
        Assert.assertEquals(false, Tools.isMacValid("00-14-22-01-23-123"));


    }
    @Test
    public void test_macLenght_invalid_weird_format(){
        Assert.assertEquals(false, Tools.isMacValid("00::14---22:::01-23-1::--::--..+++"));

    }
    @Test
    public void test_macLenght_invalid_none(){
        Assert.assertEquals(false, Tools.isMacValid(""));

    }
    @Test
    public void test_macValid_badCharacters(){
        Assert.assertEquals(false, Tools.isMacValid("fcaa142804eaghjl"));
        Assert.assertEquals(false, Tools.isMacValid("fcaa142804ea-gh-jl"));


    }
    @Test
    public void test_macValid_OK(){
        Assert.assertEquals(true, Tools.isMacValid("fcaa142804ea"));
        Assert.assertEquals(true, Tools.isMacValid("fc:aa:14:28:04:ea"));


    }
    @Test
    public void test_macValid_rota(){
        Assert.assertEquals(false, Tools.isMacValid("qwdffffhhggg"));
    }






}
