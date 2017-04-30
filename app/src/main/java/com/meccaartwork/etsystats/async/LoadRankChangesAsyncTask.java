package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.RankChangeRecyclerViewAdapter;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by oskanaan on 29/04/17.
 */

public class LoadRankChangesAsyncTask extends NetworkEnabledAsyncTask {

  String TAG = this.getClass().getSimpleName();
  private Context context;
  private View view;
  private View loadingView;

  public LoadRankChangesAsyncTask(Context context, View view, View loadingView) {
    super(context);
    this.context = context;
    this.view = view;
    this.loadingView = loadingView;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    Log.d(TAG, "Setting loading animation to visible");
    this.loadingView.setVisibility(View.VISIBLE);
  }

  @Override
  protected Object doInBackground(Object[] params) {
    try {
      int shopId = EtsyUtils.getShopId(context);

      if(shopId != -1){
        return EtsyApi.getInstance().getShopListingsWithRankChanges(context, shopId);
      }
    } catch (JSONException e) {
      Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
    }
    return null;
  }

  @Override
  protected void onPostExecute(Object o) {
    super.onPostExecute(o);
    Log.d(TAG, "Setting loading animation to GONE");
    this.loadingView.setVisibility(View.GONE);
    if(o!=null){
      Log.d(TAG, "Refreshing list data with results from rank changes, changes found = "+o);
      ((RecyclerView)view.findViewById(R.id.list)).setAdapter(new RankChangeRecyclerViewAdapter(context, (JSONArray) o));
    }
  }
}
