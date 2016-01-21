package com.teamgy.wakeonlan;

import com.teamgy.wakeonlan.utils.Tools;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by Jedi-Windows on 22.01.2016..
 */
public class MacBadCharactersFinderTest {
    @Test
    public void test_badChacters(){

        Assert.assertEquals("gh", Tools.findBadMACCharacters("00 0a 95 9d 68 gh"));
        Assert.assertEquals("gh", Tools.findBadMACCharacters("00 0a 95 9d 68 GH"));



    }
    @Test
    public void test_goodCharacters(){

        Assert.assertEquals("", Tools.findBadMACCharacters("12 34 ef 90 ab cd"));

    }
}
