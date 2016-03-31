package com.meccaartwork.etsystats;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

  List<JSONObject> data = new ArrayList<JSONObject>();
  ListingAdapter adapter;
  String categoryId;

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
//    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//    fab.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//            .setAction("Action", null).show();
//      }
//    });
  }

  private class AsyncLoadData extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
      String shopId = EtsyUtils.getShopId(CategoryListings.this);

      String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections/"+categoryId+"/listings/active?api_key=z5u6dzy42ve0vsdfyhhgrf98&includes=Images:1";
      JSONArray sections = EtsyUtils.getResultsFromUrl(url);

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
