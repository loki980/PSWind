package com.lokico.PSWind;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class BasicUnitTests {
    @Test
    public void isNoaaImplemented() {
        assertEquals(true, NOAA.isActivityImplemented());
    }

    @Test
    public void isOmniMapImplemented() {
        assertEquals(true, OmniMap.isActivityImplemented());
    }

    @Test
    public void isTJsSeattleImplemented() {
        assertEquals(true, TJsSeattle.isActivityImplemented());
    }

    @Test
    public void isTJsNorthSoundImplemented() {
        assertEquals(true, TJsNorthSound.isActivityImplemented());
    }
}