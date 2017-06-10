package com.meccaartwork.etsystats.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
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

/**
 * Created by oskanaan on 25/03/2016.
 */
public class CategoryAdapter extends SimpleAdapter {

  private Context ctx ;
  private ImageLoader imageLoader;
  JSONArray data;
  //Keep the result returned from the server intact
  JSONArray originalData;
  private String idColumnName;
  private String filter;

  public CategoryAdapter(Context context, JSONArray data, int resource, String[] from, int[] to, String idColumnName) {
    super(context, null, resource, from, to);
    this.data = data;
    this.originalData = data;
    this.ctx = context;
    imageLoader = new ImageLoader(context);
    this.idColumnName = idColumnName;
  }

  /**
   * Filter the original results using the filter param, this doesnt invoke a rest call.
   * @param filter
   */
  @TargetApi(Build.VERSION_CODES.KITKAT)
  public void filterData(String filter) throws JSONException {
    if(filter == null || filter.trim().length() == 0){
      this.data = originalData;
    } else {
      this.data = new JSONArray();
      for(int i=0 ; i<this.originalData.length() ; i++){
        JSONObject jsonObject = ((JSONObject) originalData.get(i));
        if(jsonObject.getString("title").toUpperCase().contains(filter.toUpperCase())){
          this.data.put(jsonObject);
        }
      }
    }

    notifyDataSetChanged();
  }


  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View vi=convertView;
    ViewHolder holder;
    if(convertView==null){
      LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      vi = inflater.inflate(R.layout.etsy_category, parent, false);
      holder=new ViewHolder();
      holder.text=(TextView)vi.findViewById(R.id.categoryTitle);;
      holder.count=(TextView)vi.findViewById(R.id.listingCount);
      vi.setTag(holder);
    }
    else{
      holder=(ViewHolder)vi.getTag();
    }
    try {
      JSONObject jsonObject = ((JSONObject) data.get(position));
      String title = jsonObject.getString("title");
      holder.text.setText(title.substring(0, Math.min(title.length(), 50)) + (title.length() > 50 ? "..." : ""));
      if(jsonObject.has("active_listing_count"))     {
        int activeCount = jsonObject.getInt("active_listing_count");
        holder.count.setText(" ("+activeCount+")");
      }
    } catch (JSONException e) {
      Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
    }


    return vi;
  }

  @Override
  public int getCount() {
    return data.length();
  }

  @Override
  public Object getItem(int position) {
    try {
      return data.get(position);
    } catch (JSONException e) {
      Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
    }
    return null;
  }

  @Override
  public long getItemId(int position) {
    try {
      return ((Integer)((JSONObject)data.get(position)).get(idColumnName)).longValue();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return -1;
  }

  public JSONArray getData() {
    return data;
  }

  public void setData(JSONArray data) {
    this.data = data;
  }

  public static class ViewHolder{
    public TextView text;
    public TextView count;
  }
}
