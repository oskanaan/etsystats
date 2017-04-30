package com.meccaartwork.etsystats.matcher;

import android.util.Log;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectMatcher extends TypeSafeMatcher<JSONObject> {
  private String key;
  private Object value;

  public JsonObjectMatcher(String key, Object value){
    this.key = key;
    this.value = value;
  }

  @Override
  public void describeTo(Description description) {
  }

  @Override
  protected boolean matchesSafely(JSONObject item) {
    try {
      if(item.get(key).equals(value)){
        return true;
      }
    } catch (JSONException e) {
      Log.e(this.getClass().getName(), "JSON error - Couldnt not retrieve values from json: "+e.getMessage());
    }
    return false;
  }
}
