package com.example.anastasia.fotinspiron_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class updateDescription extends AppCompatActivity {

    EditText descripton;
    Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_description);

        descripton = findViewById(R.id.et_description);
        done = findViewById(R.id.btn_done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(updateDescription.this, UserList.class);
                startActivity(i);*/

                ParseObject object = new ParseObject("Images");
                object.put("description", descripton.getText().toString());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(getApplicationContext(), "Description updated!", Toast.LENGTH_SHORT).show();
                            Log.i("DescriptionUpdate", "Success");
                            Intent i = new Intent(updateDescription.this, UserList.class);
                            startActivity(i);

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Description update failed!", Toast.LENGTH_SHORT).show();
                            Log.i("DescriptionUpdate", "Failed");
                        }
                    }
                });
            }
        });
    }
}
