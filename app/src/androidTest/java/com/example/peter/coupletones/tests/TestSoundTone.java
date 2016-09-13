package com.example.peter.coupletones.tests;

import android.media.RingtoneManager;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

import com.example.peter.coupletones.SoundTone;
import com.example.peter.coupletones.User;

/**
 * Created by Peter on 5/26/2016.
 */
public class TestSoundTone extends ActivityInstrumentationTestCase2 {
    SoundTone soundTone;
    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager
            .TYPE_NOTIFICATION);
    String name = "Default";

    public TestSoundTone() {
        super(SoundTone.class);
        soundTone = new SoundTone(uri, name);
    }

    public void testGetUri(){
        assertEquals(soundTone.getUri(), uri);
    }

    public void testGetName(){
        assertEquals(soundTone.getRingtoneName(), "Default");
    }

    public void testSetName(){
        soundTone.setRingtoneName("new");
        assertEquals(soundTone.getRingtoneName(), "new");
    }

}
