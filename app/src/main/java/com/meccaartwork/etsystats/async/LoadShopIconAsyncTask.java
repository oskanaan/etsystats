package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.util.EtsyUtils;

import java.io.IOException;

/**
 * Created by oskanaan on 22/04/17.
 */

public class LoadShopIconAsyncTask extends AsyncTask{

  private ImageView shopIconView;
  private Context context;

  public LoadShopIconAsyncTask(Context context, ImageView shopIconView){
    this.context = context;
    this.shopIconView = shopIconView;
  }

  @Override
  protected Object doInBackground(Object[] params) {
    try {
      return EtsyUtils.drawableFromUrl(context.getResources(), PreferenceManager.getDefaultSharedPreferences(context).getString("icon_url", null));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
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
