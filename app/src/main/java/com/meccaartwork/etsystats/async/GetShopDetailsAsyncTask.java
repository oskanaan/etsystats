package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;
import com.meccaartwork.etsystats.util.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by oskanaan on 22/04/17.
 */

public class GetShopDetailsAsyncTask extends NetworkEnabledAsyncTask{
  String shopNameText;
  private int shopId;
  private String shopName;
  private String shopTitle;
  private String imageUrl;
  private Drawable drawable;

  private TextView shopNameView;
  private TextView shopTitleView;
  private ImageView shopIconView;
  private Context context;

  public GetShopDetailsAsyncTask(Context context, TextView shopNameView, TextView shopTitleView, ImageView shopIconView){
    super(context);
    this.shopNameView = shopNameView;
    this.shopTitleView = shopTitleView;
    this.shopIconView = shopIconView;
    this.context = context;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    shopNameText = shopNameView.getText().toString();
  }

  @Override
  protected Object doInBackground(Object[] params) {
    JSONArray shops = EtsyApi.getInstance().getShopData(context, shopNameText);
    JSONObject match = null;
    int length = shops.length();
    try {
      if (shops.length() == 1){
        match = (JSONObject) shops.get(0);
        length = 1;
      } else {
        //Check if an exact match exist
        for(int i=0;i<shops.length();i++){
          if(((JSONObject)shops.get(i)).getString("shop_name").equalsIgnoreCase(shopNameText)){
            match = (JSONObject)shops.get(i);
            length = 1;
            break;
          }
        }
      }

      if(match != null){
        shopId = match.getInt("shop_id");
        shopName = match.getString("shop_name");
        shopTitle = match.getString("title");
        imageUrl = match.getString("icon_url_fullxfull");

        drawable = new ImageLoader(context, 350).getDrawable(imageUrl);
      }
    } catch (JSONException e) {
      Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
    }
    return length;
  }

  @Override
  protected void onPostExecute(Object o) {
    int length = (Integer)o;
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    if (length != 1){
      prefs.edit().putString("shop_name", null).commit();
      prefs.edit().putString("shop_title", null).commit();
      prefs.edit().putString("icon_url", null).commit();
      prefs.edit().putInt("shop_id", -1).commit();

      shopNameView.setText("");
      shopTitleView.setText("");
      shopIconView.setImageResource(R.drawable.stub);

      if(length > 1){
        Toast.makeText(context, R.string.error_multiple_shops_with_similar_name, Toast.LENGTH_LONG).show();
      } else {
        Toast.makeText(context, R.string.error_no_shop_with_similar_name,Toast.LENGTH_LONG).show();
      }
      return;
    }
    else {
      prefs.edit().putString("shop_name", shopName).commit();
      prefs.edit().putString("shop_title", shopTitle).commit();
      prefs.edit().putString("icon_url", imageUrl).commit();
      prefs.edit().putInt("shop_id", shopId).commit();
      shopNameView.setText(shopName);
      shopTitleView.setText(shopTitle);
      if(drawable != null){
        shopIconView.setImageDrawable(drawable);
      } else {
        shopIconView.setImageResource(R.drawable.stub);
      }
      Toast.makeText(context, R.string.shop_found_and_id_saved, Toast.LENGTH_LONG).show();

      return;
    }
  }
}
