package com.example.peter.coupletones.tests;
import android.test.ActivityInstrumentationTestCase2;

import com.example.peter.coupletones.VibeTone;

/**
 * Created by Peter on 5/26/2016.
 */
public class TestVibeTone extends ActivityInstrumentationTestCase2 {

    VibeTone vibeTone;
    long[] vibePattern = {0, 1000, 100};
    String name = "Default";

    public TestVibeTone() {
        super(VibeTone.class);
        vibeTone = new VibeTone(name, vibePattern);
    }

    public void testGetPattern(){
        assertEquals(vibeTone.getPattern(), vibePattern);
    }

    public void testGetName(){
        System.out.println(vibeTone.getName());
        System.out.println(name);
        assertTrue(vibeTone.getName().equals(name));
    }

    public void testSetPattern(){
        long[] temp = {0, 2000, 200};
        vibeTone.setPattern(temp);
        assertEquals(vibeTone.getPattern(), temp);
    }
}
