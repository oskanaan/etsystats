package com.meccaartwork.etsystats.listener;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meccaartwork.etsystats.ListingOptions;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;

/**
 * Created by oskanaan on 13/04/17.
 */

public class RankTermTextChangeListener implements TextWatcher {
  Context context;
  private String listingId;
  private int index;
  private TextView rankView;

  public RankTermTextChangeListener(Context context, TextView rankView, String listingId, int index){
    this.context = context;
    this.listingId = listingId;
    this.index = index;
    this.rankView = rankView;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    String preferenceId = PreferenceNameHelper.getSearchTermName(listingId, index);
    String previousTerm = PreferenceManager.getDefaultSharedPreferences(context).getString(preferenceId, "");
    if(!previousTerm.equals(s.toString())){
      //reset previous result since the search term has changed
      PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferenceNameHelper.getPreviousSearchTermRankName(listingId, index), null).commit();
      PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferenceNameHelper.getSearchTermRankName(listingId, index), null).commit();
      PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferenceNameHelper.getSearchTermLastRefreshed(listingId, index), null).commit();
      rankView.setText("");
    }
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(preferenceId, s.toString()).commit();
  }
}
