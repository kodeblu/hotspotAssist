package com.example.dell.hotspotguard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;


import android.util.Log;
import android.view.View;


import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import java.util.ArrayList;
import java.util.List;




/**
 * A login screen that offers login via email/password.
 */
public class HotSpotActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.

    private EditText yourName;
    private View mProgressView;
    private View hotSpotView;
    public  Button startBtn;
    public  Button stopBtn;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_spot);
        // Set up the login form.
        //mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //populateAutoComplete();

        yourName = (EditText) findViewById(R.id.name);
        yourName.setHint("Wifi Hotspot ID of choice");
        yourName.setHintTextColor(Color.WHITE);



        startBtn = (Button) findViewById(R.id.start_button);
        stopBtn = (Button) findViewById(R.id.stop_button);



        hotSpotView = findViewById(R.id.hot_spot_form);
        mProgressView = findViewById(R.id.save_progress);
    }



    public void startAlarm(View view){

        if(!beginAction()){
            manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            int interval = 10000;

            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        }

    }

    public void cancelAlarm(View view){
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();

    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean beginAction() {

        // Reset errors.

        yourName.setError(null);

        // Store values at the time of the login attempt.

        String wifi_id = yourName.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (wifi_id.isEmpty()) {
            yourName.setError(getString(R.string.error_field_required));
            focusView = yourName;
            cancel = true;
        }




        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //AlarmReceiver alarmIns = new AlarmReceiver();
            //Log.d("setting ssid","now");
            //alarmIns.setSSID(wifi_id);
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);



        }
        return cancel;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            hotSpotView.setVisibility(show ? View.GONE : View.VISIBLE);
            hotSpotView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    hotSpotView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            hotSpotView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        //addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }




    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {


        private final String mPassword;

        UserLoginTask(String password) {

            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");

            }

            // TODO: register the new account here.
            return true;
        }

        /*@Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                yourName.setError(getString(R.string.error_incorrect_password));
                yourName.requestFocus();
            }
        }*/

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

