package com.example.anastasia.fotinspiron_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class UserProfile extends AppCompatActivity {
    private String email = "";
    private String preferredname = "";
    private  String phone = "";
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        textView1 = (TextView) findViewById(R.id.textView10);
        textView2 = (TextView) findViewById(R.id.textView12);
        textView3 = (TextView) findViewById(R.id.textView14);
        textView4 = (TextView) findViewById(R.id.textView16);

        Intent i = getIntent();
        String name = i.getStringExtra("username");
        setTitle(name + "'s Profile");

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String username = b.getString("username", "");
            textView1.setText(username);
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", username);
            query.getFirstInBackground( new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if(e == null){
                    // System.out.println(object);
                        if (object.containsKey("email")){
                            email = object.getString("email");
                        }
                        if (object.containsKey("preferredname")){
                            preferredname = object.getString("preferredname");
                        }
                        if (object.containsKey("phone")){
                            phone = object.getString("phone");

                        }
                        textView2.setText(email);
                        textView3.setText(preferredname);
                        textView4.setText(phone);
                    }
                    else{
                        Log.i("Parse", "Error!");
                    }
                }
            });


        }
    }
}