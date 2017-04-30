package com.meccaartwork.etsystats;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.meccaartwork.etsystats.async.RefreshAllRanks;

public class BackgroundLaunchJob extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_background_launch_job);
    new RefreshAllRanks(getApplicationContext(), null, null).execute();
  }
}
