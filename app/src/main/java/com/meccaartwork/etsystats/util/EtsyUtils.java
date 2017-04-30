package com.meccaartwork.etsystats.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Created by oskanaan on 27/03/2016.
 */
public class EtsyUtils {
  static String TAG = EtsyUtils.class.getSimpleName();

  private static int shopId = -1;

  public static SimpleDateFormat getPreferenceDateFormat(){
    return new SimpleDateFormat("dd-MM-yyyy HH:mm");

  }

  public static int getShopId(Context context){
    if(shopId != -1){
      Log.d(TAG, "Got shop Id = "+shopId);
      return shopId;
    }
    else{
      Log.d(TAG, "Getting shop id from preferences ");
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      Log.d(TAG, "Shop id from preference = "+prefs.getInt("shop_id", -1));
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

  public static JSONObject getDataFromUrl(Context context, String url){
    JSONObject json = null;
    InputStream is = null;
    try {
      is = new URL(url).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = EtsyUtils.readAll(rd);
      json = new JSONObject(jsonText);
    } catch (IOException e) {
      Log.e(EtsyUtils.class.getName(), "getDataFromUrl IO - Couldnt not retrieve data from server: "+e.getMessage());
    } catch (JSONException e) {
      Log.e(EtsyUtils.class.getName(), "getDataFromUrl JSON error - Couldnt not retrieve value from json object: "+e.getMessage());
    }

    return json;
  }

  public static JSONArray getResults(JSONObject jsonObject){
    try {
      return (JSONArray) jsonObject.get("results");
    } catch (JSONException e) {
      Log.e(EtsyUtils.class.getName(), "getResults JSON error - Couldnt not retrieve data from json object: "+e.getMessage());
    }

    return null;
  }

  public static JSONArray getResultsFromUrl(Context context, String url){
    Log.d(TAG, "Retrieving results from server for URL "+url);
    try {
      JSONObject results = getDataFromUrl(context, url);
      if(results == null){
        return new JSONArray();
      }
      Log.d(TAG, "Retrieved results from server, size = "+((JSONArray) results.get("results")).length());
      return (JSONArray) results.get("results");
    } catch (JSONException e) {
      Log.e(EtsyUtils.class.getName(), "getResultsFromUrl JSON error - Couldnt not retrieve data from json object: "+e.getMessage());
    }

    return null;
  }

  /**
   * Address etsy 100 listing limit by looping on all pages from url
   * @param context
   * @param url
   * @return
   */
  public static JSONArray getAllResultsFromUrl(Context context, String url){
    Log.d(TAG, "Retrieving results from server for all pages from url "+url);
    boolean exit = false;
    int offset = 1;
    JSONArray result = new JSONArray();
    while(!exit){
      String paging = "&limit=100&offset=" + offset;
      Log.d(TAG, "paging : "+paging);
      JSONArray listings = EtsyUtils.getResultsFromUrl(context, url+paging);
      if(listings == null || listings.length() == 0){
        break;
      }

      for(int i=0 ; i<listings.length(); i++){
        try {
          result.put(listings.get(i));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      offset += listings.length();
      if (offset > Constants.MAX_RESULTS_CHECK){
        exit = true;
      }
    }

    Log.d(TAG, "Retrieved results from server, size = "+result.length());
    return result;
  }

  public static int getRefreshPeriodInHours(Context context, String listingId){
    int position = PreferenceManager.getDefaultSharedPreferences(context).getInt(PreferenceNameHelper.getPeriodPrefixName(listingId), -1);
    switch(position){
      case -1:
        return -1;
      case 0:
        return 24;
      case 1:
        return 48;
      case 2:
        return 24*7;
      default:
        return -1;
    }
  }

  public static int compareRankToPrevious(Context context, String listingId, int index){
    Log.d(TAG, "Comparing previous rank to current rank for listing "+listingId+" with search term at index "+index);
    String currentRankPref = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getSearchTermRankName(listingId, index), "");
    String prevRankPref = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getPreviousSearchTermRankName(listingId, index), "");
    Log.d(TAG, "currentRankPref = "+currentRankPref+", prevRankPref = "+prevRankPref);
    int currentRank = -1;
    int prevRank = -1;
    if(!currentRankPref.equals("")){
      if(currentRankPref.equals(context.getString(R.string.err_greater_than_max, Constants.MAX_RESULTS_CHECK))){
        currentRank = 1000;
      } else {
        try {
          currentRank = Integer.parseInt(currentRankPref);
        } catch (NumberFormatException pex){
          currentRank = 1000;
        }
      }
    }
    if(!prevRankPref.equals("")){
      if(prevRankPref.equals(context.getString(R.string.err_greater_than_max, Constants.MAX_RESULTS_CHECK))){
        prevRank = 1000;
      } else {
        try {
          prevRank = Integer.parseInt(prevRankPref);
        } catch (NumberFormatException pex) {
          pex.printStackTrace();
          prevRank = 1000;
        }
      }
    } else {
      Log.d(TAG, "Found no previous value, this listing will not be included in the rank changes list "+listingId);
      return 0;
    }
    if(currentRank == -1 || prevRank == -1){
      Log.d(TAG, "No data available for current or previous, ignoring this listing. id = "+listingId);
      return 0;
    }
    if(currentRank < prevRank){
      Log.d(TAG, "Current rank is less than previous rank, this is an improvement. id = "+listingId);
      return 1;
    } else if(currentRank > prevRank){
      Log.d(TAG, "Current rank is greater than previous rank, this is not good. id = "+listingId);
      return -1;
    } else {
      Log.d(TAG, "No rank changes, ignore this listing. Id = "+listingId);
      return 0;
    }
  }

  public static boolean isInternetAvailable(Context context) {
    NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

    if (info == null) {
      Log.d(EtsyUtils.class.getName(),"no internet connection");
      return false;
    } else {
      if(info.isConnected()) {
        Log.d(EtsyUtils.class.getName()," internet connection available...");
        return true;
      } else {
        Log.d(EtsyUtils.class.getName()," internet connection");
        return true;
      }
    }
  }
}
