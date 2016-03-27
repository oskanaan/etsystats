package com.meccaartwork.etsystats;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.meccaartwork.etsystats.adapter.ListingAdapter;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopCategories extends AppCompatActivity {

  List<JSONObject> data = new ArrayList<JSONObject>();
  ListingAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shop_categories);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

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
      try {

        boolean exit = false;
        int offset = 1;
        String shopId = EtsyUtils.getShopId(ShopCategories.this);

        String query = Uri.encode(params[0].toString());
        while (!exit) {
          String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections?api_key=z5u6dzy42ve0vsdfyhhgrf98&includes=Images:1";
          JSONArray sections = EtsyUtils.getResultsFromUrl(url);

          for (int i = 0; i < sections.length(); i++) {
            JSONObject listing = (JSONObject) sections.get(i);
            data.add(listing);
          }

          offset += 200;
          if (exit || offset > 100 || sections.length() == 0) {
            break;
          }
        }

      } catch (JSONException e) {
        e.printStackTrace();
      }

      return data;
    }

    @Override
    protected void onPostExecute(Object o) {
      super.onPostExecute(o);
      List<JSONObject> returnedData = (List<JSONObject>) o;
      adapter = new ListingAdapter(ShopCategories.this, returnedData, R.layout.etsy_listing, null, null, "section_id");
      ((ListView)ShopCategories.this.findViewById(R.id.listings)).setAdapter(adapter);
    }
  }
}
