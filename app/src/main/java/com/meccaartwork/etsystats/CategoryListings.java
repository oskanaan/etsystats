package com.meccaartwork.etsystats;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meccaartwork.etsystats.adapter.ListingAdapter;
import com.meccaartwork.etsystats.async.LoadCategoryListingsAsyncTask;
import com.meccaartwork.etsystats.data.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryListings extends AppCompatActivity {

  int categoryId;

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
      ((ListingAdapter)((ListView)findViewById(R.id.categoryListings)).getAdapter()).filterData(text);
    } catch (JSONException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle bundle = getIntent().getExtras();
    categoryId = bundle.getInt(Constants.SECTION_ID);

    setContentView(R.layout.activity_category_listing);
    ListView categoryListings = ((ListView)findViewById(R.id.categoryListings));
    categoryListings.setEmptyView(findViewById(R.id.noResults));
    View loadingView = findViewById(R.id.loadingPanel);

    new LoadCategoryListingsAsyncTask(this, loadingView, categoryId).execute();

    ((ListView)CategoryListings.this.findViewById(R.id.categoryListings)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject obj = (JSONObject) parent.getAdapter().getItem(position);
        ListingAdapter adapter = ((ListingAdapter)((ListView)findViewById(R.id.categoryListings)).getAdapter());
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

}
