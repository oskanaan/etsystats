package com.meccaartwork.etsystats;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Settings extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View root = inflater.inflate(R.layout.fragment_settings, container, false);
    EditText shopName = (EditText) root.findViewById(R.id.shopName);
    final TextView shopTitleView = (TextView) root.findViewById(R.id.title);
    final ImageView imageView = (ImageView) root.findViewById(R.id.shopIcon);
    String shopNameFromPrefs = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("shop_name", null);
    String shopTitleFromPrefs = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("shop_title", null);
    shopName.setText(shopNameFromPrefs);
    shopTitleView.setText(shopTitleFromPrefs);
    new AsyncTask(){

      @Override
      protected Object doInBackground(Object[] params) {
        try {
          return EtsyUtils.drawableFromUrl(getContext().getResources(), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("icon_url", null));
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      protected void onPostExecute(Object o) {
        if(o != null){
          imageView.setImageDrawable((Drawable) o);
        } else {
          imageView.setImageResource(R.drawable.stub);
        }
      }
    }.execute();

    shopName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
          new AsyncTask(){

            String shopNameText;
            private int shopId;
            private String shopName;
            private String shopTitle;
            private String imageUrl;
            private Drawable drawable;

            @Override
            protected void onPreExecute() {
              super.onPreExecute();
              shopNameText = v.getText().toString();
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

                  drawable = EtsyUtils.drawableFromUrl(getContext().getResources(), imageUrl);
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
              SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
              if (length != 1){
                prefs.edit().putString("shop_name", null).commit();
                prefs.edit().putString("shop_title", null).commit();
                prefs.edit().putString("icon_url", null).commit();
                prefs.edit().putInt("shop_id", -1).commit();

                v.setText("");
                shopTitleView.setText("");
                imageView.setImageResource(R.drawable.stub);

                if(length > 1){
                  Toast.makeText(getContext(), R.string.error_multiple_shops_with_similar_name, Toast.LENGTH_LONG).show();
                } else {
                  Toast.makeText(getContext(), R.string.error_no_shop_with_similar_name,Toast.LENGTH_LONG).show();
                }
                return;
              }
              else {
                prefs.edit().putString("shop_name", shopName).commit();
                prefs.edit().putString("shop_title", shopTitle).commit();
                prefs.edit().putString("icon_url", imageUrl).commit();
                prefs.edit().putInt("shop_id", shopId).commit();
                v.setText(shopName);
                shopTitleView.setText(shopTitle);
                if(drawable != null){
                  imageView.setImageDrawable(drawable);
                } else {
                  imageView.setImageResource(R.drawable.stub);
                }
                Toast.makeText(getContext(), R.string.shop_found_and_id_saved, Toast.LENGTH_LONG).show();

                return;
              }
            }
          }.execute();
        }
        return true;
      }
    });
    return root;
  }


}
