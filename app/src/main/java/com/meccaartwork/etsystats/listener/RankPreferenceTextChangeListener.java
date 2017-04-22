package com.meccaartwork.etsystats.listener;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.meccaartwork.etsystats.helper.PreferenceNameHelper;
import com.meccaartwork.etsystats.util.EtsyUtils;

/**
 * Created by oskanaan on 13/04/17.
 */

public class RankPreferenceTextChangeListener extends PreferenceTextChangeListener {

  private String prevPreferenceId;
  private ImageView increase;
  private ImageView decrease;
  private int index;
  private String listingId;

  public RankPreferenceTextChangeListener(Context context, String preferenceId, String prevPreferenceId, ImageView increase, ImageView decrease, String listingId, int index) {
    super(context, preferenceId);
    this.prevPreferenceId = prevPreferenceId;
    this.increase = increase;
    this.decrease = decrease;
    this.index = index;
    this.listingId = listingId;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    super.beforeTextChanged(s, start, count, after);
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(prevPreferenceId, s.toString()).commit();
  }

  @Override
  public void afterTextChanged(Editable s) {
    super.afterTextChanged(s);
    int rankChange = EtsyUtils.compareRankToPrevious(context,listingId, index);
    if(rankChange == -1){
      increase.setVisibility(View.GONE);
      decrease.setVisibility(View.VISIBLE);
    } else if(rankChange == 1){
      increase.setVisibility(View.VISIBLE);
      decrease.setVisibility(View.GONE);
    } else {
      increase.setVisibility(View.GONE);
      decrease.setVisibility(View.GONE);
    }
  }
}
