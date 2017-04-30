package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.util.EtsyUtils;
import com.meccaartwork.etsystats.util.ImageLoader;

import java.io.IOException;

/**
 * Created by oskanaan on 22/04/17.
 */

public class LoadShopIconAsyncTask extends NetworkEnabledAsyncTask {

  private ImageView shopIconView;
  private Context context;

  public LoadShopIconAsyncTask(Context context, ImageView shopIconView){
    super(context);
    this.context = context;
    this.shopIconView = shopIconView;
  }

  @Override
  protected Object doInBackground(Object[] params) {
    return new ImageLoader(context, 500).getDrawable(PreferenceManager.getDefaultSharedPreferences(context).getString("icon_url", null));
  }

  @Override
  protected void onPostExecute(Object o) {
    if(o != null){
      shopIconView.setImageDrawable((Drawable) o);
    } else {
      shopIconView.setImageResource(R.drawable.stub);
    }
  }
}
