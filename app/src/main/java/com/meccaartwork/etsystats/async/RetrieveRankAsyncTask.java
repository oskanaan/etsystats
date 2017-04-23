package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import java.util.Calendar;

/**
 * Retrieves the rank based on the search term usedd
 */

public class RetrieveRankAsyncTask extends AsyncTask {

  private String itemId;
  private TextView viewToUpdate;
  private ProgressBar progressBar;
  private int index;
  private Context context;

  public RetrieveRankAsyncTask(TextView viewToUpdate, ProgressBar progressBar, String itemId, int index){
    this.viewToUpdate = viewToUpdate;
    this.progressBar = progressBar;
    this.itemId = itemId;
    this.index = index;
    this.context = progressBar.getContext();
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  protected Object doInBackground(Object[] params) {
    return EtsyApi.getListingRank(context, itemId, index);
  }

  @Override
  protected void onPostExecute(Object o) {
    progressBar.setVisibility(View.GONE);
    if(o.equals(-1)){
      viewToUpdate.setText(R.string.err_greater_than_200);
    } else {
      viewToUpdate.setText(o.toString());
    }

    String preferenceId = PreferenceNameHelper.getSearchTermLastRefreshed(itemId, index);
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(preferenceId, EtsyUtils.getPreferenceDateFormat().format(Calendar.getInstance().getTime())).commit();
  }
}
