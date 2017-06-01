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
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    for(int i=1 ; i<Constants.MAX_SEARCH_TERMS+1 ; i++){
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

  public static void main(String [] a){
    String chypherText = "F96DE8C227A259C87EE1DA2AED57C93FE5DA36ED4EC87EF2C63AAE5B9A7EFFD673BE4ACF7BE8923CAB1ECE7AF2DA3DA44FCF7AE29235A24C963FF0DF3CA3599A70E5DA36BF1ECE77F8DC34BE129A6CF4D126BF5B9A7CFEDF3EB850D37CF0C63AA2509A76FF9227A55B9A6FE3D720A850D97AB1DD35ED5FCE6BF0D138A84CC931B1F121B44ECE70F6C032BD56C33FF9D320ED5CDF7AFF9226BE5BDE3FF7DD21ED56CF71F5C036A94D963FF8D473A351CE3FE5DA3CB84DDB71F5C17FED51DC3FE8D732BF4D963FF3C727ED4AC87EF5DB27A451D47EFD9230BF47CA6BFEC12ABE4ADF72E29224A84CDF3FF5D720A459D47AF59232A35A9A7AE7D33FB85FCE7AF5923AA31EDB3FF7D33ABF52C33FF0D673A551D93FFCD33DA35BC831B1F43CBF1EDF67F0DF23A15B963FE5DA36ED68D378F4DC36BF5B9A7AFFD121B44ECE76FEDC73BE5DD27AFCD773BA5FC93FE5DA3CB859D26BB1C63CED5CDF3FE2D730B84CDF3FF7DD21ED5ADF7CF0D636BE1EDB79E5D721ED57CE3FE6D320ED57D469F4DC27A85A963FF3C727ED49DF3FFFDD24ED55D470E69E73AC50DE3FE5DA3ABE1EDF67F4C030A44DDF3FF5D73EA250C96BE3D327A84D963FE5DA32B91ED36BB1D132A31ED87AB1D021A255DF71B1C436BF479A7AF0C13AA14794";
    char[] cypherArray = chypherText.toCharArray();
    List<St> letters = new ArrayList<>();



    for(int keyLength = 1; keyLength<20; keyLength++){
      Map<Integer, Integer> freqMap = new HashMap<>();
      for(int i = 0 ; i< cypherArray.length ; i+= keyLength){
        if(freqMap.get(new Integer(cypherArray[i])) != null){
          Integer freq = freqMap.get(new Integer(cypherArray[i]));
          freqMap.put(new Integer(cypherArray[i]), freq+1);
        } else {
          freqMap.put(new Integer(cypherArray[i]), 1);
        }
      }
      double summation = 0;
      for(Integer i : freqMap.keySet()){
        summation += freqMap.get(i);
      }
      Map<Integer, Integer> frequencies = new HashMap<>();
      double freqSum = 0;
      for(Integer i : freqMap.keySet()){
        freqSum += Math.pow(freqMap.get(i)/summation,2);
      }
//      System.out.println("Key length = "+keyLength+", summation = "+freqSum);


    }

    int KEY_LENGTH = 7;

    for(int keyIndex = 0; keyIndex<KEY_LENGTH;keyIndex++)
    for(int j = 32; j<=127; j++){
      Map<Integer, Integer> freqMap = new HashMap<>();
        breaktoouter:
        for(int i=keyIndex; i<cypherArray.length; i+=KEY_LENGTH){
          int xorResult = cypherArray[i]^j;
          if(xorResult<32 || xorResult>127){
            break breaktoouter;
          } else {
            if(freqMap.get(new Integer(xorResult)) != null){
              Integer freq = freqMap.get(new Integer(xorResult));
              freqMap.put(new Integer(xorResult), new Integer(freq+1));
            } else {
              freqMap.put(new Integer(xorResult), 1);
            }
          }
//          System.out.print(xorResult+" ");
        }
//      System.out.println();
      BigDecimal sumFreq = BigDecimal.ZERO;
      sumFreq.setScale(7, BigDecimal.ROUND_CEILING);
      for(Integer i : freqMap.keySet()){
        BigDecimal f = new BigDecimal(getFreq((char) i.intValue()));
        f.setScale(7, BigDecimal.ROUND_CEILING);
        BigDecimal v = new BigDecimal(1d*freqMap.get(i)/(cypherArray.length/KEY_LENGTH)).round(new MathContext(7, RoundingMode.HALF_UP));
        v.setScale(7, BigDecimal.ROUND_CEILING);
        sumFreq = sumFreq.add(f.multiply(v)).round(new MathContext(7, RoundingMode.HALF_UP));
      }
      System.out.println("letter = "+((char)j)+", keyIndex="+(keyIndex+1)+", freq = "+sumFreq);

    }
  }

  static double getFreq(char letter){
    if(letter == 'a')return	8.167d/100;
    if(letter == 'b')return	1.492d/100;
    if(letter == 'c')return	2.782d/100;
    if(letter == 'd')return	4.253d/100;
    if(letter == 'e')return	12.702d/100;
    if(letter == 'f')return	2.228d/100;
    if(letter == 'g')return	2.015d/100;
    if(letter == 'h')return	6.094d/100;
    if(letter == 'i')return	6.966d/100;
    if(letter == 'j')return	0.153d/100;
    if(letter == 'k')return	0.772d/100;
    if(letter == 'l')return	4.025d/100;
    if(letter == 'm')return	2.406d/100;
    if(letter == 'n')return	6.749d/100;
    if(letter == 'o')return	7.507d/100;
    if(letter == 'p')return	1.929d/100;
    if(letter == 'q')return	0.095d/100;
    if(letter == 'r')return	5.987d/100;
    if(letter == 's')return	6.327d/100;
    if(letter == 't')return	9.056d/100;
    if(letter == 'u')return	2.758d/100;
    if(letter == 'v')return	0.978d/100;
    if(letter == 'w')return	2.360d/100;
    if(letter == 'x')return	0.150d/100;
    if(letter == 'y')return	1.974d/100;
    if(letter == 'z')return	0.074d/100;
    return 0d;
  }

  public   class St{
    char letter;
    int freq;

    public St(char l, int f){
      this.letter = l;
      this.freq = f;
    }

    @Override
    public boolean equals(Object o) {
      return ((St)o).letter == letter;
    }
  }


}
