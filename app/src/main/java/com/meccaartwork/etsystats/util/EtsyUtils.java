package com.meccaartwork.etsystats.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.meccaartwork.etsystats.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Created by oskanaan on 27/03/2016.
 */
public class EtsyUtils {

  private static String shopId;

  public static boolean storeShopIdFromName(final Context context, final String name) throws IOException, JSONException {
    new AsyncTask(){

      private String shopId;
      @Override
      protected Object doInBackground(Object[] params) {
        String url = "https://openapi.etsy.com/v2/shops?api_key=z5u6dzy42ve0vsdfyhhgrf98&shop_name="+name;

        return getResultsFromUrl(url);
      }

      @Override
      protected void onPostExecute(Object o) {
        JSONArray listings = (JSONArray)o;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (listings.length() > 1){
          Toast.makeText(context, R.string.error_multiple_shops_with_similar_name, Toast.LENGTH_LONG).show();
          prefs.edit().putString("shop_name", null).commit();
          prefs.edit().putString("shop_id", null).commit();

        }
        else if(listings.length() == 0){
          Toast.makeText(context, R.string.error_no_shop_with_similar_name,Toast.LENGTH_LONG).show();
          prefs.edit().putString("shop_name", null).commit();
          prefs.edit().putString("shop_id", null).commit();
        }
        else{
          try {
            prefs.edit().putString("shop_id", ((JSONObject) listings.get(0)).getString("shop_id")).commit();
            shopId = ((JSONObject) listings.get(0)).getString("shop_id");
            Toast.makeText(context, R.string.shop_found_and_id_saved, Toast.LENGTH_LONG).show();
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      }
    }.execute();


    return true;
  }

  public static String getShopId(Context context){
    if(shopId != null){
      return shopId;
    }
    else{
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      return prefs.getString("shop_id", null);
    }

  }

  public static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONArray getResultsFromUrl(String url){
    JSONObject json = null;
    InputStream is = null;
    int resultCount = -1;
    JSONArray listings = null;
    try {
      is = new URL(url).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = EtsyUtils.readAll(rd);
      json = new JSONObject(jsonText);
      listings = (JSONArray) json.get("results");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return listings;
  }

  public static Drawable drawableFromUrl(Resources resources, String url) throws IOException {
    Bitmap x;

    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.connect();
    InputStream input = connection.getInputStream();

    x = BitmapFactory.decodeStream(input);
    return new BitmapDrawable(resources, x);
  }
}
