package com.example.anastasia.fotinspiron_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
 EditText editText1;
 EditText editText2;
 EditText editText3;
 EditText editText4;
 String username;
 String email = "";
 String preferredname = "";
 String phone = "";
 Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        editText1 = (EditText)findViewById(R.id.textView3);
        editText2 = (EditText) findViewById(R.id.textView5);
        editText3 = (EditText)findViewById(R.id.textView6);
        editText4 = (EditText)findViewById(R.id.textView8);



        button = (Button)findViewById(R.id.button2) ;

        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                username=   object.getString("username");
                System.out.println("username: "+username);
                if (object.containsKey("email")){
                    email = object.getString("email");

                }
                if (object.containsKey("preferredname")){
                    preferredname = object.getString("preferredname");
                }
                if (object.containsKey("phone")){
                    phone = object.getString("phone");

                }

                editText1.setText(username);
                editText2.setText(email);
                editText3.setText(preferredname);
                editText4.setText(phone);


            }
        });
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = editText2.getText().toString().trim();
                if (email.matches(emailPattern) && editable.length()> 0 ){
                    Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
                }

            }
        });
        editText4.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.getCurrentUser().put("username",editText1.getText().toString().replace("\n", "").replace("\r", ""));
                ParseUser.getCurrentUser().put("email",editText2.getText().toString().replace("\n", "").replace("\r", ""));
                ParseUser.getCurrentUser().put("preferredname",editText3.getText().toString().replace("\n", "").replace("\r", ""));
                ParseUser.getCurrentUser().put("phone",editText4.getText().toString().replace("\n", "").replace("\r", ""));

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Parse", "Save Succeeded");
                            Intent refresh = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(refresh);//Start the same Activity
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"Unable to update information",Toast.LENGTH_LONG).show();
                            Log.i("Parse", "Save Failed");
                        }
                    }
                });


            }
        });
    }
}
