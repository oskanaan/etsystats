package com.meccaartwork.etsystats.jobs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * Created by oskanaan on 16/04/17.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      Log.d(this.getClass().getName(),"Inside boot complete");
      ComponentName jobService = new ComponentName(context, RefreshListingRank.class);

      JobInfo jobInfo = new JobInfo.Builder((int) Calendar.getInstance().getTimeInMillis(), jobService)
          .setPeriodic(TimeUnit.SECONDS.toMillis(5))
          .build();
      JobScheduler jobScheduler = (JobScheduler)context.getSystemService(JOB_SCHEDULER_SERVICE);

      int jobId = jobScheduler.schedule(jobInfo);
      Log.d(this.getClass().getName(),"Scheduled RefreshListingRank to run now "+jobId);
    }
  }
}
