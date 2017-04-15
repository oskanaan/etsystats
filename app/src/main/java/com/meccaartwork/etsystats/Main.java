package com.meccaartwork.etsystats;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Main extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button quickAccessButton = (Button)findViewById(R.id.quickAccessButton);
    Button categoriesButton = (Button)findViewById(R.id.categoriesButton);
    Button shopSettingsButton = (Button)findViewById(R.id.shopSettingsButton);

    quickAccessButton.setOnClickListener(new OpenActivityClickListener(QuickAccess.class.getName()));
    categoriesButton.setOnClickListener(new OpenActivityClickListener(ShopCategories.class.getName()));
    shopSettingsButton.setOnClickListener(new OpenActivityClickListener(SettingsActivity.class.getName()));
  }

  private class OpenActivityClickListener implements View.OnClickListener{
    String activity;

    public OpenActivityClickListener(String activity){
      this.activity = activity;
    }

    @Override
    public void onClick(View v) {
      Intent startListingOptions = new Intent();
      startListingOptions.setClassName("com.meccaartwork.etsystats", activity);
      startActivity(startListingOptions);
    }
  }

}
