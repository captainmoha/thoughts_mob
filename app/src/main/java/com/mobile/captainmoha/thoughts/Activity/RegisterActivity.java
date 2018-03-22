package com.mobile.captainmoha.thoughts.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.mobile.captainmoha.thoughts.Network.AndroidNetwork;
import com.mobile.captainmoha.thoughts.Network.HttpRest;
import com.mobile.captainmoha.thoughts.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class RegisterActivity extends AppCompatActivity {

    private Activity activity;
    private static final String REGISTER_API_URI = "https://thoughty.herokuapp.com/register/";
//    private static final String REGISTER_API_URI = "http://192.168.1.4:1880/register/";

    EditText username, email, password, name;
    Button registerBtn ;
    ProgressBar progressBar;

    static final Map<String , String> FLAVORS = new HashMap<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.activity = this;

        username =  findViewById(R.id.username);
        email =  findViewById(R.id.email);
        password =  findViewById(R.id.password);

        name = findViewById(R.id.name);

        registerBtn = findViewById(R.id.registerBtn);

        progressBar= findViewById(R.id.PROGRESSBAR3);
        progressBar.setVisibility(View.INVISIBLE);


        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                if (isEmailValid(mail) && isPasswordValid(pass)) {
                    progressBar.setVisibility(View.VISIBLE);
                    try {

                        FLAVORS.put("username", username.getText().toString());
                        FLAVORS.put("password", pass);
                        FLAVORS.put("email", mail);
                        FLAVORS.put("name", name.getText().toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    SignupTask signupTask = new SignupTask();
                    signupTask.execute(REGISTER_API_URI);
                }

                else if (!isEmailValid(mail)) {
                    Toast.makeText(RegisterActivity.this.getApplicationContext(),
                            "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(RegisterActivity.this.getApplicationContext(),
                            "Please enter a valid password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class SignupTask extends AsyncTask <String, Void, String> {

        private boolean isNetworkAvailable;
        String[] response;
        HttpRest Http;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Http = new HttpRest();
        }

        @Override
        protected String doInBackground(String... strings) {

            AndroidNetwork Network = new AndroidNetwork(activity);
            isNetworkAvailable = Network.isNetworkAvailable();

            try {
                if (!isNetworkAvailable) {
                    return "Network is not available.";
                }

                else {
                    response = Http.sendPost(strings[0], FLAVORS);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }



            return (response != null) ? response[0] : "null";
        }

        @Override
        protected void onPostExecute(String s) {
            String result;
            progressBar.setVisibility(View.INVISIBLE);
            switch (s) {
                case "Welcome to thoughts, you're signed up :D":
                    result = "Welcome to Thoughts :)";
                    break;
                case "incomplete data":
                    result = "Please fill all fields to continue.";
                    break;
                case "already registered":
                    result = "already registered";
                    break;
                case "Invalid request":
                    result = "Something went wrong with our potatoes";
                    break;
                default:
                    result = "Network not available";
            }

            Log.d("test", result);
            Toast.makeText(activity.getApplicationContext(), result, Toast.LENGTH_LONG).show();

            if (result.equals("Welcome to Thoughts :)")) {
                Intent i = new Intent(activity, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }
}
