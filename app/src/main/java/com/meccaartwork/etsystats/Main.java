package com.meccaartwork.etsystats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.meccaartwork.etsystats.data.Constants;

public class Main extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener {

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
   * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
   * derivative, which will keep every loaded fragment in memory. If this becomes too memory
   * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  AppSectionsPagerAdapter mAppSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will display the three primary sections of the app, one at a
   * time.
   */
  ViewPager mViewPager;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
    setContentView(R.layout.activity_main);

    // Create the adapter that will return a fragment for each of the three primary sections
    // of the app.
    mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

    // Set up the action bar.
    final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

    // Specify that the Home/Up button should not be enabled, since there is no hierarchical
    // parent.
    actionBar.setHomeButtonEnabled(false);

    // Specify that we will be displaying tabs in the action bar.
    actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_TABS);

    // Set up the ViewPager, attaching the adapter and setting up a listener for when the
    // user swipes between sections.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mAppSectionsPagerAdapter);
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        // When swiping between different app sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        actionBar.setSelectedNavigationItem(position);
      }
    });

    // For each of the sections in the app, add a tab to the action bar.
    for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
      // Create a tab with text corresponding to the page title defined by the adapter.
      // Also specify this Activity object, which implements the TabListener interface, as the
      // listener for when this tab is selected.
      actionBar.addTab(
          actionBar.newTab()
              .setText(mAppSectionsPagerAdapter.getPageTitle(i))
              .setTabListener(Main.this));
    }

    Bundle bundle = getIntent().getExtras();
    if(bundle != null && bundle.getBoolean(Constants.FROM_NOTIFICATION, false)){
      mViewPager.setCurrentItem(3, true);

    }
  }

  @Override
  public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {
    mViewPager.setCurrentItem(tab.getPosition(), true);
  }

  @Override
  public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {
  }

  @Override
  public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {
  }

  public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

    public AppSectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int i) {
      switch (i) {
        case 0:
          return new Settings();
        case 1:
          return new QuickAccess();
        case 2:
          return new ShopCategories();
        case 3:
          return new RankChangeFragment();

        default:
          return null;
      }
    }

    @Override
    public int getCount() {
      return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position){
        case 0:
          return Main.this.getApplicationContext().getString(R.string.title_activity_settings);
        case 1:
          return Main.this.getApplicationContext().getString(R.string.quick_access);
        case 2:
          return Main.this.getApplicationContext().getString(R.string.title_activity_shop_categories);
        case 3:
          return getApplicationContext().getString(R.string.rank_change);
        default:
          return "";
      }
    }
  }
}
