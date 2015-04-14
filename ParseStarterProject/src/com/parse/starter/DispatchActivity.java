package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

/**
 * This Activity starts an intent for either a user that's
 * logged in (MainActivity) or logged out (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends Activity {

  public DispatchActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Check if there is current user info
    if (ParseUser.getCurrentUser() != null) {
      // Start an intent for the logged in activity
      startActivity(new Intent(this, LoggedInActivity.class));
    } else {
      // Start and intent for the logged out activity
      startActivity(new Intent(this, LoginActivity.class));
    }
  }

}
