package com.meccaartwork.etsystats.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.meccaartwork.etsystats.ListingOptions;
import com.meccaartwork.etsystats.util.EtsyUtils;
import com.meccaartwork.etsystats.util.ImageHelper;
import com.meccaartwork.etsystats.util.ImageLoader;

import java.io.IOException;

/**
 * Created by oskanaan on 29/04/17.
 */

public class DrawableFromUrlAsyncTask extends NetworkEnabledAsyncTask {
  private BitmapDrawable drawable;
  private String listingId;
  private ImageView listingImage;
  private Context context;
  private String imageUrl;

  public DrawableFromUrlAsyncTask(Context context, String listingId, ImageView listingImage, String imageUrl) {
    super(context);
    this.context = context;
    this.listingId = listingId;
    this.listingImage = listingImage;
    this.imageUrl = imageUrl;
  }

  @Override
  protected Object doInBackground(Object[] objects) {
    return new ImageLoader(context).getBitmap(imageUrl);
  }

  @Override
  protected void onPostExecute(Object o) {
    Bitmap bitmap = ((Bitmap)o).copy(Bitmap.Config.ARGB_8888, true );

    bitmap = ImageHelper.getRoundedCornerBitmap(bitmap, 20);
    ImageHelper.scaleImage(listingImage, bitmap);

  }
}
