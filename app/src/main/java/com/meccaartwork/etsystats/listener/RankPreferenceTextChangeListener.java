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

public class RankPreferenceTextChangeListener implements TextWatcher {

  private ImageView increase;
  private ImageView decrease;
  private int index;
  private String listingId;
  private Context context;

  public RankPreferenceTextChangeListener(Context context, ImageView increase, ImageView decrease, String listingId, int index) {
    this.increase = increase;
    this.decrease = decrease;
    this.index = index;
    this.listingId = listingId;
    this.context = context;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferenceNameHelper.getPreviousSearchTermRankName(listingId, index), s.toString()).commit();
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    String preferenceId = PreferenceNameHelper.getSearchTermRankName(listingId, index);
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(preferenceId, s.toString()).commit();

    int rankChange = EtsyUtils.compareRankToPrevious(context, listingId, index);
    if(rankChange == -1){
      PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceNameHelper.getRankChangeIndicatorName(), true).commit();
      PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceNameHelper.getItemRankChangeDismissFlagName(listingId), false).commit();
      increase.setVisibility(View.GONE);
      decrease.setVisibility(View.VISIBLE);
    } else if(rankChange == 1){
      PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceNameHelper.getRankChangeIndicatorName(), true).commit();
      PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceNameHelper.getItemRankChangeDismissFlagName(listingId), false).commit();
      increase.setVisibility(View.VISIBLE);
      decrease.setVisibility(View.GONE);
    } else {
      increase.setVisibility(View.GONE);
      decrease.setVisibility(View.GONE);
    }
  }
}
