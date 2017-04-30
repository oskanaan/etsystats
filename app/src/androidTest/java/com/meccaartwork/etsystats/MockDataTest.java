package com.meccaartwork.etsystats;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.test.ApplicationTestCase;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.meccaartwork.etsystats.idling.ViewPagerIdlingResource;
import com.meccaartwork.etsystats.matcher.JsonObjectMatcher;
import com.meccaartwork.etsystats.util.EtsyApi;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MockDataTest extends ApplicationTestCase<Application> {
  private int rankChange = 0;
  ViewPagerIdlingResource idlingResource2;

  public MockDataTest() {
        super(Application.class);
    }

  @Rule
  public ActivityTestRule<Main> mActivityRule = new ActivityTestRule<>(Main.class);

  @Rule
  public ActivityTestRule<BackgroundLaunchJob> backgroundActivityRule = new ActivityTestRule<>(BackgroundLaunchJob.class, true, false);

  @Before
  public void mockAPI() {
    idlingResource2 = new ViewPagerIdlingResource((ViewPager) mActivityRule.getActivity().findViewById(R.id.pager), "SwipeRefresh");
    Espresso.registerIdlingResources(idlingResource2);

    EtsyApi etsyApi = Mockito.mock(EtsyApi.class);
    try {
      Mockito.when(etsyApi.getShopData(Mockito.any(Context.class), Mockito.anyString())).thenReturn(new JSONArray("[{\"shop_id\":11544691,\"shop_name\":\"MeccaArt\",\"user_id\":71121327,\"creation_tsz\":1439717694,\"title\":\"Mecca Art (meccaartwork.com)\",\"announcement\":\"Welcome to Mecca Art. Here you will find a range of Islamic art made out of wood. Visit our online store at www.meccaartwork.com\",\"currency_code\":\"NZD\",\"is_vacation\":false,\"vacation_message\":null,\"sale_message\":null,\"digital_sale_message\":null,\"last_updated_tsz\":1490641707,\"listing_active_count\":27,\"digital_listing_count\":0,\"login_name\":\"mecaart\",\"accepts_custom_requests\":true,\"policy_welcome\":null,\"policy_payment\":\"SECURE OPTIONS\\nEtsy keeps your payment information secure. Sellers don't receive your credit card information.\\n- Visa\\n- MasterCard\\n- American Express\\n- Discover\\n- PayPal\\n- Apple Pay\\n- SOFORT Bank Transfer\\n- iDEAL\\n- Etsy Gift Card\",\"policy_shipping\":\"PROCESSING TIME\\nThe time I need to prepare an order for shipping varies. For details, see individual items.\\n\\nESTIMATED SHIPPING TIME\\nNorth America: 6 - 10 business days\\nEurope: 6 - 10 business days\\nAustralia, New Zealand and Oceania: 6 - 10 business days\\nAsia Pacific: 6 - 10 business days\\nLatin America and the Caribbean: 6 - 10 business days\\nNorth Africa and the Middle East: 6 - 10 business days\\nSub-Saharan Africa: 6 - 10 business days\\n\\nI'll do my best to meet these shipping estimates, but cannot guarantee them. Actual delivery time will depend on the shipping method you choose.\\n\\nCUSTOMS AND DUTIES FEES\\nBuyers are responsible for any customs or duties fees that may apply. Sellers aren't responsible for delays due to customs\",\"policy_refunds\":\"I GLADLY ACCEPT RETURNS, EXCHANGES, AND CANCELLATIONS\\nJust contact me within: 14 days of delivery\\nShip returns back to me within: 30 days of delivery\\nRequest a cancellation within: 24 hours of purchase\\n\\nTHE FOLLOWING ITEMS CAN'T BE RETURNED\\nBecause of the nature of these items, unless they arrive damaged or defective, I can't accept returns for:\\n- Custom or personalized orders\\n\\nCONDITIONS OF RETURN\\nBuyers are responsible for return shipping costs.\\nIf the item is not returned in its original condition, the buyer is responsible for any loss in value.\\n\\nQUESTIONS ABOUT YOUR ORDER?\\nPlease contact me if you have any problems with your order.\",\"policy_additional\":\"I will only use your shipping and billing address, and contact information\\n- To communicate with you about your order\\n- To fulfill your order\\n- For legal reasons (like paying taxes)\",\"policy_seller_info\":\"\",\"policy_updated_tsz\":1443429271,\"policy_has_private_receipt_info\":false,\"vacation_autoreply\":null,\"url\":\"https:\\/\\/www.etsy.com\\/shop\\/MeccaArt?utm_source=lazyteammember&utm_medium=api&utm_campaign=api\",\"image_url_760x100\":\"https:\\/\\/img0.etsystatic.com\\/138\\/0\\/11544691\\/iusb_760x100.18241914_k06m.jpg\",\"num_favorers\":233,\"languages\":[\"en-US\"],\"upcoming_local_event_id\":null,\"icon_url_fullxfull\":\"https:\\/\\/img0.etsystatic.com\\/101\\/0\\/11544691\\/isla_fullxfull.16502030_l883jr19.jpg\",\"is_using_structured_policies\":true,\"has_onboarded_structured_policies\":true,\"has_unstructured_policies\":true,\"custom_shops_state\":3,\"include_dispute_form_link\":false}]"));
      Mockito.when(etsyApi.getShopCategories(Mockito.any(Context.class), Mockito.eq(11544691))).thenReturn(new JSONArray("[{\"shop_section_id\":18187175,\"title\":\"Pendants\",\"rank\":1,\"user_id\":71121327,\"active_listing_count\":0,\"error_messages\":[\"Access denied on association Images\"]},{\"shop_section_id\":18183292,\"title\":\"Cutting boards\",\"rank\":2,\"user_id\":71121327,\"active_listing_count\":0,\"error_messages\":[\"Access denied on association Images\"]},{\"shop_section_id\":20165692,\"title\":\"Keychain holders\",\"rank\":3,\"user_id\":71121327,\"active_listing_count\":1,\"error_messages\":[\"Access denied on association Images\"]}]"));
      Mockito.when(etsyApi.getCategoryListings(Mockito.any(Context.class), Mockito.eq(11544691), Mockito.eq(-1))).thenReturn(new JSONArray("[{\"listing_id\":249591837,\"category_id\":68892008,\"title\":\"Wooden clock - wooden wall art - islamic wall art\",\"creation_tsz\":1491727985,\"ending_tsz\":1502268785,\"original_creation_tsz\":1443351991,\"last_modified_tsz\":1491727985,\"price\":\"50.00\",\"currency_code\":\"NZD\",\"quantity\":2,\"tags\":[\"Wooden wall art\",\"wooden wall hanging\",\"wood wall hanging\",\"wall art\",\"Wooden islamic art\",\"Wooden clock\",\"islamic wall art\",\"islamic wall hanging\",\"islamic gifts\"],\"category_path\":[\"Housewares\",\"Home Decor\"],\"category_path_ids\":[69150425,68892008],\"materials\":[\"wood\",\"Rimu\"],\"shop_section_id\":null,\"featured_rank\":null,\"state_tsz\":1491727982,\"url\":\"https:\\/\\/www.etsy.com\\/listing\\/249591837\\/wooden-clock-wooden-wall-art-islamic?utm_source=lazyteammember&utm_medium=api&utm_campaign=api\",\"views\":273,\"num_favorers\":143,\"shipping_template_id\":null,\"processing_min\":3,\"processing_max\":5,\"who_made\":\"i_did\",\"is_supply\":\"false\",\"when_made\":\"made_to_order\",\"item_weight\":null,\"item_weight_units\":null,\"item_length\":null,\"item_width\":null,\"item_height\":null,\"item_dimensions_unit\":\"in\",\"is_private\":false,\"recipient\":null,\"occasion\":null,\"style\":null,\"non_taxable\":false,\"is_customizable\":false,\"is_digital\":false,\"file_data\":\"\",\"language\":\"en-US\",\"has_variations\":false,\"taxonomy_id\":1029,\"taxonomy_path\":[\"Home & Living\",\"Home Décor\",\"Wall Décor\",\"Wall Hangings\"],\"used_manufacturer\":false,\"Images\":[{\"listing_image_id\":840419465,\"hex_code\":\"000000\",\"red\":0,\"green\":0,\"blue\":0,\"hue\":0,\"saturation\":0,\"brightness\":0,\"is_black_and_white\":null,\"creation_tsz\":1443351991,\"listing_id\":249591837,\"rank\":1,\"url_75x75\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_75x75.840419465_zleb.jpg\",\"url_170x135\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_170x135.840419465_zleb.jpg\",\"url_570xN\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_570xN.840419465_zleb.jpg\",\"url_fullxfull\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_fullxfull.840419465_zleb.jpg\",\"full_height\":683,\"full_width\":1024}]},{\"listing_id\":349591837,\"category_id\":68892008,\"title\":\"Wooden clock - wooden wall art - islamic wall art\",\"creation_tsz\":1491727985,\"ending_tsz\":1502268785,\"original_creation_tsz\":1443351991,\"last_modified_tsz\":1491727985,\"price\":\"50.00\",\"currency_code\":\"NZD\",\"quantity\":2,\"tags\":[\"Wooden wall art\",\"wooden wall hanging\",\"wood wall hanging\",\"wall art\",\"Wooden islamic art\",\"Wooden clock\",\"islamic wall art\",\"islamic wall hanging\",\"islamic gifts\"],\"category_path\":[\"Housewares\",\"Home Decor\"],\"category_path_ids\":[69150425,68892008],\"materials\":[\"wood\",\"Rimu\"],\"shop_section_id\":null,\"featured_rank\":null,\"state_tsz\":1491727982,\"url\":\"https:\\/\\/www.etsy.com\\/listing\\/249591837\\/wooden-clock-wooden-wall-art-islamic?utm_source=lazyteammember&utm_medium=api&utm_campaign=api\",\"views\":273,\"num_favorers\":143,\"shipping_template_id\":null,\"processing_min\":3,\"processing_max\":5,\"who_made\":\"i_did\",\"is_supply\":\"false\",\"when_made\":\"made_to_order\",\"item_weight\":null,\"item_weight_units\":null,\"item_length\":null,\"item_width\":null,\"item_height\":null,\"item_dimensions_unit\":\"in\",\"is_private\":false,\"recipient\":null,\"occasion\":null,\"style\":null,\"non_taxable\":false,\"is_customizable\":false,\"is_digital\":false,\"file_data\":\"\",\"language\":\"en-US\",\"has_variations\":false,\"taxonomy_id\":1029,\"taxonomy_path\":[\"Home & Living\",\"Home Décor\",\"Wall Décor\",\"Wall Hangings\"],\"used_manufacturer\":false,\"Images\":[{\"listing_image_id\":840419465,\"hex_code\":\"000000\",\"red\":0,\"green\":0,\"blue\":0,\"hue\":0,\"saturation\":0,\"brightness\":0,\"is_black_and_white\":null,\"creation_tsz\":1443351991,\"listing_id\":249591837,\"rank\":1,\"url_75x75\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_75x75.840419465_zleb.jpg\",\"url_170x135\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_170x135.840419465_zleb.jpg\",\"url_570xN\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_570xN.840419465_zleb.jpg\",\"url_fullxfull\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_fullxfull.840419465_zleb.jpg\",\"full_height\":683,\"full_width\":1024}]}]"));
      Mockito.when(etsyApi.getCategoryListings(Mockito.any(Context.class), Mockito.eq(11544691), Mockito.eq(-1))).thenReturn(new JSONArray("[{\"listing_id\":249591837,\"category_id\":68892008,\"title\":\"Wooden clock - wooden wall art - islamic wall art\",\"creation_tsz\":1491727985,\"ending_tsz\":1502268785,\"original_creation_tsz\":1443351991,\"last_modified_tsz\":1491727985,\"price\":\"50.00\",\"currency_code\":\"NZD\",\"quantity\":2,\"tags\":[\"Wooden wall art\",\"wooden wall hanging\",\"wood wall hanging\",\"wall art\",\"Wooden islamic art\",\"Wooden clock\",\"islamic wall art\",\"islamic wall hanging\",\"islamic gifts\"],\"category_path\":[\"Housewares\",\"Home Decor\"],\"category_path_ids\":[69150425,68892008],\"materials\":[\"wood\",\"Rimu\"],\"shop_section_id\":null,\"featured_rank\":null,\"state_tsz\":1491727982,\"url\":\"https:\\/\\/www.etsy.com\\/listing\\/249591837\\/wooden-clock-wooden-wall-art-islamic?utm_source=lazyteammember&utm_medium=api&utm_campaign=api\",\"views\":273,\"num_favorers\":143,\"shipping_template_id\":null,\"processing_min\":3,\"processing_max\":5,\"who_made\":\"i_did\",\"is_supply\":\"false\",\"when_made\":\"made_to_order\",\"item_weight\":null,\"item_weight_units\":null,\"item_length\":null,\"item_width\":null,\"item_height\":null,\"item_dimensions_unit\":\"in\",\"is_private\":false,\"recipient\":null,\"occasion\":null,\"style\":null,\"non_taxable\":false,\"is_customizable\":false,\"is_digital\":false,\"file_data\":\"\",\"language\":\"en-US\",\"has_variations\":false,\"taxonomy_id\":1029,\"taxonomy_path\":[\"Home & Living\",\"Home Décor\",\"Wall Décor\",\"Wall Hangings\"],\"used_manufacturer\":false,\"Images\":[{\"listing_image_id\":840419465,\"hex_code\":\"000000\",\"red\":0,\"green\":0,\"blue\":0,\"hue\":0,\"saturation\":0,\"brightness\":0,\"is_black_and_white\":null,\"creation_tsz\":1443351991,\"listing_id\":249591837,\"rank\":1,\"url_75x75\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_75x75.840419465_zleb.jpg\",\"url_170x135\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_170x135.840419465_zleb.jpg\",\"url_570xN\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_570xN.840419465_zleb.jpg\",\"url_fullxfull\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_fullxfull.840419465_zleb.jpg\",\"full_height\":683,\"full_width\":1024}]},{\"listing_id\":349591837,\"category_id\":68892008,\"title\":\"Wooden clock - wooden wall art - islamic wall art\",\"creation_tsz\":1491727985,\"ending_tsz\":1502268785,\"original_creation_tsz\":1443351991,\"last_modified_tsz\":1491727985,\"price\":\"50.00\",\"currency_code\":\"NZD\",\"quantity\":2,\"tags\":[\"Wooden wall art\",\"wooden wall hanging\",\"wood wall hanging\",\"wall art\",\"Wooden islamic art\",\"Wooden clock\",\"islamic wall art\",\"islamic wall hanging\",\"islamic gifts\"],\"category_path\":[\"Housewares\",\"Home Decor\"],\"category_path_ids\":[69150425,68892008],\"materials\":[\"wood\",\"Rimu\"],\"shop_section_id\":null,\"featured_rank\":null,\"state_tsz\":1491727982,\"url\":\"https:\\/\\/www.etsy.com\\/listing\\/249591837\\/wooden-clock-wooden-wall-art-islamic?utm_source=lazyteammember&utm_medium=api&utm_campaign=api\",\"views\":273,\"num_favorers\":143,\"shipping_template_id\":null,\"processing_min\":3,\"processing_max\":5,\"who_made\":\"i_did\",\"is_supply\":\"false\",\"when_made\":\"made_to_order\",\"item_weight\":null,\"item_weight_units\":null,\"item_length\":null,\"item_width\":null,\"item_height\":null,\"item_dimensions_unit\":\"in\",\"is_private\":false,\"recipient\":null,\"occasion\":null,\"style\":null,\"non_taxable\":false,\"is_customizable\":false,\"is_digital\":false,\"file_data\":\"\",\"language\":\"en-US\",\"has_variations\":false,\"taxonomy_id\":1029,\"taxonomy_path\":[\"Home & Living\",\"Home Décor\",\"Wall Décor\",\"Wall Hangings\"],\"used_manufacturer\":false,\"Images\":[{\"listing_image_id\":840419465,\"hex_code\":\"000000\",\"red\":0,\"green\":0,\"blue\":0,\"hue\":0,\"saturation\":0,\"brightness\":0,\"is_black_and_white\":null,\"creation_tsz\":1443351991,\"listing_id\":249591837,\"rank\":1,\"url_75x75\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_75x75.840419465_zleb.jpg\",\"url_170x135\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_170x135.840419465_zleb.jpg\",\"url_570xN\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_570xN.840419465_zleb.jpg\",\"url_fullxfull\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_fullxfull.840419465_zleb.jpg\",\"full_height\":683,\"full_width\":1024}]}]"));
      Mockito.when(etsyApi.getAllShopListings(Mockito.any(Context.class), Mockito.eq(11544691))).thenReturn(new JSONArray("[{\"listing_id\":249591837,\"category_id\":68892008,\"title\":\"Wooden clock - wooden wall art - islamic wall art\",\"creation_tsz\":1491727985,\"ending_tsz\":1502268785,\"original_creation_tsz\":1443351991,\"last_modified_tsz\":1491727985,\"price\":\"50.00\",\"currency_code\":\"NZD\",\"quantity\":2,\"tags\":[\"Wooden wall art\",\"wooden wall hanging\",\"wood wall hanging\",\"wall art\",\"Wooden islamic art\",\"Wooden clock\",\"islamic wall art\",\"islamic wall hanging\",\"islamic gifts\"],\"category_path\":[\"Housewares\",\"Home Decor\"],\"category_path_ids\":[69150425,68892008],\"materials\":[\"wood\",\"Rimu\"],\"shop_section_id\":null,\"featured_rank\":null,\"state_tsz\":1491727982,\"url\":\"https:\\/\\/www.etsy.com\\/listing\\/249591837\\/wooden-clock-wooden-wall-art-islamic?utm_source=lazyteammember&utm_medium=api&utm_campaign=api\",\"views\":273,\"num_favorers\":143,\"shipping_template_id\":null,\"processing_min\":3,\"processing_max\":5,\"who_made\":\"i_did\",\"is_supply\":\"false\",\"when_made\":\"made_to_order\",\"item_weight\":null,\"item_weight_units\":null,\"item_length\":null,\"item_width\":null,\"item_height\":null,\"item_dimensions_unit\":\"in\",\"is_private\":false,\"recipient\":null,\"occasion\":null,\"style\":null,\"non_taxable\":false,\"is_customizable\":false,\"is_digital\":false,\"file_data\":\"\",\"language\":\"en-US\",\"has_variations\":false,\"taxonomy_id\":1029,\"taxonomy_path\":[\"Home & Living\",\"Home Décor\",\"Wall Décor\",\"Wall Hangings\"],\"used_manufacturer\":false,\"Images\":[{\"listing_image_id\":840419465,\"hex_code\":\"000000\",\"red\":0,\"green\":0,\"blue\":0,\"hue\":0,\"saturation\":0,\"brightness\":0,\"is_black_and_white\":null,\"creation_tsz\":1443351991,\"listing_id\":249591837,\"rank\":1,\"url_75x75\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_75x75.840419465_zleb.jpg\",\"url_170x135\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_170x135.840419465_zleb.jpg\",\"url_570xN\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_570xN.840419465_zleb.jpg\",\"url_fullxfull\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_fullxfull.840419465_zleb.jpg\",\"full_height\":683,\"full_width\":1024}]},{\"listing_id\":349591837,\"category_id\":68892008,\"title\":\"Wooden clock - wooden wall art - islamic wall art\",\"creation_tsz\":1491727985,\"ending_tsz\":1502268785,\"original_creation_tsz\":1443351991,\"last_modified_tsz\":1491727985,\"price\":\"50.00\",\"currency_code\":\"NZD\",\"quantity\":2,\"tags\":[\"Wooden wall art\",\"wooden wall hanging\",\"wood wall hanging\",\"wall art\",\"Wooden islamic art\",\"Wooden clock\",\"islamic wall art\",\"islamic wall hanging\",\"islamic gifts\"],\"category_path\":[\"Housewares\",\"Home Decor\"],\"category_path_ids\":[69150425,68892008],\"materials\":[\"wood\",\"Rimu\"],\"shop_section_id\":null,\"featured_rank\":null,\"state_tsz\":1491727982,\"url\":\"https:\\/\\/www.etsy.com\\/listing\\/249591837\\/wooden-clock-wooden-wall-art-islamic?utm_source=lazyteammember&utm_medium=api&utm_campaign=api\",\"views\":273,\"num_favorers\":143,\"shipping_template_id\":null,\"processing_min\":3,\"processing_max\":5,\"who_made\":\"i_did\",\"is_supply\":\"false\",\"when_made\":\"made_to_order\",\"item_weight\":null,\"item_weight_units\":null,\"item_length\":null,\"item_width\":null,\"item_height\":null,\"item_dimensions_unit\":\"in\",\"is_private\":false,\"recipient\":null,\"occasion\":null,\"style\":null,\"non_taxable\":false,\"is_customizable\":false,\"is_digital\":false,\"file_data\":\"\",\"language\":\"en-US\",\"has_variations\":false,\"taxonomy_id\":1029,\"taxonomy_path\":[\"Home & Living\",\"Home Décor\",\"Wall Décor\",\"Wall Hangings\"],\"used_manufacturer\":false,\"Images\":[{\"listing_image_id\":840419465,\"hex_code\":\"000000\",\"red\":0,\"green\":0,\"blue\":0,\"hue\":0,\"saturation\":0,\"brightness\":0,\"is_black_and_white\":null,\"creation_tsz\":1443351991,\"listing_id\":249591837,\"rank\":1,\"url_75x75\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_75x75.840419465_zleb.jpg\",\"url_170x135\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_170x135.840419465_zleb.jpg\",\"url_570xN\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_570xN.840419465_zleb.jpg\",\"url_fullxfull\":\"https:\\/\\/img1.etsystatic.com\\/103\\/0\\/11544691\\/il_fullxfull.840419465_zleb.jpg\",\"full_height\":683,\"full_width\":1024}]}]"));
      Mockito.when(etsyApi.getListingRank(Mockito.any(Context.class), Mockito.eq("249591837"), Mockito.anyInt())).thenReturn(555);
      Mockito.when(etsyApi.getListingRank(Mockito.any(Context.class), Mockito.eq("349591837"), Mockito.anyInt())).thenReturn(11);
    } catch (JSONException e) {
      Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
    }
    EtsyApi.setInstance(etsyApi);
  }

  @After
  public void tearDown(){
    Espresso.registerIdlingResources(idlingResource2);
  }

  @Test
  public void testFavourite() throws InterruptedException {
    // Type text and then press the button.
    onView(withId(R.id.shopName)).perform(clearText(), typeText("MeccaArt"), ViewActions.pressImeActionButton(), closeSoftKeyboard());
    onView(withId(R.id.shopName)).check(matches(withText("MeccaArt")));
    onView(withId(R.id.pager)).perform(swipeLeft(), swipeLeft());
    onData(allOf(is(instanceOf(JSONObject.class)), new JsonObjectMatcher("title", "No category"))).inAdapterView(withId(R.id.shopCategories)).perform(click());
    onData(allOf(is(instanceOf(JSONObject.class)), new JsonObjectMatcher("listing_id", new Integer("249591837")))).inAdapterView(withId(R.id.categoryListings)).perform(click());
    onView(withId(R.id.favourite)).perform(click());
    pressBack();
    pressBack();
    onView(withId(R.id.pager)).perform(swipeRight());
    onView(withId(R.id.swiperefresh)).perform(swipeDown());
    onData(allOf(is(instanceOf(JSONObject.class)), new JsonObjectMatcher("listing_id", new Integer("249591837")))).inAdapterView(withId(R.id.quickAccess)).perform(click());
    onView(withId(R.id.favourite)).perform(click());
    pressBack();
    onView(withId(R.id.swiperefresh)).perform(swipeDown());
    onData(allOf(is(instanceOf(JSONObject.class)), new JsonObjectMatcher("listing_id", new Integer("249591837")))).inAdapterView(withId(R.id.quickAccess));
    onView(withId(R.id.quickAccess)).check(matches(new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(org.hamcrest.Description description) {

      }

      @Override
      public boolean matchesSafely(View view) {
        ListView listView = (ListView) view;

        return listView.getCount()==0;
      }

    }));
  }


  @Test
  public void rankTermChangeTest() throws InterruptedException {
    // Type text and then press the button.
    onView(withId(R.id.shopName)).perform(clearText(), typeText("MeccaArt"), ViewActions.pressImeActionButton(), closeSoftKeyboard());
    onView(withId(R.id.shopName)).check(matches(withText("MeccaArt")));
    onView(withId(R.id.pager)).perform(swipeLeft(), swipeLeft());
    onData(allOf(is(instanceOf(JSONObject.class)), new JsonObjectMatcher("title", "No category"))).inAdapterView(withId(R.id.shopCategories)).perform(click());
    onData(allOf(is(instanceOf(JSONObject.class)), new JsonObjectMatcher("listing_id", new Integer("249591837")))).inAdapterView(withId(R.id.categoryListings)).perform(click());
    onView(withTagValue(equalTo((Object)"searchTermTag1"))).perform(clearText(), typeText("Search Term"), closeSoftKeyboard());
    onView(withId(R.id.refreshResult)).perform(click());
    //rank change +
    Mockito.when(EtsyApi.getInstance().getListingRank(Mockito.any(Context.class), Mockito.matches("249591837"), Mockito.anyInt())).thenReturn(400);


    onView(withId(R.id.refreshResult)).perform(click());
    onView(withTagValue(equalTo((Object)"increaseImage1"))).check(ViewAssertions.matches(isDisplayed()));
    onView(withTagValue(equalTo((Object)"decreaseImage1"))).check(ViewAssertions.matches(not(isDisplayed())));

    //rank change
    Mockito.when(EtsyApi.getInstance().getListingRank(Mockito.any(Context.class), Mockito.matches("249591837"), Mockito.anyInt())).thenReturn(600);

    onView(withId(R.id.refreshResult)).perform(click());
    onView(withTagValue(equalTo((Object)"decreaseImage1"))).check(ViewAssertions.matches(isDisplayed()));
    onView(withTagValue(equalTo((Object)"increaseImage1"))).check(ViewAssertions.matches(not(isDisplayed())));

    onView(withTagValue(equalTo((Object)"searchTermTag1"))).perform(clearText(), typeText("Search Term2"), closeSoftKeyboard());
    onView(withTagValue(equalTo((Object)"decreaseImage1"))).check(ViewAssertions.matches(not(isDisplayed())));
    onView(withTagValue(equalTo((Object)"increaseImage1"))).check(ViewAssertions.matches(not(isDisplayed())));

  }

  @Test
  public void backgroundJobTest() throws InterruptedException {
    // Type text and then press the button.
    onView(withId(R.id.shopName)).perform(clearText(), typeText("MeccaArt"), ViewActions.pressImeActionButton(), closeSoftKeyboard());
    onView(withId(R.id.shopName)).check(matches(withText("MeccaArt")));
    onView(withId(R.id.pager)).perform(swipeLeft(), swipeLeft());
    onData(allOf(is(instanceOf(JSONObject.class)), new JsonObjectMatcher("title", "No category"))).inAdapterView(withId(R.id.shopCategories)).perform(click());
    onData(allOf(is(instanceOf(JSONObject.class)), new JsonObjectMatcher("listing_id", new Integer("249591837")))).inAdapterView(withId(R.id.categoryListings)).perform(click());
    onView(withTagValue(equalTo((Object)"searchTermTag1"))).perform(clearText(), typeText("Search Term"), closeSoftKeyboard());
    onView(withId(R.id.autoRefreshPeriod)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Every Day"))).perform(click());
    onView(withId(R.id.autoRefreshPeriod)).check(matches(withSpinnerText(containsString("Every Day"))));
    onView(withId(R.id.refreshResult)).perform(click());
    pressBack();
    onData(allOf(is(instanceOf(JSONObject.class)), new JsonObjectMatcher("listing_id", new Integer("349591837")))).inAdapterView(withId(R.id.categoryListings)).perform(click());
    onView(withTagValue(equalTo((Object)"searchTermTag1"))).perform(clearText(), typeText("Search Term3"), closeSoftKeyboard());
    onView(withId(R.id.autoRefreshPeriod)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Every Day"))).perform(click());
    onView(withId(R.id.autoRefreshPeriod)).check(matches(withSpinnerText(containsString("Every Day"))));
    onView(withId(R.id.refreshResult)).perform(click());
    pressBack();

    Mockito.when(EtsyApi.getInstance().getListingRank(Mockito.any(Context.class), Mockito.matches("249591837"), Mockito.anyInt())).thenReturn(699);
    Mockito.when(EtsyApi.getInstance().getListingRank(Mockito.any(Context.class), Mockito.matches("349591837"), Mockito.anyInt())).thenReturn(2);

    Intent intent = new Intent();
    backgroundActivityRule.launchActivity(intent);
    pressBack();
    pressBack();
    onView(withId(R.id.pager)).perform(swipeLeft());

    onView(withId(R.id.list)).check(matches(new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(org.hamcrest.Description description) {

      }

      @Override
      public boolean matchesSafely(View view) {
        RecyclerView listView = (RecyclerView) view;

        return listView.getChildCount()==2;
      }

    }));
  }

  public static ViewAction withCustomConstraints(final ViewAction action, final Matcher<View> constraints) {
    return new ViewAction() {
      @Override
      public Matcher<View> getConstraints() {
        return constraints;
      }

      @Override
      public String getDescription() {
        return action.getDescription();
      }

      @Override
      public void perform(UiController uiController, View view) {
        action.perform(uiController, view);
      }
    };
  }
}