package com.example.anastasia.fotinspiron_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UsersFeed extends AppCompatActivity {

   String username = "";
    LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_feed);

        ll = findViewById(R.id.linearlayout);
        Intent i = getIntent();
        username = i.getStringExtra("username");

        setTitle(username + "'s Feed");

        //get info for users feed from the parse database
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
        query.whereEqualTo("username", username);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject object : objects) {

                            //pointer to the file
                            ParseFile file = (ParseFile) object.get("images");
                            if (file != null) {
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            //convert the byte array to an image
                                            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);

                                            ImageView imageview = new ImageView(getApplicationContext());

                                            imageview.setImageBitmap(image);

                                            imageview.setLayoutParams(new ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                                            ));
                                            ll.addView(imageview);
                                        }
                                    }

                                });
                            }
                        }
                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_view, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profile){
            Intent main = new Intent(getApplicationContext(),UserProfile.class);
            main.putExtra("username", username);
            startActivity(main);
        }
        return super.onOptionsItemSelected(item);

    }


    }
