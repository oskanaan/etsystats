package com.meccaartwork.etsystats;

import com.meccaartwork.etsystats.util.EtsyApi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class EtsyAPIUnitTests {

  static String TEST_SHOP_NAME = "MeccaArt";

  @Test
  public void testRetrieveShopName() throws Exception {
    JSONArray shops = EtsyApi.getShopData(TEST_SHOP_NAME);
    assertEquals(shops.length(), 1);
  }

  @Test
  public void testShopCategoriesAndListings() throws Exception {
    JSONArray shops = EtsyApi.getShopData(TEST_SHOP_NAME);
    int shopId = ((JSONObject)shops.get(0)).getInt("shop_id");
    JSONArray categories = EtsyApi.getShopCategories(shopId);
    for(int i=0 ; i<categories.length(); i++){
      JSONObject category = (JSONObject) categories.get(i);
      JSONArray listings = EtsyApi.getCategoryListings(shopId, category.getInt("shop_section_id"));

      for(int j=0; j<listings.length(); j++){
        JSONObject listing = (JSONObject) listings.get(j);
        assertEquals(listing.getInt("shop_section_id"), category.getInt("shop_section_id"));
      }
    }
  }
}