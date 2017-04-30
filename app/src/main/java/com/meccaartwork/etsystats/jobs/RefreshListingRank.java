package com.meccaartwork.etsystats.jobs;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.meccaartwork.etsystats.async.RefreshAllRanks;
import com.meccaartwork.etsystats.util.EtsyUtils;

import java.util.Calendar;

/**
 * Created by oskanaan on 16/04/17.
 */


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RefreshListingRank extends JobService {
  public static String JOB_RUN_PREFIX = "scheduleAlreadyRun";

  @Override
  public boolean onStartJob(JobParameters params) {
    if(!EtsyUtils.isInternetAvailable(getApplicationContext())){
      jobFinished(params, true);
      Log.d(this.getClass().getName(), "Rescheduling job as there is no internet connection.");
      jobFinished(params, true);
      return false;
    }
    Calendar cal = Calendar.getInstance();
    int calendarHour = cal.get(Calendar.HOUR_OF_DAY);
    Log.d(this.getClass().getName(), "Running job at "+calendarHour+", already run status = "+isJobAlreadyRun());
    if(calendarHour < 10 || calendarHour > 20 || isJobAlreadyRun()){
      Log.d(this.getClass().getName(), "Rescheduling job");
      jobFinished(params, true);
      return false;
    }
    Log.d(this.getClass().getName(), "On start job. Params = "+params);
    new RefreshAllRanks(getApplicationContext(), this, params).execute();
    return true;
  }

  @Override
  public boolean onStopJob(JobParameters params) {
    Log.d(this.getClass().getName(), "On stop job. called");
    return false;
  }

  private boolean isJobAlreadyRun(){
    return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(JOB_RUN_PREFIX+Calendar.getInstance().get(Calendar.DATE), false);
  }

}
