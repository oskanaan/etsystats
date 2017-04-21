package com.meccaartwork.etsystats;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.meccaartwork.etsystats.jobs.RefreshListingRank;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Main extends AppCompatActivity {

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button quickAccessButton = (Button)findViewById(R.id.quickAccessButton);
    Button categoriesButton = (Button)findViewById(R.id.categoriesButton);
    Button shopSettingsButton = (Button)findViewById(R.id.shopSettingsButton);

    quickAccessButton.setOnClickListener(new OpenActivityClickListener(QuickAccess.class.getName()));
    categoriesButton.setOnClickListener(new OpenActivityClickListener(ShopCategories.class.getName()));
    shopSettingsButton.setOnClickListener(new OpenActivityClickListener(SettingsActivity.class.getName()));

    startRefreshRankScheduledJob();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        .setPeriodic(TimeUnit.SECONDS.toMillis(5))
        .build();

    int jobId = jobScheduler.schedule(jobInfo);
    Log.d(this.getClass().getName(),"Scheduled RefreshListingRank to run now "+jobId);
  }

  private class OpenActivityClickListener implements View.OnClickListener{
    String activity;

    public OpenActivityClickListener(String activity){
      this.activity = activity;
    }

    @Override
    public void onClick(View v) {
      Intent startListingOptions = new Intent();
      startListingOptions.setClassName("com.meccaartwork.etsystats", activity);
      startActivity(startListingOptions);
    }
  }


}
