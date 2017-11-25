package com.example.anastasia.fotinspiron_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FacebookLoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    EditText et_email;
    EditText et_password;
    EditText et_password_verify;

    Button btn_signup_or_login;

    LoginButton loginButton;


    TextView tv_signup_or_login;
    TextView tv_verifyPass;
    TextView textView;

    ImageView icon;

    RelativeLayout relativeLayout;

    CallbackManager callbackManager;

    String text;
    Boolean valid = false;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());


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
        textView = (TextView)findViewById(R.id.textViewLoginStatus);


        //Initially login
        btn_signup_or_login.setText("Log In");
        et_password_verify.setVisibility(View.INVISIBLE);
        tv_verifyPass.setVisibility(View.INVISIBLE);

        //Login Button
        loginButton = (LoginButton)findViewById(R.id.login_button);

        //ImageView
        icon = (ImageView) findViewById(R.id.iv_icon);

        //Relative layout
        relativeLayout = (RelativeLayout) findViewById(R.id.rl);
      /*SpannableString content = new SpannableString("Sign Up");
      content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
      text = content.toString();*/


        //For the facebook login
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };

        accessTokenTracker.startTracking();
        //profileTracker.startTracking();

        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));

        tv_signup_or_login.setText("Sign Up");
        //For the facebook login
        callbackManager = CallbackManager.Factory.create();
        /*icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });*/
        icon.setOnClickListener(this);
        /*relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });*/
        relativeLayout.setOnClickListener(this);
        loginButton.setReadPermissions(Arrays.asList("email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult) {
                textView.setText("Login successful "+loginResult.getAccessToken().getUserId());

              //  final Profile profile = Profile.getCurrentProfile();
               // nextActivityProfile(profile);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("FacebookLoginActivity", response.toString());
                        // Get facebook data from login

                        Bundle bFacebookData = getFacebookData(object);

                        final String id = bFacebookData.getString("idFacebook","");
                        final String email = bFacebookData.getString("email","");
                        if (Profile.getCurrentProfile() == null){
                            profileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                                    profileTracker.stopTracking();
                                }
                            };

                        }else{
                            Profile profile = Profile.getCurrentProfile();
                            Intent main = new Intent(getApplicationContext(),ProfileActivity.class);
                            main.putExtra("name",profile.getName());
                            main.putExtra("email",email);
                            startActivity(main);


                        }



                       ParseUser.logInInBackground(email, id, new LogInCallback() {
                           @Override
                           public void done(ParseUser user, ParseException e) {
                               if(user != null){
                                   Log.i("AppInfo", "Login successful");
                                   //Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();

                               }

                               else {
                                   //create a new user
                                   ParseUser new_user = new ParseUser();
                                   new_user.setUsername(email);
                                   new_user.setPassword(id);
                                   new_user.signUpInBackground(new SignUpCallback() {
                                       @Override
                                       public void done(ParseException e) {
                                           if(e == null){
                                               Log.i("AppInfo", "SignUp successful");
                                           }
                                           else{
                                               System.out.println("Unable to create account id ");
                                             //  Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG ).show();
                                           }
                                       }

                                   });

                                   //Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG ).show();

                               }

                           }
                       });

                    }


                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                request.setParameters(parameters);
                request.executeAsync();

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
    @Override
    protected  void onResume(){
        super.onResume();
        Profile profile = Profile.getCurrentProfile();

        //nextActivity(profile);
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onStop(){
        super.onStop();
        accessTokenTracker.stopTracking();
      //  profileTracker.stopTracking();
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
                        Intent main = new Intent(getApplicationContext(),ProfileActivity.class);
                        main.putExtra("email",et_email.getText().toString());
                        main.putExtra("name","");
                        startActivity(main);
                        //Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
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
            System.out.println(id);
            bundle.putString("idFacebook", id);
            if(object.has("email")){

            String email = object.getString("email");
                bundle.putString("email",email);
                System.out.println(email);
            }
        }
        catch (JSONException e){
            Log.d("FacebookLogin","Error parsing JSON");
        }
        return bundle;
    }
/*
    private void nextActivity(Profile profile,String email){
        if (profile !=null){
            Intent main = new Intent(this,ProfileActivity.class);
            main.putExtra("name",profile.getName());
            main.putExtra("email",email);
            startActivity(main);

        }

    }
    */

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event){

       if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            onsignUp_or_login(v);
        }
        return false;
    }
    public void hideKeyboard(View view) {
        if (view.getId() == R.id.iv_icon || view.getId() == R.id.rl) {
            Log.i("InsideOnClick", "View id: ");

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View view) {

        Log.i("InsideOnClick","View id: " +view.getId());
        /*if(tv_signup_or_login.getText().equals("Sign Up")){
            Log.i("OnClickTV", "In if");
            btn_signup_or_login.setText("Sign Up!");
            et_password_verify.setVisibility(View.VISIBLE);
            tv_verifyPass.setVisibility(View.VISIBLE);

            *//*SpannableString content = new SpannableString("Log In");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            text = content.toString();*//*
            tv_signup_or_login.setText("Log In");
        }
        else if(tv_signup_or_login.getText().equals("Log In")){
            Log.i("OnClickTV", "In else");
            btn_signup_or_login.setText("Log In");
            et_password_verify.setVisibility(View.INVISIBLE);
            tv_verifyPass.setVisibility(View.INVISIBLE);
            *//*SpannableString content = new SpannableString("Sign Up");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            text = content.toString();*//*
            tv_signup_or_login.setText("Sign Up");
        }*/
       if(view.getId() == R.id.iv_icon || view.getId() == R.id.rl){
            Log.i("InsideOnClick","View id: " +view.getId() + "RLId: " +R.id.rl);
            //KeyboardUtils.
           view.postDelayed(new Runnable() {
               @Override
               public void run() {
                   InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                   imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(),0);
               }
           },50);

        }
      /*else if(view.getId() == R.id.iv_icon || view.getId() == R.id.rl){


            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(),0);

        }*/
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