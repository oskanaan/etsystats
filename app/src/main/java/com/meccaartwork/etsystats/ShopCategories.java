package com.meccaartwork.etsystats;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meccaartwork.etsystats.adapter.CategoryAdapter;
import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopCategories extends Fragment {

  CategoryAdapter adapter;
  private View loadingPanel;
  private View root;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    root = inflater.inflate(R.layout.content_shop_categories, container, false);
    ListView shopCategories = (ListView) root.findViewById(R.id.shopCategories);
    shopCategories.setEmptyView(inflater.inflate(R.layout.empty_list, container, false));

    ((ListView)root.findViewById(R.id.shopCategories)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject obj = (JSONObject)parent.getAdapter().getItem(position);
        if(adapter != null){
          Bundle bundle = new Bundle();
          try {
            bundle.putInt(Constants.SECTION_ID, obj.getInt("shop_section_id"));
          } catch (JSONException e) {
            Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
          }

          Intent startCategoryListings = new Intent();
          startCategoryListings.putExtras(bundle);
          startCategoryListings.setClassName("com.meccaartwork.etsystats", "com.meccaartwork.etsystats.CategoryListings");
          startActivity(startCategoryListings);
        }
      }
    });
    final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swiperefresh);
    loadingPanel = root.findViewById(R.id.loadingPanel);
    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshLayout.setRefreshing(false);
        new AsyncLoadData(getContext(), root).execute();
      }
    });

    new AsyncLoadData(getContext(), root).execute();
    return root;
  }

  private class AsyncLoadData extends AsyncTask {

    private View view;
    private Context context;

    public AsyncLoadData(Context context, View view){
      this.context = context;
      this.view = view;
    }

    @Override
    protected Object doInBackground(Object[] params) {
      int shopId = EtsyUtils.getShopId(getContext());
      if(shopId == -1){
        return null;
      } else {
        return EtsyApi.getInstance().getShopCategories(context, shopId);
      }
    }

    @Override
    protected void onPreExecute() {
      loadingPanel.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Object o) {
      loadingPanel.setVisibility(View.GONE);

      if(o == null){
        return;
      }

      JSONArray returnedData = (JSONArray) o;
      JSONArray filtered = new JSONArray();

      for(int i=0 ; i<returnedData.length() ; i++){
        try {
          JSONObject jsonObject = (JSONObject) returnedData.get(i);
          if(jsonObject.getInt("active_listing_count") > 0){
            filtered.put(jsonObject);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      //Add non categorized section
      try {
        filtered.put(new JSONObject("{shop_section_id:"+Constants.NO_CATEGORY+", title:\"No category\"}"));
      } catch (JSONException e) {
        e.printStackTrace();
      }
      adapter = new CategoryAdapter(getContext(), filtered, R.layout.etsy_listing, null, null, "shop_section_id");
      ((ListView)view.findViewById(R.id.shopCategories)).setAdapter(adapter);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PreferenceNameHelper.getFavouriteChangeIndicatorName(), false)){
      PreferenceManager.getDefaultSharedPreferences(getContext())
          .edit()
          .putBoolean(PreferenceNameHelper.getFavouriteChangeIndicatorName(), false)
          .commit();
      new AsyncLoadData(getContext(), root).execute();
    }
  }
}
