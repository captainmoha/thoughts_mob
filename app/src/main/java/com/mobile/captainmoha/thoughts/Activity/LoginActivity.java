package com.mobile.captainmoha.thoughts.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;

import android.os.AsyncTask;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.mobile.captainmoha.thoughts.Network.AndroidNetwork;
import com.mobile.captainmoha.thoughts.Network.HttpRest;
import com.mobile.captainmoha.thoughts.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private LoginTask mAuthTask = null;
    private static final String LOGIN_API_URI = "https://thoughty.herokuapp.com/login/";
    //    private static final String LOGIN_API_URI = "http://192.168.1.4:1880/loginMob";
    private String dataPath;

    // UI references.
    private EditText passwordText, emailText;
    private TextView statusText;
    private View mProgressView;
    private View mLoginFormView;

    private static String email;

    private Activity activity;

    static final Map<String,String> Data = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.activity = this;

        dataPath = activity.getApplicationContext().getExternalFilesDir("").toString();
        // Set up the login form.
        statusText = (TextView)findViewById(R.id.statusText);
        passwordText = (EditText) findViewById(R.id.password);
        emailText = (EditText) findViewById(R.id.email);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        statusText.setText("");

        // go directly to dashboard if user is logged in
        isLoggedIn();

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // to do
                attemptLogin();

                try {
                    Data.put("username" , emailText.getText().toString());
                    Data.put("password",passwordText.getText().toString() );

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button eEmailsRegisterButton = (Button) findViewById(R.id.email_register_button);
        eEmailsRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusText.setText("");
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        emailText.setError(null);
        passwordText.setError(null);

        // Store values at the time of the login attempt.
        email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordText.setError("Invalid Password");
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailText.setError("Required field");
            cancel = true;
        } else if (email.equals("")) {
            emailText.setError("Invalid Username");
            cancel = true;
        } else if (password.equals("")) {
            passwordText.setError("Invalid Password");
            cancel = true;
        }

        if (!cancel) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new LoginTask();
            mAuthTask.execute(LOGIN_API_URI);
        }
    }

   /* private boolean isEmailValid(String email) {
        return email.contains("@");
    }*/

    private boolean isPasswordValid(String password) {
        return password.length() >= 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.-
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class LoginTask extends AsyncTask <String, Void, String> {


        File device_Id;
        String [] response;
        boolean isNetworkAvailable;

        @Override
        protected String doInBackground(String... strings) {


            HttpRest Http = new HttpRest();
            AndroidNetwork Network = new AndroidNetwork(activity);

            isNetworkAvailable = Network.isNetworkAvailable();

            try {
                if (isNetworkAvailable)
                    response = Http.sendPost(strings[0], Data);
                else
                    return "Network Error";

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response != null) {
                // empty json then the user used a wrong combination
                if (response.length == 0) {
                    return "404";
                }
            }
            else {
                return "null";
            }

            return response[0];
        }

        @Override
        protected void onPostExecute(String s) {
            mAuthTask = null;
            showProgress(false);
            statusText.setText(s);
            device_Id = new File(LoginActivity.this.getApplicationContext().getExternalFilesDir(""),"device id");

            if (!isNetworkAvailable) {
                statusText.setText("Network not available");
            }
            else if(s.equals("Logged in!")) {

                try {
                    FileWriter fw = new FileWriter(device_Id);
                    fw.write(s);
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                statusText.setText("Logging in...");
                writeSession();
                finish();
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.putExtra("Email",emailText.getText().toString());
                startActivity(i);
            }

            else if (s.equals("Invalid request")) {
                statusText.setText("Server error");
            }
            else {
                statusText.setText("Incorrect Username or Password");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private void writeSession() {

            try {
                FileWriter fw = new FileWriter(dataPath + "/session.s");
                Log.d("session", "----"+ email);
                fw.write(email + "\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private boolean isLoggedIn() {

        if (new File(dataPath + "/session.s").exists()) {
            finish();
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            return true;
        }

        return false;
    }
}

