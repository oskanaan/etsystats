package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by oskanaan on 22/04/17.
 */

public class GetShopDetailsAsyncTask extends AsyncTask{
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
    JSONArray shops = EtsyApi.getShopData(shopNameText);
    if (shops.length() == 1){
      try {
        shopId = ((JSONObject) shops.get(0)).getInt("shop_id");
        shopName = ((JSONObject) shops.get(0)).getString("shop_name");
        shopTitle = ((JSONObject) shops.get(0)).getString("title");
        imageUrl = ((JSONObject) shops.get(0)).getString("icon_url_fullxfull");

        drawable = EtsyUtils.drawableFromUrl(context.getResources(), imageUrl);
      } catch (JSONException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    return shops.length();
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
