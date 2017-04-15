package com.meccaartwork.etsystats.listener;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.meccaartwork.etsystats.ListingOptions;

/**
 * Created by oskanaan on 13/04/17.
 */

public class PreferenceTextChangeListener implements TextWatcher {
  View editText;
  int index;
  String prefix;
  String listingId;

  public PreferenceTextChangeListener(View item, int index, String listingId, String prefix){
    this.editText = item;
    this.index = index;
    this.listingId = listingId;
    this.prefix = prefix;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    String preferenceId = prefix+listingId+index;
    PreferenceManager.getDefaultSharedPreferences(editText.getContext())
        .edit().putString(preferenceId, s.toString()).commit();
  }
}
