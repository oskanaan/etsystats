package com.meccaartwork.etsystats;

import android.content.Intent;
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

import com.meccaartwork.etsystats.async.LoadQuickAccessListingsAsyncTask;
import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuickAccess extends Fragment {

  private View loadingPanel;
  private View root;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    root = inflater.inflate(R.layout.content_my_items, container, false);
    final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swiperefresh);
    ListView categoryListings = ((ListView)root.findViewById(R.id.quickAccess));
    categoryListings.setEmptyView(root.findViewById(R.id.noResults));
    loadingPanel = root.findViewById(R.id.loadingPanel);

    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshLayout.setRefreshing(false);
        new LoadQuickAccessListingsAsyncTask(getContext(), loadingPanel).execute(root);
      }
    });
    new LoadQuickAccessListingsAsyncTask(getContext(), loadingPanel).execute(root);

    ((ListView)root.findViewById(R.id.quickAccess)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject obj = (JSONObject) parent.getAdapter().getItem(position);
        if (parent.getAdapter() != null) {
          Bundle bundle = new Bundle();
          try {
            bundle.putString(Constants.LISTING_ID, obj.getString("listing_id"));
            bundle.putString(Constants.LISTING_TITLE, obj.getString("title"));
            bundle.putString(Constants.LISTING_IMAGE_URL, ((JSONObject) ((JSONArray)obj.get("Images")).get(0)).getString("url_570xN"));
          } catch (JSONException e) {
            Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
          }

          Intent startListingOptions = new Intent();
          startListingOptions.putExtras(bundle);
          startListingOptions.setClassName("com.meccaartwork.etsystats", "com.meccaartwork.etsystats.ListingOptions");
          startActivity(startListingOptions);
        }
      }
    });

    return root;
  }

  @Override
  public void onResume() {
    super.onResume();
    if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PreferenceNameHelper.getFavouriteChangeIndicatorName(), false)){
      PreferenceManager.getDefaultSharedPreferences(getContext())
          .edit()
          .putBoolean(PreferenceNameHelper.getFavouriteChangeIndicatorName(), false)
          .commit();
      new LoadQuickAccessListingsAsyncTask(getContext(), loadingPanel).execute(root);
    }
  }

  //  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(R.menu.options_menu, menu);
//
//    // Associate searchable configuration with the SearchView
//    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//      @Override
//      public boolean onQueryTextSubmit(String query) {
//        return reloadResults(query);
//      }
//
//      @Override
//      public boolean onQueryTextChange(String newText) {
//        return reloadResults(newText);
//      }
//    });
//    return true;
//  }

//  private boolean reloadResults(String text) {
//    try {
//      adapter.filterData(text);
//    } catch (JSONException e) {
//      e.printStackTrace();
//      return false;
//    }
//
//    return true;
//  }

}
