package com.meccaartwork.etsystats;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.meccaartwork.etsystats.data.Constants;
import com.meccaartwork.etsystats.util.EtsyApi;
import com.meccaartwork.etsystats.util.EtsyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupActionBar();
  }

  /**
   * Set up the {@link android.app.ActionBar}, if the API is available.
   */
  private void setupActionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      // Show the Up button in the action bar.
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }  /**
   * Helper method to determine if the device has an extra-large screen. For
   * example, 10" tablets are extra-large.
   */
  private static boolean isXLargeTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      if (!super.onMenuItemSelected(featureId, item)) {
        NavUtils.navigateUpFromSameTask(this);
      }
      return true;
    }
    return super.onMenuItemSelected(featureId, item);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean onIsMultiPane() {
    return isXLargeTablet(this);
  }  /**
   * A preference value change listener that updates the preference's summary
   * to reflect its new value.
   */
  private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
      String stringValue = value.toString();

      if (preference instanceof ListPreference) {
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list.
        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(stringValue);

        // Set the summary to reflect the new value.
        preference.setSummary(
            index >= 0
                ? listPreference.getEntries()[index]
                : null);

      }  else {
        // For all other preferences, set the summary to the value's
        // simple string representation.
        preference.setSummary(stringValue);
      }
      return true;
    }
  };

  /**
   * {@inheritDoc}
   */
  @Override
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.pref_headers, target);
  }  /**
   * Binds a preference's summary to its value. More specifically, when the
   * preference's value is changed, its summary (line of text below the
   * preference title) is updated to reflect the value. The summary is also
   * immediately updated upon calling this method. The exact display format is
   * dependent on the type of preference.
   *
   * @see #sBindPreferenceSummaryToValueListener
   */
  private static void bindPreferenceSummaryToValue(Preference preference) {
    // Set the listener to watch for value changes.
    preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

    // Trigger the listener immediately with the preference's
    // current value.
    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
        PreferenceManager
            .getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(), ""));
  }

  /**
   * This method stops fragment injection in malicious applications.
   * Make sure to deny any unknown fragments here.
   */
  protected boolean isValidFragment(String fragmentName) {
    return PreferenceFragment.class.getName().equals(fragmentName)
        || GeneralPreferenceFragment.class.getName().equals(fragmentName)
        || NotificationPreferenceFragment.class.getName().equals(fragmentName);
  }

  public static class GeneralPreferenceFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_my_shop);
      setHasOptionsMenu(true);

      // Bind the summaries of EditText/List/Dialog/Ringtone preferences
      // to their values. When their values change, their summaries are
      // updated to reflect the new value, per the Android Design
      // guidelines.
      Preference shopName = findPreference("shop_name");
      bindPreferenceSummaryToValue(shopName);

      shopName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
          try {
            return storeShopIdFromName(getActivity(), newValue.toString());
          } catch (IOException e) {
            e.printStackTrace();
          } catch (JSONException e) {
            e.printStackTrace();
          }
          return false;
        }
      });
//      bindPreferenceSummaryToValue(findPreference("example_list"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }
  }

  /**
   * This fragment shows notification preferences only. It is used when the
   * activity is showing a two-pane settings UI.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class NotificationPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_notification);
      setHasOptionsMenu(true);

      // Bind the summaries of EditText/List/Dialog/Ringtone preferences
      // to their values. When their values change, their summaries are
      // updated to reflect the new value, per the Android Design
      // guidelines.
      bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }
  }

  public static boolean storeShopIdFromName(final Context context, final String name) throws IOException, JSONException {
    new AsyncTask(){

      private int shopId;
      @Override
      protected Object doInBackground(Object[] params) {
        return EtsyApi.getShopData(name);
      }

      @Override
      protected void onPostExecute(Object o) {
        JSONArray listings = (JSONArray)o;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (listings.length() > 1){
          Toast.makeText(context, R.string.error_multiple_shops_with_similar_name, Toast.LENGTH_LONG).show();
          prefs.edit().putString("shop_name", null).commit();
          prefs.edit().putInt("shop_id", -1).commit();

        }
        else if(listings.length() == 0){
          Toast.makeText(context, R.string.error_no_shop_with_similar_name,Toast.LENGTH_LONG).show();
          prefs.edit().putString("shop_name", null).commit();
          prefs.edit().putInt("shop_id", -1).commit();
        }
        else{
          try {
            prefs.edit().putInt("shop_id", ((JSONObject) listings.get(0)).getInt("shop_id")).commit();
            shopId = ((JSONObject) listings.get(0)).getInt("shop_id");
            Toast.makeText(context, R.string.shop_found_and_id_saved, Toast.LENGTH_LONG).show();
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      }
    }.execute();


    return true;
  }
}
