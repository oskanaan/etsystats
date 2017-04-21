package com.meccaartwork.etsystats.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.async.RetrieveRankAsyncTask;
import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by oskanaan on 16/04/17.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RefreshListingRank extends JobService {
  private JobParameters parameters;
  static String JOB_RUN_PREFIX = "scheduleAlreadyRun";

  @Override
  public boolean onStartJob(JobParameters params) {
    Calendar cal = Calendar.getInstance();
    int calendarHour = cal.get(Calendar.HOUR_OF_DAY);
    Log.d(this.getClass().getName(), "Running job at "+calendarHour+", already run status = "+isJobAlreadyRun());
//    if(calendarHour < 10 || calendarHour > 20 || isJobAlreadyRun()){
//      Log.d(this.getClass().getName(), "Rescheduling job");
//      jobFinished(params, true);
//      return false;
//    }
    this.parameters = params;
    Log.d(this.getClass().getName(), "On start job. Params = "+params);
    new RefreshAllRanks().execute();
    return true;
  }

  @Override
  public boolean onStopJob(JobParameters params) {
    Log.d(this.getClass().getName(), "On stop job. called");
    return false;
  }

  private class RefreshAllRanks extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] params) {
      Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getAll();

      Log.d(this.getClass().getName(), "Refreshing ranks from server.");
      for(String key : prefs.keySet()){
        Log.d(this.getClass().getName(), "Got key : "+key);
        if(key.startsWith(Constants.REFRESH_PERIOD_PREFIX) && key.contains("#")){
          String listingId = key.split("#")[1];
          if(!isRefreshDue(listingId)){
            Log.d(this.getClass().getName(), "Refresh not due yet, continue with other items.");
            continue;
          }
          Log.d(this.getClass().getName(), "Refreshing ranks for listing ID: "+listingId);
          for(int i=1; i<Constants.MAX_SEARCH_TERMS; i++){
            String term = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PreferenceNameHelper.getSearchTermName(listingId, i), null);
            Log.d(this.getClass().getName(), "Refreshing ranks for listing ID: "+listingId+", search term : "+term);
            if(term != null){
              int rank = EtsyApi.getListingRank(listingId, term);
              String previousRank = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PreferenceNameHelper.getSearchTermRankName(listingId, i), null);
              if(previousRank != null){
                Log.d(this.getClass().getName(), "Setting previous rank for listing "+listingId+" to "+previousRank);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(
                    PreferenceNameHelper.getSearchTermRankName(listingId, i),
                    previousRank
                ).commit();
              }
              String rankValue = (rank == -1?getApplicationContext().getText(R.string.err_greater_than_200).toString(): Integer.toString(rank));
              Log.d(this.getClass().getName(), "Setting rank for listing "+listingId+" to "+rankValue);
              PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(
                  PreferenceNameHelper.getSearchTermRankName(listingId, i),
                  rankValue
              ).commit();

              String preferenceId = PreferenceNameHelper.getSearchTermLastRefreshed(listingId);
              PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(preferenceId, EtsyUtils.getPreferenceDateFormat().format(Calendar.getInstance().getTime())).commit();

            }
          }
        }
      }

      return null;
    }

    private boolean isRefreshDue(String listingId) {
      String preferenceId = PreferenceNameHelper.getSearchTermLastRefreshed(listingId);
      String lastRefreshed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(preferenceId, null);
      int refreshHours = EtsyUtils.getRefreshPeriodInHours(getApplicationContext(), listingId);
      Log.d(this.getClass().getName(), "Item last refreshed at "+lastRefreshed+", expected refresh is after "+refreshHours+" hours");
      if(refreshHours == -1){
        return false;
      }
      if(lastRefreshed == null){
        return true;
      } else {
        long lastRefreshedDate = 0;
        try {
          lastRefreshedDate = EtsyUtils.getPreferenceDateFormat().parse(lastRefreshed).getTime();
        } catch (ParseException e) {
          e.printStackTrace();
        }
        if(Calendar.getInstance().getTimeInMillis() < lastRefreshedDate+TimeUnit.HOURS.toMinutes(refreshHours)){
          Log.d(this.getClass().getName(), "Item is due for refresh.");
          return true;
        }
      }
      return false;
    }

    @Override
    protected void onPostExecute(Object o) {
      String preferenceId = JOB_RUN_PREFIX+Calendar.getInstance().get(Calendar.DATE);
      for(String key: PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getAll().keySet()){
        if(key.startsWith(JOB_RUN_PREFIX)){
          PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove(key).commit();
        }
      }
      PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(preferenceId, true).commit();

      jobFinished(parameters, true);
      Log.d(this.getClass().getName(), "Job finished : "+parameters);
    }
  }

  private boolean isJobAlreadyRun(){
    return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(JOB_RUN_PREFIX+Calendar.getInstance().get(Calendar.DATE), false);
  }
}
