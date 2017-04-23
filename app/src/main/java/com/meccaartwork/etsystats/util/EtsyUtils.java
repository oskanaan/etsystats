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
import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;

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
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Created by oskanaan on 27/03/2016.
 */
public class EtsyUtils {

  private static int shopId = -1;

  public static SimpleDateFormat getPreferenceDateFormat(){
    return new SimpleDateFormat("dd-MM-yyyy HH:mm");

  }

  public static int getShopId(Context context){
    if(shopId != -1){
      return shopId;
    }
    else{
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      return prefs.getInt("shop_id", -1);
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

  public static JSONObject getDataFromUrl(String url){
    JSONObject json = null;
    InputStream is = null;
    try {
      is = new URL(url).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = EtsyUtils.readAll(rd);
      json = new JSONObject(jsonText);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return json;
  }

  public static JSONArray getResults(JSONObject jsonObject){
    try {
      return (JSONArray) jsonObject.get("results");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static JSONArray getResultsFromUrl(String url){
    try {
      return (JSONArray) getDataFromUrl(url).get("results");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static Drawable drawableFromUrl(Resources resources, String url) throws IOException {
    Bitmap x;

    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.connect();
    InputStream input = connection.getInputStream();

    x = BitmapFactory.decodeStream(input);
    return new BitmapDrawable(resources, x);
  }

  public static int getRefreshPeriodInHours(Context context, String listingId){
    int position = PreferenceManager.getDefaultSharedPreferences(context).getInt(PreferenceNameHelper.getPeriodPrefixName(listingId), -1);
    switch(position){
      case -1:
        return -1;
      case 0:
        return 1;
      case 1:
        return 24;
      case 2:
        return 48;
      case 3:
        return 24*7;
      default:
        return -1;
    }
  }

  public static int compareRankToPrevious(Context context, String listingId, int index){
    String currentRankPref = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getSearchTermRankName(listingId, index), "");
    String prevRankPref = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getPreviousSearchTermRankName(listingId, index), "");
    int currentRank = -1;
    int prevRank = -1;
    if(!currentRankPref.equals("")){
      if(currentRankPref.equals(context.getString(R.string.err_greater_than_200))){
        currentRank = 1000;
      } else {
        currentRank = Integer.parseInt(currentRankPref);
      }
    }
    if(!prevRankPref.equals("")){
      if(prevRankPref.equals(context.getString(R.string.err_greater_than_200))){
        prevRank = 1000;
      } else {
        prevRank = Integer.parseInt(prevRankPref);
      }
    } else {
      //No previous rank so do not show any image.
      return 0;
    }
    if(currentRank < prevRank){
      return 1;
    } else if(currentRank > prevRank){
      return -1;
    } else {
      return 0;
    }
  }
}
