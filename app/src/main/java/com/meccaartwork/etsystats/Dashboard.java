package com.meccaartwork.etsystats;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meccaartwork.etsystats.adapter.ListingAdapter;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends Activity{

  ListingAdapter adapter ;
  List<JSONObject> data = new ArrayList<JSONObject>();
  int selection = 0;
  ListView mDrawerList;

  static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
      ContactsContract.Data.DISPLAY_NAME};

  // This is the select criteria
  static final String SELECTION = "((" +
      ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
      ContactsContract.Data.DISPLAY_NAME + " != '' ))";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);
    ((SearchView)findViewById(R.id.searchItem)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        new AsyncLoadData().execute("cutting board");
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });

    mDrawerList = (ListView) findViewById(R.id.left_drawer);
    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, new String[]{"Search Term", "My Shop", "Settings"}));
    mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 1){
          startActivity(new Intent(Dashboard.this, ShopCategories.class));
        } else if(position == 2){
          //settings
          startActivity(new Intent(Dashboard.this, SettingsActivity.class));
        }
      }
    });
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_dashboard, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private class AsyncLoadData extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] params) {
      try {

        boolean exit = false;
        int offset = 1;
        String query = Uri.encode(params[0].toString());
        while (!exit) {
          String url = "https://openapi.etsy.com/v2/listings/active?api_key=z5u6dzy42ve0vsdfyhhgrf98&includes=Images:1&keywords=" + query + "&limit=200&offset=" + offset;
          JSONArray listings = EtsyUtils.getResultsFromUrl(url);

          for (int i = 0; i < listings.length(); i++) {
            JSONObject listing = (JSONObject) listings.get(i);
            data.add(listing);

            if (listing.get("listing_id").equals(262516286)) {
              exit = true;
              selection = i;
              break;
            }
          }

          offset += 200;
          if (exit || offset > 100 || listings.length() == 0) {
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
      adapter = new ListingAdapter(Dashboard.this, returnedData, R.layout.etsy_listing, null, null, "listing_id");
      ((ListView)Dashboard.this.findViewById(R.id.listings)).setAdapter(adapter);
    }
  }

}
