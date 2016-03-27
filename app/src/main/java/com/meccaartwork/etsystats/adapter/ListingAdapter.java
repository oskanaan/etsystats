package com.meccaartwork.etsystats.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.meccaartwork.etsystats.R;
import com.meccaartwork.etsystats.util.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by oskanaan on 25/03/2016.
 */
public class ListingAdapter extends SimpleAdapter {

  private Context ctx ;
  private ImageLoader imageLoader;
  List<JSONObject> data;
  private String idColumnName;
  /**
   * Constructor
   *
   * @param context  The context where the View associated with this SimpleAdapter is running
   * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
   *                 Maps contain the data for each row, and should include all the entries specified in
   *                 "from"
   * @param resource Resource identifier of a view layout that defines the views for this list
   *                 item. The layout file should include at least those named views defined in "to"
   * @param from     A list of column names that will be added to the Map associated with each
   *                 item.
   * @param to       The views that should display column in the "from" parameter. These should all be
   *                 TextViews. The first N views in this list are given the values of the first N columns
   */
  public ListingAdapter(Context context, List<JSONObject> data, int resource, String[] from, int[] to, String idColumnName) {
    super(context, null, resource, from, to);
    this.data = data;
    this.ctx = context;
    imageLoader = new ImageLoader(context);
    this.idColumnName = idColumnName;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View vi=convertView;
    ViewHolder holder;
    if(convertView==null){
      LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      vi = inflater.inflate(R.layout.etsy_listing, parent, false);
      holder=new ViewHolder();
      holder.text=(TextView)vi.findViewById(R.id.listingTitle);;
      holder.image=(ImageView)vi.findViewById(R.id.listingImage);
      vi.setTag(holder);
    }
    else{
      holder=(ViewHolder)vi.getTag();
    }
    try {
      String title = data.get(position).get("title").toString();
      holder.text.setText(title.substring(0,Math.min(title.length()-1, 50))+"...");
      holder.image.setTag(((JSONObject) ((JSONArray) data.get(position).get("Images")).get(0)).getString("url_75x75"));
      imageLoader.displayImage(((JSONObject) ((JSONArray) data.get(position).get("Images")).get(0)).getString("url_75x75"), ((Activity) ctx), holder.image);
    } catch (JSONException e) {
      e.printStackTrace();
    }


    return vi;
  }

  @Override
  public int getCount() {
    return data.size();
  }

  @Override
  public Object getItem(int position) {
    return data.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    try {
      return ((Integer)data.get(position).get(idColumnName)).longValue();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return -1;
  }

  public List<JSONObject> getData() {
    return data;
  }

  public void setData(List<JSONObject> data) {
    this.data = data;
  }

  public static class ViewHolder{
    public TextView text;
    public ImageView image;
  }
}
