package com.meccaartwork.etsystats.listener;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.meccaartwork.etsystats.ListingOptions;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;

/**
 * Created by oskanaan on 13/04/17.
 */

public class PreferenceTextChangeListener implements TextWatcher {
  Context context;
  String preferenceId;

  public PreferenceTextChangeListener(Context context, String preferenceId){
    this.context = context;
    this.preferenceId = preferenceId;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(preferenceId, s.toString()).commit();
  }
}
