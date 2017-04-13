package com.meccaartwork.etsystats.async;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    int selection = -1;
    try {

      boolean exit = false;
      int offset = 1;

      while (!exit) {
        String url = "https://openapi.etsy.com/v2/listings/active?api_key=z5u6dzy42ve0vsdfyhhgrf98&keywords=" + Uri.encode(term) + "&sort_on=score&limit=200&offset=" + offset;
        JSONArray listings = EtsyUtils.getResultsFromUrl(url);

        if(listings == null || listings.length() == 0){
          break;
        }

        for (int i = 0; i < listings.length(); i++) {
          JSONObject listing = (JSONObject) listings.get(i);

          if (listing.get("listing_id").equals(Integer.parseInt(itemId))) {
            exit = true;
            selection = i+1;
            break;
          }
        }

        offset += listings.length();
        if (exit || offset > 100 || listings.length() == 0) {
          break;
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return selection;
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
