package com.meccaartwork.etsystats.async;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.meccaartwork.etsystats.Main;
import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;
import com.meccaartwork.etsystats.jobs.RefreshListingRank;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by oskanaan on 27/04/17.
 */

public class RefreshAllRanks extends AsyncTask {

  private Context context;
  private JobService jobService;
  private JobParameters jobParameters;
  
  public RefreshAllRanks(Context context, JobService jobService, JobParameters jobParameters){
    this.context = context;
    this.jobService = jobService;
    this.jobParameters = jobParameters;
  }

  @Override
  protected Object doInBackground(Object[] params) {
    Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(context).getAll();

    Log.d(this.getClass().getName(), "Refreshing ranks from server.");
    for(String key : prefs.keySet()){
      Log.d(this.getClass().getName(), "Got key : "+key);
      if(key.startsWith(Constants.REFRESH_PERIOD_PREFIX) && key.contains("#")){
        String listingId = key.split("#")[1];
        Log.d(this.getClass().getName(), "Refreshing ranks for listing ID: "+listingId);
        for(int i=1; i<Constants.MAX_SEARCH_TERMS+1; i++){
          if(!isRefreshDue(listingId, i)){
            Log.d(this.getClass().getName(), "Refresh not due yet, continue with other search terms.");
            continue;
          }
          String term = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getSearchTermName(listingId, i), null);
          Log.d(this.getClass().getName(), "Refreshing ranks for listing ID: "+listingId+", search term : "+term);
          if(term != null){
            int rank = EtsyApi.getInstance().getListingRank(context, listingId, i);
            String previousRank = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getSearchTermRankName(listingId, i), null);
            if(previousRank != null){
              Log.d(this.getClass().getName(), "Setting previous rank for listing "+listingId+" to "+previousRank);
              PreferenceManager.getDefaultSharedPreferences(context).edit().putString(
                  PreferenceNameHelper.getPreviousSearchTermRankName(listingId, i),
                  previousRank
              ).commit();
            }
            String rankValue = (rank == -1?context.getString(R.string.err_greater_than_max, Constants.MAX_RESULTS_CHECK).toString(): Integer.toString(rank));
            Log.d(this.getClass().getName(), "Setting rank for listing "+listingId+" to "+rankValue);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(
                PreferenceNameHelper.getSearchTermRankName(listingId, i),
                rankValue
            ).commit();

            String preferenceId = PreferenceNameHelper.getSearchTermLastRefreshed(listingId, i);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(preferenceId, EtsyUtils.getPreferenceDateFormat().format(Calendar.getInstance().getTime())).commit();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceNameHelper.getRankChangeIndicatorName(), true).commit();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceNameHelper.getItemRankChangeDismissFlagName(listingId), false).commit();

          }
        }
      }
    }

    return null;
  }

  private boolean isRefreshDue(String listingId, int index) {
    String preferenceId = PreferenceNameHelper.getSearchTermLastRefreshed(listingId, index);
    String lastRefreshed = PreferenceManager.getDefaultSharedPreferences(context).getString(preferenceId, null);
    int refreshHours = EtsyUtils.getRefreshPeriodInHours(context, listingId);
    Log.d(this.getClass().getName(), "Item last refreshed at "+lastRefreshed+", expected refresh is after "+refreshHours+" hours");
    if(refreshHours == -1){
      return false;
    }
    if(lastRefreshed == null){
      return true;
    } else {
      long lastRefreshedDateMillis = 0;
      try {
        lastRefreshedDateMillis = EtsyUtils.getPreferenceDateFormat().parse(lastRefreshed).getTime();
      } catch (ParseException e) {
        Log.e(this.getClass().getName(), "Parse error "+e.getMessage());
      }
      if(Calendar.getInstance().getTimeInMillis() < lastRefreshedDateMillis+ TimeUnit.HOURS.toMillis(refreshHours)){
        Log.d(this.getClass().getName(), "Item is due for refresh.");
        return true;
      }
    }
    return false;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onPostExecute(Object o) {
    String preferenceId = RefreshListingRank.JOB_RUN_PREFIX+Calendar.getInstance().get(Calendar.DATE);
    for(String key: PreferenceManager.getDefaultSharedPreferences(context).getAll().keySet()){
      if(key.startsWith(RefreshListingRank.JOB_RUN_PREFIX)){
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).commit();
      }
    }
    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(preferenceId, true).commit();

    if(jobService != null){
      jobService.jobFinished(this.jobParameters, true);
      Log.d(this.getClass().getName(), "Job finished : "+this.jobParameters);
    }

    //Show a notification that the job is ready if the user has enabled it.
    Boolean isShowPushNotifications = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_push_notifications", true);
    if(isShowPushNotifications){
      generateNotification(context);
    }
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
