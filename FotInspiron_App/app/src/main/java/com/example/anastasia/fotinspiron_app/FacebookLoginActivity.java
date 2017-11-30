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


    ImageView icon;

    RelativeLayout relativeLayout;

    CallbackManager callbackManager;

    String text;
    Boolean valid = false;

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


        //Initially login
        btn_signup_or_login.setText("Log In");
        et_password_verify.setVisibility(View.INVISIBLE);


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

        if(ParseUser.getCurrentUser() != null){
            DispUserList();
        }


        relativeLayout.setOnClickListener(this);
        loginButton.setReadPermissions(Arrays.asList("email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult) {


                Log.i("FBLogin", "login successful");

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("FacebookLoginActivity", response.toString());
                        // Get facebook data from login

                        Bundle bFacebookData = getFacebookData(object);

                        final String id = bFacebookData.getString("idFacebook","");
                        final String email = bFacebookData.getString("email","");
                        //DispUserList();
                       ParseUser.logInInBackground(email, id, new LogInCallback() {
                           @Override
                           public void done(ParseUser user, ParseException e) {
                               if(user != null){
                                   Log.i("AppInfo", "Login successful");


                                   Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();

                                   DispUserList();

                                //   DispUserList();
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
                                               Toast.makeText(getApplicationContext(),"Signup successful!", Toast.LENGTH_LONG ).show();

                                               DispUserList();

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
                Log.i("OnCancelFB", "Login cancelled");
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

    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onStop(){
        super.onStop();

    }
    public void onClickTextView(View v){
        if(tv_signup_or_login.getText().equals("Sign Up")){
            Log.i("OnClickTV", "In if");
            btn_signup_or_login.setText("Sign Up!");
            et_password_verify.setVisibility(View.VISIBLE);

            /*SpannableString content = new SpannableString("Log In");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            text = content.toString();*/
            tv_signup_or_login.setText("Log In");
        }
        else{
            Log.i("OnClickTV", "In else");
            btn_signup_or_login.setText("Log In");
            et_password_verify.setVisibility(View.INVISIBLE);
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

                        //Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();

                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG ).show();
                        DispUserList();


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
                        Toast.makeText(getApplicationContext(), "Signup Successful!", Toast.LENGTH_LONG).show();
                       DispUserList();

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


    //keyboard feature
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
    private void DispUserList(){
        Intent i = new Intent(getApplicationContext(), UserList.class);
        startActivity(i);
    }

    @Override
    public void onClick(View view) {

        Log.i("InsideOnClick","View id: " +view.getId());

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

    }
}


