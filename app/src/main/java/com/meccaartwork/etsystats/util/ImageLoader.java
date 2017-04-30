package com.meccaartwork.etsystats.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.meccaartwork.etsystats.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by oskanaan on 25/03/2016.
 */
public class ImageLoader {

  //the simplest in-memory cache implementation. This should be replaced with something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
  private HashMap<String, Bitmap> cache=new HashMap<String, Bitmap>();

  private File cacheDir;
  private int requiredImageSize;

  final static int DEFAULT_IMAGE_SIZE = 70;

  public ImageLoader(Context context){
   this(context, DEFAULT_IMAGE_SIZE);
  }

  public ImageLoader(Context context, int requiredImageSize){
    this.requiredImageSize = requiredImageSize;
    //Make the background thead low priority. This way it will not affect the UI performance
    photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

    //Find the dir to save cached images
    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
      cacheDir=new File(context.getExternalCacheDir().getAbsoluteFile()+"/EtsyStats");
    else
      cacheDir=context.getCacheDir();
    if(!cacheDir.exists())
      cacheDir.mkdirs();
  }

  public void displayImage(String url, Activity activity, ImageView imageView)
  {

    if(url==null){
      imageView.setImageResource(R.drawable.stub);
      return;
    }

    if(cache.containsKey(url))
      imageView.setImageBitmap(cache.get(url));
    else
    {
      queuePhoto(url, imageView);
      imageView.setImageResource(R.drawable.stub);
    }
  }

  public Drawable getDrawable(String url)
  {

    if(url==null){
      return null;
    }

    if(cache.containsKey(url))
      return new BitmapDrawable(cache.get(url));
    else
    {
      //Dont queue
      return new BitmapDrawable(getBitmap(url));
    }
  }

  private void queuePhoto(String url, ImageView imageView)
  {
    //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them.
    photosQueue.Clean(imageView);
    PhotoToLoad p=new PhotoToLoad(url, imageView);
    synchronized(photosQueue.photosToLoad){
      photosQueue.photosToLoad.push(p);
      photosQueue.photosToLoad.notifyAll();
    }

    //start thread if it's not started yet
    if(photoLoaderThread.getState()==Thread.State.NEW)
      photoLoaderThread.start();
  }

  public Bitmap getBitmap(String url)
  {
    //I identify images by hashcode. Not a perfect solution, good for the demo.
    String filename=String.valueOf(url.hashCode());
    File f=new File(cacheDir, filename);

    //from SD cache
    Bitmap b = decodeFile(f);
    if(b!=null)
      return b;

    //from web
    try {
      Bitmap bitmap=null;
      InputStream is=new URL(url).openStream();
      OutputStream os = new FileOutputStream(f);
      CopyStream(is, os);
      os.close();
      bitmap = decodeFile(f);
      return bitmap;
    } catch (Exception ex){
      Log.e(this.getClass().getName(), "error : "+ex.getMessage(), ex);
      return null;
    }
  }

  //decodes image and scales it to reduce memory consumption
  private Bitmap decodeFile(File f){
    try {
      //decode image size
      BitmapFactory.Options o = new BitmapFactory.Options();
      o.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(new FileInputStream(f),null,o);

      //Find the correct scale value. It should be the power of 2.
      int width_tmp=o.outWidth, height_tmp=o.outHeight;
      int scale=1;
      while(true){
        if(width_tmp/2<requiredImageSize || height_tmp/2<requiredImageSize)
          break;
        width_tmp/=2;
        height_tmp/=2;
        scale++;
      }

      //decode with inSampleSize
      BitmapFactory.Options o2 = new BitmapFactory.Options();
      o2.inSampleSize=scale;
      return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    } catch (FileNotFoundException e) {}
    return null;
  }

  //Task for the queue
  private class PhotoToLoad
  {
    public String url;
    public ImageView imageView;
    public PhotoToLoad(String u, ImageView i){
      url=u;
      imageView=i;
    }
  }

  PhotosQueue photosQueue=new PhotosQueue();

  public void stopThread()
  {
    photoLoaderThread.interrupt();
  }

  //stores list of photos to download
  class PhotosQueue
  {
    private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();

    //removes all instances of this ImageView
    public void Clean(ImageView image)
    {

      for(int j=0 ;j<photosToLoad.size();){
        if(photosToLoad.get(j).imageView==image)
          photosToLoad.remove(j);
        else
          ++j;
      }
    }
  }

  class PhotosLoader extends Thread {
    public void run() {
      try {
        while(true)
        {
          //thread waits until there are any images to load in the queue
          if(photosQueue.photosToLoad.size()==0)
            synchronized(photosQueue.photosToLoad){
              photosQueue.photosToLoad.wait();
            }
          if(photosQueue.photosToLoad.size()!=0)
          {
            PhotoToLoad photoToLoad;
            synchronized(photosQueue.photosToLoad){
              photoToLoad=photosQueue.photosToLoad.pop();
            }
            Bitmap bmp=getBitmap(photoToLoad.url);
            bmp = ImageHelper.getRoundedCornerBitmap(bmp, 20);
            cache.put(photoToLoad.url, bmp);
            if(((String)photoToLoad.imageView.getTag()).equals(photoToLoad.url)){
              BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView);
              Activity a=(Activity)photoToLoad.imageView.getContext();
              a.runOnUiThread(bd);
            }
          }
          if(Thread.interrupted())
            break;
        }
      } catch (InterruptedException e) {
        //allow thread to exit
      }
    }
  }

  PhotosLoader photoLoaderThread=new PhotosLoader();

  //Used to display bitmap in the UI thread
  class BitmapDisplayer implements Runnable
  {
    Bitmap bitmap;
    ImageView imageView;
    public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b;imageView=i;}
    public void run()
    {
      if(bitmap!=null)
        imageView.setImageBitmap(bitmap);
      else
        imageView.setImageResource(R.drawable.stub);
    }
  }

  public void clearCache() {
    //clear memory cache
    cache.clear();

    //clear SD cache
    File[] files=cacheDir.listFiles();
    for(File f:files)
      f.delete();
  }

  public static void CopyStream(InputStream is, OutputStream os)
  {
    final int buffer_size=1024;
    try
    {
      byte[] bytes=new byte[buffer_size];
      for(;;)
      {
        int count=is.read(bytes, 0, buffer_size);
        if(count==-1)
          break;
        os.write(bytes, 0, count);
      }
    }
    catch(Exception ex){}
  }

}
