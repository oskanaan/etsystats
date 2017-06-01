package com.meccaartwork.etsystats.async;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.adapter.ListingAdapter;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;

/**
 * Created by oskanaan on 22/04/17.
 */

public class LoadCategoryListingsAsyncTask extends NetworkEnabledAsyncTask {
  private View loadingPanel ;
  private Activity activity;
  private int categoryId;

  public LoadCategoryListingsAsyncTask(Activity activity, View loadingPanel, int categoryId){
    super(activity);
    this.loadingPanel = loadingPanel;
    this.activity = activity;
    this.categoryId = categoryId;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    loadingPanel.setVisibility(View.VISIBLE);
  }

  @Override
  protected Object doInBackground(Object[] params) {
    int shopId = EtsyUtils.getShopId(activity);
    return EtsyApi.getInstance().getCategoryListings(activity, shopId, categoryId);
  }

  @Override
  protected void onPostExecute(Object o) {
    loadingPanel.setVisibility(View.GONE);
    if(o==null){
      return;
    }
    super.onPostExecute(o);
    JSONArray returnedData = (JSONArray) o;
    ListingAdapter adapter = new ListingAdapter(activity, returnedData, R.layout.etsy_listing, null, null, "listing_id");
    ((ListView)activity.findViewById(R.id.categoryListings)).setAdapter(adapter);
  }

}
