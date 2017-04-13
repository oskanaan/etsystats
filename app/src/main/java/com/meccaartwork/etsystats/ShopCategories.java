package com.meccaartwork.etsystats;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class ShopCategories extends AppCompatActivity {

  ListingAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shop_categories);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ((ListView)ShopCategories.this.findViewById(R.id.shopCategories)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject obj = (JSONObject)parent.getAdapter().getItem(position);
        if(adapter != null){
          Bundle bundle = new Bundle();
          try {
            bundle.putString(Constants.SECTION_ID, obj.getString("shop_section_id"));
          } catch (JSONException e) {
            e.printStackTrace();
          }

          Intent startCategoryListings = new Intent();
          startCategoryListings.putExtras(bundle);
          startCategoryListings.setClassName("com.meccaartwork.etsystats", "com.meccaartwork.etsystats.CategoryListings");
          startActivity(startCategoryListings);
        }
      }
    });

    new AsyncLoadData().execute();

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
      String shopId = EtsyUtils.getShopId(ShopCategories.this);

      String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections?api_key=z5u6dzy42ve0vsdfyhhgrf98&includes=Images:1";
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
      //Add non categorized section
      try {
        returnedData.put(new JSONObject("{shop_section_id:"+Constants.NO_CATEGORY+", title:\"No category\"}"));
      } catch (JSONException e) {
        e.printStackTrace();
      }
      adapter = new ListingAdapter(ShopCategories.this, returnedData, R.layout.etsy_listing, null, null, "shop_section_id");
      ((ListView)ShopCategories.this.findViewById(R.id.shopCategories)).setAdapter(adapter);

    }
  }
}
