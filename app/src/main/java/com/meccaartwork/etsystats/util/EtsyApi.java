package com.meccaartwork.etsystats.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oskanaan on 15/04/17.
 */

public class EtsyApi {
  public static JSONArray getShopData(String shopName){
    String url = "https://openapi.etsy.com/v2/shops?api_key="+ Constants.API_KEY+"&shop_name="+shopName;

    return EtsyUtils.getResultsFromUrl(url);
  }

  public static JSONObject getShopCategoriesObject(int shopId){
    String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections?api_key="+Constants.API_KEY+"&includes=Images:1";
    return EtsyUtils.getDataFromUrl(url);
  }

  public static JSONArray getShopCategories(int shopId){
    String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections?api_key="+Constants.API_KEY+"&includes=Images:1";
    return EtsyUtils.getResultsFromUrl(url);
  }

  public static JSONArray getShopListingsWithRankChanges(Context context, int shopId) throws JSONException {
    String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/listings/active?api_key="+ Constants.API_KEY+"&includes=Images:1";
    JSONArray jsonArray = EtsyUtils.getResultsFromUrl(url);
    JSONArray filtered = new JSONArray();
    for(int i=jsonArray.length()-1 ; i>=0 ; i--){
      for(int j=1 ; j< Constants.MAX_SEARCH_TERMS; j++){
        if(EtsyUtils.compareRankToPrevious(context, ((JSONObject)jsonArray.get(i)).getString("listing_id"), j) != 0){
          filtered.put(jsonArray.get(i));
          break;
        }
      }
    }

    return filtered;
  }

  public static JSONArray getCategoryListings(int shopId, int categoryId){
    JSONArray sections;
    if(categoryId == Constants.NO_CATEGORY){
      String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/listings/active?api_key="+Constants.API_KEY+"&includes=Images:1";
      JSONArray allListings = EtsyUtils.getResultsFromUrl(url);
      sections = new JSONArray();
      for(int i=0 ; i < allListings.length() ; i++){
        JSONObject jsonObject = null;
        try {
          jsonObject = ((JSONObject) allListings.get(i));
          if(jsonObject.get("shop_section_id") == JSONObject.NULL){
            sections.put(jsonObject);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    } else {
      String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections/"+categoryId+"/listings/active?api_key="+Constants.API_KEY+"&includes=Images:1";
      sections = EtsyUtils.getResultsFromUrl(url);
    }

    return sections;
  }

  public static int getListingRank(Context context, String listingId, int index){
    int selection = -1;
    try {

      boolean exit = false;
      int offset = 1;

      String term = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getSearchTermName(listingId, index), null);
      //Do nothing for empty terms.
      if(term == null){
        return selection;
      }
      while (!exit) {
        String url = "https://openapi.etsy.com/v2/listings/active?api_key="+ Constants.API_KEY+"&keywords=" + Uri.encode(term) + "&sort_on=score&limit=200&offset=" + offset;
        JSONArray listings = EtsyUtils.getResultsFromUrl(url);

        if(listings == null || listings.length() == 0){
          break;
        }

        for (int i = 0; i < listings.length(); i++) {
          JSONObject listing = (JSONObject) listings.get(i);

          if (listing.get("listing_id").equals(Integer.parseInt(listingId))) {
            exit = true;
            selection = i+1;
            break;
          }
        }

        offset += listings.length();
        if (exit || offset > 100 || listings.length() == 0) {
          break;
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return selection;
  }
}
