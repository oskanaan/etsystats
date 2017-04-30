package com.meccaartwork.etsystats.jobs;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.meccaartwork.etsystats.data.Constants;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * Created by oskanaan on 16/04/17.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      Log.d(this.getClass().getName(),"Inside boot complete");
      ComponentName jobService = new ComponentName(context, RefreshListingRank.class);

      JobInfo jobInfo = new JobInfo.Builder((int) Calendar.getInstance().getTimeInMillis(), jobService)
          .setPeriodic(TimeUnit.HOURS.toMillis(Constants.BACKGROUND_JOB_RUN_HOURS))
          .build();
      JobScheduler jobScheduler = (JobScheduler)context.getSystemService(JOB_SCHEDULER_SERVICE);

      int jobId = jobScheduler.schedule(jobInfo);
      Log.d(this.getClass().getName(),"Scheduled RefreshListingRank to run now "+jobId);
    }
  }
}
