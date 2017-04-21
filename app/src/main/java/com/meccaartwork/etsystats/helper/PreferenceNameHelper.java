package com.meccaartwork.etsystats.helper;

import com.meccaartwork.etsystats.data.Constants;

/**
 * Created by oskanaan on 17/04/17.
 */

public class PreferenceNameHelper {

  public static String getSearchTermRankName(String listingId, int index){
    return Constants.SEARCH_TERM_RANK_PREFIX +"#"+ listingId +"#"+index;
  }

  public static String getSearchTermLastRefreshed(String listingId){
    return Constants.SEARCH_TERM_LAST_REFRESHED_PREFIX +"#"+ listingId ;
  }

  public static String getPreviousSearchTermRankName(String listingId, int index){
    return Constants.SEARCH_TERM_RANK_PREFIX +"PREV#"+ listingId +"#"+index;
  }

  public static String getSearchTermName(String listingId, int index){
    return Constants.SEARCH_TERM_PREFIX +"#"+ listingId +"#"+index;
  }

  public static String getPeriodPrefixName(String listingId){
    return Constants.REFRESH_PERIOD_PREFIX +"#"+ listingId;
  }

  public static String getFavouriteName(String listingId){
    return Constants.FAVOURITE_PREFIX +"#"+ listingId;
  }

}
