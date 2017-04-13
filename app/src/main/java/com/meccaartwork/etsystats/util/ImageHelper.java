package com.meccaartwork.etsystats.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.NoSuchElementException;

public class ImageHelper {
  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
        .getHeight(), Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);
    final float roundPx = pixels;

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
  }

  public static void scaleImage(ImageView view, Bitmap bitmap) throws NoSuchElementException {
    // Get current dimensions AND the desired bounding box
    int width = 0;

    try {
      width = bitmap.getWidth();
    } catch (NullPointerException e) {
      throw new NoSuchElementException("Can't find bitmap on given view/drawable");
    }

    int height = bitmap.getHeight();
    int bounding = dpToPx(view.getContext(), 100);
    Log.i("Test", "original width = " + Integer.toString(width));
    Log.i("Test", "original height = " + Integer.toString(height));
    Log.i("Test", "bounding = " + Integer.toString(bounding));

    // Determine how much to scale: the dimension requiring less scaling is
    // closer to the its side. This way the image always stays inside your
    // bounding box AND either x/y axis touches it.
    float xScale = ((float) bounding) / width;
    float yScale = ((float) bounding) / height;
    float scale = (xScale <= yScale) ? xScale : yScale;
    Log.i("Test", "xScale = " + Float.toString(xScale));
    Log.i("Test", "yScale = " + Float.toString(yScale));
    Log.i("Test", "scale = " + Float.toString(scale));

    // Create a matrix for the scaling and add the scaling data
    Matrix matrix = new Matrix();
    matrix.postScale(scale, scale);

    // Create a new bitmap and convert it to a format understood by the ImageView
    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    width = scaledBitmap.getWidth(); // re-use
    height = scaledBitmap.getHeight(); // re-use
    BitmapDrawable result = new BitmapDrawable(scaledBitmap);
    Log.i("Test", "scaled width = " + Integer.toString(width));
    Log.i("Test", "scaled height = " + Integer.toString(height));

    // Apply the scaled bitmap
    view.setImageDrawable(result);

    // Now change ImageView's dimensions to match the scaled image
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
    params.width = width;
    params.height = height;
    view.setLayoutParams(params);

    Log.i("Test", "done");
  }

  private static int dpToPx(Context context, int dp) {
    float density = context.getResources().getDisplayMetrics().density;
    return Math.round((float)dp * density);
  }
}