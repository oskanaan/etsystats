package com.meccaartwork.etsystats;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;

import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.jobs.RefreshListingRank;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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

    setPageTitle();
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

    startRefreshRankScheduledJob();
  }

  private void setPageTitle() {
    String shopTite = PreferenceManager.getDefaultSharedPreferences(this).getString("shop_title", null);
    if(shopTite == null){
      setTitle(R.string.app_name);
    } else {
      setTitle(getString(R.string.app_name)+" - "+shopTite);
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void startRefreshRankScheduledJob() {
    JobScheduler jobScheduler = (JobScheduler)getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
    for(JobInfo pendingJob : jobScheduler.getAllPendingJobs()){
      if(pendingJob.getService().getClassName().equals(RefreshListingRank.class.getName())){
        Log.d(this.getClass().getName(), "Refresh rank job is already scheduled");
        return;
      }
    }

    Log.d(this.getClass().getName(),"Starting rank refresh scheduled job on app startup");
    ComponentName jobService = new ComponentName(getApplicationContext(), RefreshListingRank.class);

    JobInfo jobInfo = new JobInfo.Builder((int) Calendar.getInstance().getTimeInMillis(), jobService)
        .setPeriodic(TimeUnit.HOURS.toMillis(Constants.BACKGROUND_JOB_RUN_HOURS))
        .build();

    int jobId = jobScheduler.schedule(jobInfo);
    Log.d(this.getClass().getName(),"Scheduled RefreshListingRank to run now "+jobId);
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
