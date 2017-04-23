package com.meccaartwork.etsystats.jobs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.meccaartwork.etsystats.Main;
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
          Log.d(this.getClass().getName(), "Refreshing ranks for listing ID: "+listingId);
          for(int i=1; i<Constants.MAX_SEARCH_TERMS; i++){
            if(!isRefreshDue(listingId, i)){
              Log.d(this.getClass().getName(), "Refresh not due yet, continue with other search terms.");
              continue;
            }
            String term = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PreferenceNameHelper.getSearchTermName(listingId, i), null);
            Log.d(this.getClass().getName(), "Refreshing ranks for listing ID: "+listingId+", search term : "+term);
            if(term != null){
              int rank = EtsyApi.getListingRank(getApplicationContext(), listingId, i);
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

              String preferenceId = PreferenceNameHelper.getSearchTermLastRefreshed(listingId, i);
              PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(preferenceId, EtsyUtils.getPreferenceDateFormat().format(Calendar.getInstance().getTime())).commit();

            }
          }
        }
      }

      return null;
    }

    private boolean isRefreshDue(String listingId, int index) {
      String preferenceId = PreferenceNameHelper.getSearchTermLastRefreshed(listingId, index);
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

      //Show a notification that the job is ready if the user has enabled it.
      Boolean isShowPushNotifications = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("show_push_notifications", true);
      if(isShowPushNotifications){
        generateNotification(getApplicationContext());
      }
    }
  }

  private boolean isJobAlreadyRun(){
    return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(JOB_RUN_PREFIX+Calendar.getInstance().get(Calendar.DATE), false);
  }

  private static void generateNotification(Context context) {
    Intent intent = new Intent(context, Main.class);
    intent.putExtra(Constants.FROM_NOTIFICATION, true);
    NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
    mBuilder.setContentTitle(context.getString(R.string.refresh_done))
        .setContentText(context.getString(R.string.press_here_for_details))
        .setSmallIcon(R.drawable.notification_icon)
        .setContentIntent(PendingIntent.getActivity(context, 21330, intent, 0));
    mNotifyManager.notify(21223, mBuilder.build());
  }
}
