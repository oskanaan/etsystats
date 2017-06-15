package com.meccaartwork.etsystats.util;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oskanaan on 15/04/17.
 */

public class EtsyApi {

  String TAG = this.getClass().getSimpleName();
  static EtsyApi instance;

  public static EtsyApi getInstance(){
    if(instance == null){
      instance = new EtsyApi();
    }
    return instance;
  }

  public static void setInstance(EtsyApi pInstance){
    instance = pInstance;
  }

  public JSONArray getShopData(Context context, String shopName){
    String url = "https://openapi.etsy.com/v2/shops?api_key="+ Constants.API_KEY+"&shop_name="+shopName;

    return EtsyUtils.getAllResultsFromUrl(context, url);
  }

  public JSONObject getShopCategoriesObject(Context context, int shopId){
    String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections?api_key="+Constants.API_KEY+"&includes=Images:1";
    return EtsyUtils.getDataFromUrl(context, url);
  }

  public JSONArray getShopCategories(Context context, int shopId){
    String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections?api_key="+Constants.API_KEY;
    return EtsyUtils.getResultsFromUrl(context, url);
  }

  public JSONArray getAllShopListings(Context context, int shopId){
    Log.d(TAG, "Getting ALL shop listings");
    String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/listings/active?api_key="+ Constants.API_KEY+"&includes=Images:1";
    return EtsyUtils.getAllResultsFromUrl(context, url);
  }

  public JSONArray getCategoryListings(Context context, int shopId, int categoryId){
    JSONArray sections;
    if(categoryId == Constants.NO_CATEGORY){
      String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/listings/active?api_key="+Constants.API_KEY+"&includes=Images:1";
      JSONArray allListings = EtsyUtils.getAllResultsFromUrl(context, url);
      sections = new JSONArray();
      for(int i=0 ; i < allListings.length() ; i++){
        JSONObject jsonObject = null;
        try {
          jsonObject = ((JSONObject) allListings.get(i));
          if(jsonObject.get("shop_section_id") == JSONObject.NULL){
            sections.put(jsonObject);
          }
        } catch (JSONException e) {
          Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
        }
      }
    } else {
      String url = "https://openapi.etsy.com/v2/shops/"+shopId+"/sections/"+categoryId+"/listings/active?api_key="+Constants.API_KEY+"&includes=Images:1";
      sections = EtsyUtils.getAllResultsFromUrl(context, url);
    }

    return sections;
  }

  public int getListingRank(Context context, String listingId, int index){
    int selection = -1;
    try {

      boolean exit = false;
      int offset = 0;

      String term = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceNameHelper.getSearchTermName(listingId, index), null);
      //Do nothing for empty terms.
      if(term == null){
        return selection;
      }
      while (!exit) {
        String url = "https://openapi.etsy.com/v2/listings/active?api_key="+ Constants.API_KEY+"&keywords=" + Uri.encode(term) + "&sort_on=score&region=NZ&limit=100&offset=" + offset;
        JSONArray listings = EtsyUtils.getResultsFromUrl(context, url);

        if(listings == null || listings.length() == 0){
          selection = -1;
          break;
        }

        for (int i = 0; i < listings.length(); i++) {
          JSONObject listing = (JSONObject) listings.get(i);

          if (listing.get("listing_id").equals(Integer.parseInt(listingId))) {
            exit = true;
            selection = offset + i;
            break;
          }
        }

        offset += listings.length();
        if (exit){
          break;
        }

        if(offset > Constants.MAX_RESULTS_CHECK-1 || listings.length() == 0){
          selection = -1;
          break;
        }

      }

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return selection;
  }
}
