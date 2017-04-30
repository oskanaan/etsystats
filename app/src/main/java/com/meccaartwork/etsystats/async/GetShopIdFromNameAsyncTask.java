package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.util.EtsyApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oskanaan on 29/04/17.
 */

public class GetShopIdFromNameAsyncTask extends NetworkEnabledAsyncTask {

  private int shopId;
  private Context context;
  private String shopName;

  public GetShopIdFromNameAsyncTask(Context context, String shopName){
    super(context);
    this.context = context;
    this.shopName = shopName;
  }


  @Override
  protected Object doInBackground(Object[] params) {
    return EtsyApi.getInstance().getShopData(context, shopName);
  }

  @Override
  protected void onPostExecute(Object o) {
    JSONArray listings = (JSONArray)o;
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    if (listings.length() > 1){
      Toast.makeText(context, R.string.error_multiple_shops_with_similar_name, Toast.LENGTH_LONG).show();
      prefs.edit().putString("shop_name", null).commit();
      prefs.edit().putInt("shop_id", -1).commit();

    }
    else if(listings.length() == 0){
      Toast.makeText(context, R.string.error_no_shop_with_similar_name,Toast.LENGTH_LONG).show();
      prefs.edit().putString("shop_name", null).commit();
      prefs.edit().putInt("shop_id", -1).commit();
    }
    else{
      try {
        prefs.edit().putInt("shop_id", ((JSONObject) listings.get(0)).getInt("shop_id")).commit();
        shopId = ((JSONObject) listings.get(0)).getInt("shop_id");
        Toast.makeText(context, R.string.shop_found_and_id_saved, Toast.LENGTH_LONG).show();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }}
