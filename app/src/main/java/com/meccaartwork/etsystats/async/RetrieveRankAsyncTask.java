package com.meccaartwork.etsystats.async;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.util.EtsyApi;

/**
 * Retrieves the rank based on the search term usedd
 */

public class RetrieveRankAsyncTask extends AsyncTask {

  private String term;
  private String itemId;
  private TextView viewToUpdate;
  private ProgressBar progressBar;

  public RetrieveRankAsyncTask(TextView viewToUpdate, ProgressBar progressBar, String term, String itemId){
    this.viewToUpdate = viewToUpdate;
    this.progressBar = progressBar;
    this.term = term;
    this.itemId = itemId;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  protected Object doInBackground(Object[] params) {
    return EtsyApi.getListingRank(itemId, term);
  }

  @Override
  protected void onPostExecute(Object o) {
    if(o.equals(-1)){
      viewToUpdate.setText(R.string.err_greater_than_200);
    } else {
      viewToUpdate.setText(o.toString());
    }

    progressBar.setVisibility(View.INVISIBLE);

  }
}
