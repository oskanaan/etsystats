package com.meccaartwork.etsystats;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.meccaartwork.etsystats.data.Constants;

public class ListingOptions extends AppCompatActivity {

  String listingId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle bundle = getIntent().getExtras();
    listingId = bundle.getString(Constants.LISTING_ID);

    setContentView(R.layout.activity_listing_options);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    EditText searchTerm1 = (EditText) findViewById(R.id.firstSearchTerm);
    EditText searchTerm2 = (EditText) findViewById(R.id.secondSearchTerm);
    EditText searchTerm3 = (EditText) findViewById(R.id.thirdSearchTerm);

    searchTerm1.addTextChangedListener(new PreferenceTextChangedListener(searchTerm1));
    searchTerm2.addTextChangedListener(new PreferenceTextChangedListener(searchTerm2));
    searchTerm3.addTextChangedListener(new PreferenceTextChangedListener(searchTerm3));

    searchTerm1.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("SearchTerm" + listingId + searchTerm1.getId(), ""));
    searchTerm2.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("SearchTerm"+listingId+searchTerm2.getId(), ""));
    searchTerm3.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("SearchTerm"+listingId+searchTerm3.getId(), ""));

    CheckBox favourite = (CheckBox)findViewById(R.id.favourite);
    favourite.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Favourite"+listingId, false));

    favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        PreferenceManager.getDefaultSharedPreferences(ListingOptions.this).edit().putBoolean("Favourite"+listingId, isChecked).commit();
      }
    });
  }

  private class PreferenceTextChangedListener implements TextWatcher{
    EditText editText;
    public PreferenceTextChangedListener(EditText item){
      this.editText = item;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      String text = s.toString();
      String preferenceId = "SearchTerm"+listingId+editText.getId();
      PreferenceManager.getDefaultSharedPreferences(ListingOptions.this)
          .edit().putString(preferenceId, s.toString()).commit();
    }
  }
}
