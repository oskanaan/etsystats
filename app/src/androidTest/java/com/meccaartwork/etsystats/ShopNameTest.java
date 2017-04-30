package com.meccaartwork.etsystats;

import android.app.Application;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShopNameTest extends ApplicationTestCase<Application> {
    public ShopNameTest() {
        super(Application.class);
    }

  private String mStringToBetyped;

  @Rule
  public ActivityTestRule<Main> mActivityRule = new ActivityTestRule<>(
      Main.class);


  @Test
  public void testShopNameSelection() {
    // Type text and then press the button.
    onView(withId(R.id.shopName)).perform(clearText(), typeText("Mecc"), ViewActions.pressImeActionButton());
    onView(withId(R.id.shopName)).check(matches(withText("")));

    onView(withId(R.id.shopName)).perform(clearText(), typeText("MeccaAr"), ViewActions.pressImeActionButton());
    //if only 1 match it should be able to find the job
    onView(withId(R.id.shopName)).check(matches(withText("MeccaArt")));

    onView(withId(R.id.shopName)).perform(clearText(), typeText("MeCcAArT"), ViewActions.pressImeActionButton());
    //Case insensetive
    onView(withId(R.id.shopName)).check(matches(withText("MeccaArt")));
  }

}