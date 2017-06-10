package com.meccaartwork.etsystats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;
import com.meccaartwork.etsystats.util.EtsyUtils;
import com.meccaartwork.etsystats.util.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

  public class RankChangeRecyclerViewAdapter extends RecyclerView.Adapter<RankChangeRecyclerViewAdapter.ViewHolder> {

  private final JSONArray mValues;
  private final Context context;
  private final ImageLoader imageLoader;

  public RankChangeRecyclerViewAdapter(Context context, JSONArray items) {
    mValues = items;
    this.context = context;
    imageLoader = new ImageLoader(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_rankchange, parent, false);

    for(int i = Constants.MAX_SEARCH_TERMS+1; i< Constants.MAX_SEARCH_TERM_ALLOWED+1; i++){
      view.findViewWithTag("Index"+i).setVisibility(View.GONE);
    }

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    try {
      holder.mItem = (JSONObject)mValues.get(position);
      holder.listingId = holder.mItem.getString("listing_id");
      String title = holder.mItem.getString("title");
      if(title.length() > 70){
        title = title.substring(0, 69)+"...";
      }
      holder.listingTitle.setText(title);
      String url = ((JSONObject)((JSONArray)holder.mItem.get("Images")).get(0)).getString("url_75x75");
      holder.listingImage.setTag(url);
      imageLoader.displayImage(url, ((Activity) context), holder.listingImage);

      for(int i = 1; i< Constants.MAX_SEARCH_TERMS+1; i++){
        int rankComparison = EtsyUtils.compareRankToPrevious(context, holder.listingId, i);
        if(rankComparison != 0){
          View rankDisplay = holder.mView.findViewWithTag("Index"+i);
          if(rankComparison > 0){
            rankDisplay.findViewById(R.id.increase).setVisibility(View.VISIBLE);
          } else {
            rankDisplay.findViewById(R.id.decrease).setVisibility(View.VISIBLE);
          }
          String prevRank = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getPreviousSearchTermRankName(holder.listingId, i), "N/A");
          String currentRank = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getSearchTermRankName(holder.listingId, i), "N/A");
          ((TextView)rankDisplay.findViewById(R.id.rankFromTo)).setText(context.getString(R.string.result_from_to, prevRank, currentRank));
          String term = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getSearchTermName(holder.listingId, i), "N/A");
          ((TextView)rankDisplay.findViewById(R.id.searchTerm)).setText(term);
        } else {
          View rankDisplay = holder.mView.findViewWithTag("Index"+i);
          rankDisplay.setVisibility(View.GONE);
        }
      }

      holder.mView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Bundle bundle = new Bundle();
          try {
            bundle.putString(Constants.LISTING_ID, holder.mItem.getString("listing_id"));
            bundle.putString(Constants.LISTING_TITLE, holder.mItem.getString("title"));
            bundle.putString(Constants.LISTING_IMAGE_URL, ((JSONObject) ((JSONArray)holder.mItem.get("Images")).get(0)).getString("url_570xN"));
          } catch (JSONException e) {
            Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
          }

          Intent startListingOptions = new Intent();
          startListingOptions.putExtras(bundle);
          startListingOptions.setClassName("com.meccaartwork.etsystats", "com.meccaartwork.etsystats.ListingOptions");
          context.startActivity(startListingOptions);
        }
      });
//      holder.mContentView.setText(mValues.get(position).content);

//      holder.mView.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//          if (null != mListener) {
//            // Notify the active callbacks interface (the activity, if the
//            // fragment is attached to one) that an item has been selected.
//            mListener.onListFragmentInteraction(holder.mItem);
//          }
//        }
//      });
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  @Override
  public int getItemCount() {
    return mValues.length();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public String listingId;
    public final ImageView listingImage;
    public final TextView listingTitle;
    public final View mView;
    public JSONObject mItem;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      listingImage = (ImageView) view.findViewById(R.id.listingImage);
      listingTitle = (TextView) view.findViewById(R.id.listingTitle);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + listingTitle.getText() + "'";
    }
  }
}
