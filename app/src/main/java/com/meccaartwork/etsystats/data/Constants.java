package com.meccaartwork.etsystats.data;

/**
 * Created by oskanaan on 28/03/2016.
 */
public interface Constants {
  String API_KEY = "z5u6dzy42ve0vsdfyhhgrf98";

  String SECTION_ID = "$SECTION_ID";
  String LISTING_ID = "$LISTING_ID";
  String LISTING_TITLE = "$LISTING_TITLE";
  String LISTING_IMAGE_URL = "$LISTING_IMAGE_ID";

  int NO_CATEGORY = -1;
  int MAX_SEARCH_TERMS = 2;
  int MAX_SEARCH_TERM_ALLOWED = 5;

  //Settings prefixes
  String SEARCH_TERM_PREFIX = "SearchTerm";
  String SEARCH_TERM_RANK_PREFIX = "SearchTermRank";
  String RANK_CHANGE_DISMISS_FLAG = "DismissRankChange";
  String FAVOURITE_PREFIX = "Favourite";
  String REFRESH_PERIOD_PREFIX = "RefreshPeriod";
  String SEARCH_TERM_LAST_REFRESHED_PREFIX = "LastRefreshed";

  String FROM_NOTIFICATION = "FromNotification";
  int MAX_RESULTS_CHECK = 300;
  int BACKGROUND_JOB_RUN_HOURS = 24;
  String RANK_CHANGED = "RankChangedFlag";
  String FAVOURITES_CHANGED = "FavouritesChanged";
}
