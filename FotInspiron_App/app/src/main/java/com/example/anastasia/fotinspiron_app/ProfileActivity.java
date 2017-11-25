package com.example.anastasia.fotinspiron_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ProfileActivity extends AppCompatActivity {
 EditText editText1;
 EditText editText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        editText1 = (EditText)findViewById(R.id.textView3);
        editText2 = (EditText) findViewById(R.id.textView5);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            String email = bundle.getString("email","");
            String name = bundle.getString("name","");
            editText1.setText(name);
            editText2.setText(email);



        }
    }
}
