package com.meccaartwork.etsystats;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meccaartwork.etsystats.adapter.ListingAdapter;
import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuickAccess extends Fragment {

  ListingAdapter adapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View root = inflater.inflate(R.layout.content_my_items, container, false);

    new AsyncLoadData().execute(root);

    ((ListView)root.findViewById(R.id.quickAccess)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject obj = (JSONObject) parent.getAdapter().getItem(position);
        if (adapter != null) {
          Bundle bundle = new Bundle();
          try {
            bundle.putString(Constants.LISTING_ID, obj.getString("listing_id"));
            bundle.putString(Constants.LISTING_TITLE, obj.getString("title"));
            bundle.putString(Constants.LISTING_IMAGE_URL, ((JSONObject) ((JSONArray)obj.get("Images")).get(0)).getString("url_570xN"));
          } catch (JSONException e) {
            e.printStackTrace();
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

  private boolean reloadResults(String text) {
    try {
      adapter.filterData(text);
    } catch (JSONException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private class AsyncLoadData extends AsyncTask {

    private View view;
    @Override
    protected Object doInBackground(Object[] params) {
      this.view = (View) params[0];
      int shopId = EtsyUtils.getShopId(getContext());

      String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/listings/active?api_key="+Constants.API_KEY+"&includes=Images:1";
      JSONArray listings = EtsyUtils.getResultsFromUrl(url);
      JSONArray quickAccessListings = new JSONArray();

      for(int i=0 ; i<listings.length() ; i++){
        try {
          JSONObject jsonObject = listings.getJSONObject(i);
          if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("Favourite"+jsonObject.getString("listing_id"), false)){
            quickAccessListings.put(jsonObject);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      return quickAccessListings;
    }

    @Override
    protected void onPostExecute(Object o) {
      if(o==null){
        return;
      }
      super.onPostExecute(o);
      JSONArray returnedData = (JSONArray) o;
      adapter = new ListingAdapter(getContext(), returnedData, R.layout.etsy_listing, null, null, "listing_id");
      ((ListView)view.findViewById(R.id.quickAccess)).setAdapter(adapter);
    }
  }

}
