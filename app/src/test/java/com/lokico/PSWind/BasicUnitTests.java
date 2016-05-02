package com.lokico.PSWind;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic local unit tests for the app.
 * Use these to determine what further detailed tests can proceed.
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class BasicUnitTests {
    @Test
    public void isNoaaImplemented() {
        assertTrue(NOAA.isActivityImplemented());
    }

    @Test
    public void isOmniMapImplemented() {
        assertTrue(OmniMap.isActivityImplemented());
    }

    @Test
    public void isTJsSeattleImplemented() {
        assertTrue(TJsSeattle.isActivityImplemented());
    }

    @Test
    public void isTJsNorthSoundImplemented() {
        assertTrue(TJsNorthSound.isActivityImplemented());
    }
}