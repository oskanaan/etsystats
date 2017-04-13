package com.meccaartwork.etsystats;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meccaartwork.etsystats.adapter.ListingAdapter;
import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryListings extends AppCompatActivity {

  ListingAdapter adapter;
  String categoryId;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options_menu, menu);

    // Associate searchable configuration with the SearchView
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return reloadResults(query);
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return reloadResults(newText);
      }
    });
    return true;
  }

  private boolean reloadResults(String text) {
    try {
      adapter.filterData(text);
    } catch (JSONException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle bundle = getIntent().getExtras();
    categoryId = bundle.getString(Constants.SECTION_ID);

    setContentView(R.layout.activity_category_listing);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    new AsyncLoadData().execute();

    ((ListView)CategoryListings.this.findViewById(R.id.categoryListings)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
  }

  private class AsyncLoadData extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
      String shopId = EtsyUtils.getShopId(CategoryListings.this);

      JSONArray sections;
      if(Integer.parseInt(categoryId) == Constants.NO_CATEGORY){
        String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/listings/active?api_key=z5u6dzy42ve0vsdfyhhgrf98&includes=Images:1";
        JSONArray allListings = EtsyUtils.getResultsFromUrl(url);
        sections = new JSONArray();
        for(int i=0 ; i < allListings.length() ; i++){
          JSONObject jsonObject = null;
          try {
            jsonObject = ((JSONObject) allListings.get(i));
            if(jsonObject.get("shop_section_id") == JSONObject.NULL){
              sections.put(jsonObject);
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      } else {
        String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections/"+categoryId+"/listings/active?api_key=z5u6dzy42ve0vsdfyhhgrf98&includes=Images:1";
        sections = EtsyUtils.getResultsFromUrl(url);
      }

      return sections;
    }

    @Override
    protected void onPostExecute(Object o) {
      if(o==null){
        return;
      }
      super.onPostExecute(o);
      JSONArray returnedData = (JSONArray) o;
      adapter = new ListingAdapter(CategoryListings.this, returnedData, R.layout.etsy_listing, null, null, "listing_id");
      ((ListView)CategoryListings.this.findViewById(R.id.categoryListings)).setAdapter(adapter);
    }
  }
}
