package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.adapter.ListingAdapter;
import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oskanaan on 23/04/17.
 */

public class LoadQuickAccessListingsAsyncTask extends NetworkEnabledAsyncTask {

  private View view;
  private Context context;
  private View loadingPanel;

  public LoadQuickAccessListingsAsyncTask(Context context, View loadingPanel){
    super(context);
    this.context = context;
    this.loadingPanel = loadingPanel;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    this.loadingPanel.setVisibility(View.VISIBLE);
  }

  @Override
  protected Object doInBackground(Object[] params) {
    this.view = (View) params[0];
    int shopId = EtsyUtils.getShopId(context);

    if(shopId == -1){
      return null;
    }
    JSONArray listings = EtsyApi.getInstance().getAllShopListings(context.getApplicationContext(), shopId);
    JSONArray quickAccessListings = new JSONArray();

    for(int i=0 ; i<listings.length() ; i++){
      try {
        JSONObject jsonObject = listings.getJSONObject(i);
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceNameHelper.getFavouriteName(jsonObject.getString("listing_id")), false)){
          quickAccessListings.put(jsonObject);
        }
      } catch (JSONException e) {
        Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
      }
    }

    return quickAccessListings;
  }

  @Override
  protected void onPostExecute(Object o) {
    super.onPostExecute(o);
    loadingPanel.setVisibility(View.GONE);
    SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
    refreshLayout.setRefreshing(false);
    JSONArray returnedData;
    if(o==null){
      returnedData = new JSONArray();
    } else {
      returnedData = (JSONArray) o;
    }

    ListingAdapter adapter = new ListingAdapter(context, returnedData, R.layout.etsy_listing, null, null, "listing_id");
    ((ListView)view.findViewById(R.id.quickAccess)).setAdapter(adapter);

  }
}
