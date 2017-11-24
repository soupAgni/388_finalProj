package com.example.anastasia.fotinspiron_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseAnalytics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

public class FacebookLoginActivity extends AppCompatActivity {
LoginButton loginButton;
TextView textView;
CallbackManager callbackManager;
    EditText et_email;
    EditText et_password;
    EditText et_password_verify;
    Button btn_signup_or_login;
    TextView tv_signup_or_login;
    TextView tv_verifyPass;
    String text;
    Boolean valid = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        loginButton = (LoginButton)findViewById(R.id.login_button);
        textView = (TextView)findViewById(R.id.textViewLoginStatus);
        //Initilizations
        //All Edit texts
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password_verify = (EditText) findViewById(R.id.et_passVerify);


        //All buttons
        btn_signup_or_login = (Button) findViewById(R.id.btn_login);

        //All textViews
        tv_signup_or_login = (TextView) findViewById(R.id.tv_signup);
        tv_verifyPass = (TextView) findViewById(R.id.tv_verifyPass);

        //Initially login
        btn_signup_or_login.setText("Log In");
        et_password_verify.setVisibility(View.INVISIBLE);
        tv_verifyPass.setVisibility(View.INVISIBLE);
      /*SpannableString content = new SpannableString("Sign Up");
      content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
      text = content.toString();*/
        tv_signup_or_login.setText("Sign Up");

        //For the facebook login
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult) {
                textView.setText("Login successful "+loginResult.getAccessToken().getUserId());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("FacebookLoginActivity", response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                    }

                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email, password");
                request.setParameters(parameters);
                request.executeAsync();

                //sending info to parse
                ParseUser.logInInBackground((parameters.get("email").toString()), (parameters.get("idFacebook").toString()), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user != null){
                            Log.i("AppInfo", "Login successful");
                            Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG ).show();

                        }
                    }
                });

            }

            @Override
            public void onCancel() {
                textView.setText("Login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("onError");
                Log.v("LoginActivity", error.getCause().toString());
            }
        });

    }
    public void onClickTextView(View v){
        if(tv_signup_or_login.getText().equals("Sign Up")){
            Log.i("OnClickTV", "In if");
            btn_signup_or_login.setText("Sign Up!");
            et_password_verify.setVisibility(View.VISIBLE);
            tv_verifyPass.setVisibility(View.VISIBLE);

            /*SpannableString content = new SpannableString("Log In");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            text = content.toString();*/
            tv_signup_or_login.setText("Log In");
        }
        else{
            Log.i("OnClickTV", "In else");
            btn_signup_or_login.setText("Log In");
            et_password_verify.setVisibility(View.INVISIBLE);
            tv_verifyPass.setVisibility(View.INVISIBLE);
            /*SpannableString content = new SpannableString("Sign Up");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            text = content.toString();*/
            tv_signup_or_login.setText("Sign Up");
        }
    }
    public void onsignUp_or_login(View v){
        if(btn_signup_or_login.getText().toString().equals("Log In")){
            Log.i("onsignUp_or_login", "Check for credentials");

            ParseUser.logInInBackground(String.valueOf(et_email.getText()), String.valueOf(et_password.getText()), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user != null){
                        Log.i("AppInfo", "Login successful");
                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG ).show();

                    }
                }
            });


        }
        else{
            //valid();
            //if(valid()){

            //Log.i("onsignUp_or_login", "Add user to database");
            ParseUser user = new ParseUser();
            user.setUsername(String.valueOf(et_email.getText()));
            user.setPassword(String.valueOf(et_password.getText()));

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Log.i("AppInfo", "SignUp successful");
                    }
                    else{
                        Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG ).show();
                    }
                }

            });
            // }
            /*else {
                Toast.makeText(this, "Enter valid credentials!", Toast.LENGTH_LONG).show();
                Log.i("onsignUp_or_login", "Invalid user!");
            }*/
        }
    }

    public boolean valid(){
        if(et_password.getText().toString().equals(et_password_verify.getText().toString()) && et_password.getText() != null && et_email.getText() != null){
            valid = true;
        }
        return valid;
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private Bundle getFacebookData(JSONObject object){
        Bundle bundle = new Bundle();
        try{

            String id = object.getString("id");
            bundle.putString("idFacebook", id);
            if(object.has("email")){
                bundle.putString("email", object.getString("email"));
            }
        }
        catch (JSONException e){
            Log.d("FacebookLogin","Error parsing JSON");
        }
        return null;
    }

}

/* All the parse tests: Put in onCreate and see what each one does. This is a means to communicate with the parse dashboard.

    //checks to see if the Score class exists and puts score in that class or it will create a new Score
      /*ParseObject gameScore = new ParseObject("GameScore");
      gameScore.put("score", 2000);
      gameScore.put("playerName", "new user 3");
      gameScore.put("cheatMode", false);
      gameScore.saveInBackground(new SaveCallback() {
          public void done(ParseException e) {
              if (e == null) {
                  Log.i("Parse", "Save Succeeded");
              } else {
                  Log.i("Parse", "Save Failed");
              }
          }
      });*/
/*
      ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
      query.getInBackground("ruyGoU6vXD", new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject object, ParseException e) {
              if(e == null){
                  object.put("playerName", "This is cool");
                  object.saveInBackground();
                  Log.i("Parse", "Save Succeeded");
              }
              else{
                  Log.i("Parse", "Save Failed");
              }
          }
      });*/

    /* ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
      query.whereEqualTo("playerName", "Souparni Agnihotri");
      query.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> objects, ParseException e) {
              if(e == null){
                  Log.i("findInBg", "Retrieved" + objects.size() + " results");

                  for(ParseObject object : objects){
                      Log.i("FindInBg", String.valueOf(object.get("score")));
                  }
              }
          }
      });*/

      /*ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
      query.whereEqualTo("playerName", "Souparni Agnihotri");
      query.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> objects, ParseException e) {
              if(e == null){
                  for(ParseObject object: objects){
                      Object id = object.get("objectId");
                      object.put("score", 190);
                      object.saveInBackground();
                      Log.i("findInBg", "Saved");

                  }
              }
          }
      });*/