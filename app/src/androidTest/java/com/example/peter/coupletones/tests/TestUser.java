package com.example.peter.coupletones.tests;


import android.test.ActivityInstrumentationTestCase2;

import com.example.peter.coupletones.User;


/**
 * This class tests `User.java`
 * Created by Runjie on 050616.
 */
public class TestUser extends ActivityInstrumentationTestCase2 {
    private final static String MY_EMAIL = "hey@ijustmetyou.com";
    private final static String MY_NAME = "CARLY RAE JEPSEN";
    private final static String MY_ID = "Here1sMy#";
    private final static String PARTNER_ID="S0Ca11MeMay8e";

    User user;

    public TestUser() {
        super(User.class);
        user = new User(MY_NAME,MY_EMAIL);
    }
    public void testGetMyEmail() {
        assertEquals(user.getMyEmail(), MY_EMAIL);
    }

    public void testGetMyName() {
        assertEquals(user.getMyName(), MY_NAME);
    }

    public void testGetPartnerEmail() {
        user.addPartnerEmail(PARTNER_ID);
        assertEquals(user.getPartnerEmail(), PARTNER_ID);
    }

    public void testGetUserEmail() {
        user.addMyEmail(MY_ID);
        assertEquals(user.getMyEmail(), MY_ID);
    }

}
