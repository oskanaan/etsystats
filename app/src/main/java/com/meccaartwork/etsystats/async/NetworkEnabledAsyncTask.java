package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.util.EtsyUtils;

/**
 * Created by oskanaan on 29/04/17.
 */

public abstract class NetworkEnabledAsyncTask extends AsyncTask{

  private Context context;

  public NetworkEnabledAsyncTask(Context context){
    this.context = context;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    if(!EtsyUtils.isInternetAvailable(context)){
      Toast.makeText(context, R.string.no_internet_connect, Toast.LENGTH_SHORT).show();
      cancel(true);
    }
  }
}
