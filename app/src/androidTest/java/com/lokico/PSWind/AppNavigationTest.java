package com.lokico.PSWind;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.hasContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.junit.Assert.fail;

//import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.widget.DrawerLayout;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.Gravity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static com.lokico.PSWind.NavigationViewActions.navigateTo;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppNavigationTest {

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     * <p/>
     * <p/>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickOmniMapButton() throws Exception {
        // Click on the OmniMap button
        onView(withId(R.id.button)).perform(click());

        // Check if the OmniMap screen is displayed
        onView(withId(R.id.display_omni_map)).check(matches(isDisplayed()));
    }

    @Test
    public void clickTjsSeattleButton() throws Exception {
        // Click on the TJsSeattle button
        onView(withId(R.id.button2)).perform(click());

        // Check if the TJs Seattle screen is displayed
        onView(withId(R.id.display_tjs_seattle)).check(matches(isDisplayed()));
    }

    @Test
    public void clickTjsNorthButton() throws Exception {
        // Click on the TJsSeattle button
        onView(withId(R.id.button3)).perform(click());

        // Check if the TJs North screen is displayed
        onView(withId(R.id.display_tjs_north)).check(matches(isDisplayed()));
    }

    @Test
    public void clickNoaaButton() throws Exception {
        // Click on the TJsSeattle button
        onView(withId(R.id.button4)).perform(click());

        // Check if the NOAA screen is displayed
        onView(withId(R.id.display_noaa)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOmniMapNavDrawerItem() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start OmniMap screen.
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_omni_map));

        // Check if the OmniMap screen is displayed
        onView(withId(R.id.display_omni_map)).check(matches(isDisplayed()));
    }

    @Test
    public void clickTjsSeattleNavDrawerItem() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start TJs Seattle screen.
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_tjs_seattle));

        // Check if the TJs Seattle screen is displayed
        onView(withId(R.id.display_tjs_seattle)).check(matches(isDisplayed()));
    }

    @Test
    public void clickTjsNorthNavDrawerItem() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start TJs North screen.
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_tjs_north_sound));

        // Check if the TJs North screen is displayed
        onView(withId(R.id.display_tjs_north)).check(matches(isDisplayed()));
    }

    @Test
    public void clickNoaaNavDrawerItem() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start NOAA screen.
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_noaa));

        // Check if the NOAA screen is displayed
        onView(withId(R.id.display_noaa)).check(matches(isDisplayed()));
    }
}
