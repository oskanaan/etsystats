package com.meccaartwork.etsystats;

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

import com.meccaartwork.etsystats.async.GetShopDetailsAsyncTask;
import com.meccaartwork.etsystats.async.LoadShopIconAsyncTask;

public class Settings extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View root = inflater.inflate(R.layout.fragment_settings, container, false);
    final EditText shopName = (EditText) root.findViewById(R.id.shopName);
    final TextView shopTitleView = (TextView) root.findViewById(R.id.title);
    final ImageView imageView = (ImageView) root.findViewById(R.id.shopIcon);
    String shopNameFromPrefs = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("shop_name", null);
    String shopTitleFromPrefs = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("shop_title", null);
    shopName.setText(shopNameFromPrefs);
    shopTitleView.setText(shopTitleFromPrefs);
    new LoadShopIconAsyncTask(getContext(), imageView).execute();

    shopName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
          new GetShopDetailsAsyncTask(getContext(), shopName, shopTitleView, imageView).execute();
        }
        return true;
      }
    });
    return root;
  }


}
