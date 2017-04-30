package com.meccaartwork.etsystats;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.meccaartwork.etsystats.async.DrawableFromUrlAsyncTask;
import com.meccaartwork.etsystats.async.RetrieveRankAsyncTask;
import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;
import com.meccaartwork.etsystats.listener.RankTermTextChangeListener;
import com.meccaartwork.etsystats.listener.RankPreferenceTextChangeListener;
import com.meccaartwork.etsystats.util.EtsyUtils;
import com.meccaartwork.etsystats.util.ImageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListingOptions extends AppCompatActivity {

  String listingId;

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle bundle = getIntent().getExtras();
    listingId = bundle.getString(Constants.LISTING_ID);

    setContentView(R.layout.activity_listing_options);

    final String imageUrl = bundle.getString(Constants.LISTING_IMAGE_URL);
    final ImageView listingImage = (ImageView) findViewById(R.id.listingImage);
    listingImage.setBackgroundColor(Color.TRANSPARENT);

    final EditText listingTitle = (EditText) findViewById(R.id.listingTitle);
    String listingTitleText = bundle.getString(Constants.LISTING_TITLE);
    listingTitle.setText(listingTitleText.substring(0,Math.min(100, listingTitleText.length()))+(listingTitleText.length()>=100?"...":""));

    new DrawableFromUrlAsyncTask(getApplicationContext(), listingId, listingImage, imageUrl).execute();

    final List<View> parentViews = new ArrayList<>();
    LinearLayoutCompat layout = (LinearLayoutCompat)findViewById(R.id.rankDisplay);
    LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    for(int i=1 ; i<Constants.MAX_SEARCH_TERMS ; i++){
      View rankItemsParent = layoutInflater.inflate(R.layout.listing_rank_display, layout, false);
      parentViews.add(rankItemsParent);
      layout.addView(rankItemsParent, i-1 );
      final EditText searchTerm = (EditText) rankItemsParent.findViewById(R.id.searchTerm);
      searchTerm.setTag("searchTermTag"+i);
      final TextView searchTermRank = (TextView) rankItemsParent.findViewById(R.id.searchTermRank);
      searchTermRank.setTag("searchTermRank"+i);
      final ImageView increaseImage = (ImageView) rankItemsParent.findViewById(R.id.increase);
      increaseImage.setTag("increaseImage"+i);
      final ImageView decreaseImage = (ImageView) rankItemsParent.findViewById(R.id.decrease);
      decreaseImage.setTag("decreaseImage"+i);

      int rankChange = EtsyUtils.compareRankToPrevious(getApplicationContext(),listingId, i);
      if(rankChange == -1){
        increaseImage.setVisibility(View.GONE);
        decreaseImage.setVisibility(View.VISIBLE);
      } else if(rankChange == 1){
        increaseImage.setVisibility(View.VISIBLE);
        decreaseImage.setVisibility(View.GONE);
      } else {
        increaseImage.setVisibility(View.GONE);
        decreaseImage.setVisibility(View.GONE);
      }
      searchTerm.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(PreferenceNameHelper.getSearchTermName(listingId, i), ""));
      searchTermRank.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(PreferenceNameHelper.getSearchTermRankName(listingId, i) , ""));
      searchTerm.addTextChangedListener(new RankTermTextChangeListener(getApplicationContext(), searchTermRank, listingId, i));
      searchTermRank.addTextChangedListener(new RankPreferenceTextChangeListener(getApplicationContext(), increaseImage, decreaseImage, listingId, i));
    }

    findViewById(R.id.refreshResult).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        for(int i=0 ; i<parentViews.size(); i++){
          final EditText searchTerm = (EditText) parentViews.get(i).findViewById(R.id.searchTerm);
          final TextView searchTermRank = (TextView)parentViews.get(i).findViewById(R.id.searchTermRank);
          final ProgressBar progressBar = (ProgressBar)parentViews.get(i).findViewById(R.id.progress);
          if(searchTerm.getText().length() > 0){
            new RetrieveRankAsyncTask(searchTermRank, progressBar, listingId, i+1).execute();
          }
        }
      }
    });

    final Spinner spinner = (Spinner) findViewById(R.id.autoRefreshPeriod);
    final String preferenceId = PreferenceNameHelper.getPeriodPrefixName(listingId);

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        PreferenceManager.getDefaultSharedPreferences(view.getContext())
            .edit().putInt(preferenceId, position).commit();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        PreferenceManager.getDefaultSharedPreferences(spinner.getContext())
            .edit().putInt(preferenceId, 3).commit();
      }
    });

    int selectedRefreshPeriod = PreferenceManager.getDefaultSharedPreferences(spinner.getContext()).getInt(preferenceId, 3);
    spinner.setSelection(selectedRefreshPeriod);


    CheckBox favourite = (CheckBox)findViewById(R.id.favourite);
    favourite.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PreferenceNameHelper.getFavouriteName(listingId), false));

    favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        PreferenceManager.getDefaultSharedPreferences(ListingOptions.this)
            .edit()
            .putBoolean(PreferenceNameHelper.getFavouriteName(listingId), isChecked)
            .commit();
        PreferenceManager.getDefaultSharedPreferences(ListingOptions.this)
            .edit()
            .putBoolean(PreferenceNameHelper.getFavouriteChangeIndicatorName(), true)
            .commit();
      }
    });


    
  }

}
